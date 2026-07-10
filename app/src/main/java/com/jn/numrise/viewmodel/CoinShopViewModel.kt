package com.jn.numrise.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.numrise.billing.BillingManager
import com.jn.numrise.domain.repository.GameRepository
import com.jn.numrise.domain.usecase.UpdatePlayerStatsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CoinShopViewModel(
    private val billingManager: BillingManager,
    private val repository: GameRepository,
    private val updatePlayerStatsUseCase: UpdatePlayerStatsUseCase
) : ViewModel() {

    val products = billingManager.products
    val playerStats = repository.getPlayerStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun buyPack(activity: Activity, productId: String) {
        val product = products.value.find { it.productId == productId }
        product?.let {
            billingManager.launchPurchaseFlow(activity, it)
        }
    }
}
