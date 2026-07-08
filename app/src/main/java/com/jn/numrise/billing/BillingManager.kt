package com.jn.numrise.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.jn.numrise.data.LevelDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillingManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val levelDao: LevelDao
) : PurchasesUpdatedListener {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    private val productIds = listOf(
        "coins_100", "coins_500", "coins_1000", "coins_1500",
        "coins_2000", "coins_2500", "coins_3000", "coins_3500", "coins_4000"
    )

    init {
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                startConnection()
            }
        })
    }

    private fun queryProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map { id ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, result ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _products.value = result.productDetailsList
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.consumeAsync(consumeParams) { billingResult, _ ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val productId = purchase.products.firstOrNull()
                    val amount = when (productId) {
                        "coins_100" -> 100
                        "coins_500" -> 500
                        "coins_1000" -> 1000
                        "coins_1500" -> 1500
                        "coins_2000" -> 2000
                        "coins_2500" -> 2500
                        "coins_3000" -> 3000
                        "coins_3500" -> 3500
                        "coins_4000" -> 4000
                        else -> 0
                    }
                    if (amount > 0) {
                        scope.launch {
                            updateCoinsInDb(amount)
                        }
                    }
                }
            }
        }
    }

    private suspend fun updateCoinsInDb(amount: Int) {
        withContext(Dispatchers.IO) {
            val currentStats = levelDao.getPlayerStats().first()
            val currentCoins = currentStats?.coins ?: 0
            levelDao.updateCoins(currentCoins + amount)
        }
    }
}
