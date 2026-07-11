package com.jn.numrise.billing

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.jn.numrise.domain.repository.GameRepository
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
    private val repository: GameRepository
) : PurchasesUpdatedListener {

    private val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val _products = MutableStateFlow<List<BillingProduct>>(emptyList())
    val products: StateFlow<List<BillingProduct>> = _products.asStateFlow()

    init {
        startConnection()
    }

    private fun loadMockProducts() {
        val mockProducts = BillingConstants.PRODUCT_IDS.map { id ->
            BillingProduct(
                productId = id,
                price = BillingConstants.getPrice(id)
            )
        }
        _products.value = mockProducts
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                } else if (isDebug) {
                    loadMockProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                startConnection()
            }
        })
    }

    private fun queryProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                BillingConstants.PRODUCT_IDS.map { id ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, result ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val billingProducts = result.productDetailsList.map {
                    BillingProduct(
                        productId = it.productId,
                        price = it.oneTimePurchaseOfferDetails?.formattedPrice ?: "N/A",
                        productDetails = it
                    )
                }
                if (billingProducts.isNotEmpty()) {
                    _products.value = billingProducts
                } else if (isDebug) {
                    loadMockProducts()
                }
            } else if (isDebug) {
                loadMockProducts()
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, product: BillingProduct) {
        if (product.productDetails == null && isDebug) {
            handleMockPurchase(product.productId)
            return
        }

        product.productDetails?.let { productDetails ->
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
    }

    private fun handleMockPurchase(productId: String) {
        val amount = BillingConstants.getAmount(productId)
        if (amount > 0) {
            scope.launch {
                updateCoinsInDb(amount)
            }
        }
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
                    val productId = purchase.products.firstOrNull() ?: ""
                    val amount = BillingConstants.getAmount(productId)
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
            val currentStats = repository.getPlayerStats().first()
            val currentCoins = currentStats?.coins ?: 0
            repository.updateCoins(currentCoins + amount)
        }
    }
}
