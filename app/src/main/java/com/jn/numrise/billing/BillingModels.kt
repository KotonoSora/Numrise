package com.jn.numrise.billing

import com.android.billingclient.api.ProductDetails

data class BillingProduct(
    val productId: String,
    val price: String,
    val productDetails: ProductDetails? = null
)
