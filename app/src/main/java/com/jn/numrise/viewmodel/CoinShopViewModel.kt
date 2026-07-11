package com.jn.numrise.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.numrise.billing.BillingConstants
import com.jn.numrise.billing.BillingManager
import com.jn.numrise.domain.model.CoinPack
import com.jn.numrise.domain.repository.GameRepository
import com.jn.numrise.domain.usecase.UpdatePlayerStatsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CoinShopViewModel(
    private val billingManager: BillingManager,
    private val repository: GameRepository,
    private val updatePlayerStatsUseCase: UpdatePlayerStatsUseCase
) : ViewModel() {

    val playerStats = repository.getPlayerStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val coinPacks = billingManager.products.map { products ->
        products.map { product ->
            CoinPack(
                id = product.productId,
                amount = BillingConstants.getAmount(product.productId),
                price = product.price,
                color = BillingConstants.getColor(product.productId),
                iconRes = BillingConstants.getIcon(product.productId),
                productId = product.productId
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun buyPack(activity: Activity, pack: CoinPack) {
        val product = billingManager.products.value.find { it.productId == pack.productId }
        product?.let {
            billingManager.launchPurchaseFlow(activity, it)
        }
    }
}
