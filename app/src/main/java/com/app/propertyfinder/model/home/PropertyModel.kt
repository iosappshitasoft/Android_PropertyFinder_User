package com.app.propertyfinder.model.home

data class PropertyModel(
    val tagType: String,
    val isShowHeart: Boolean,
    val isVerified: Boolean,
    val isSuperAgent: Boolean,
    val propertyType: String,
    val off_plan: String,
    val deliveryDate: String,
    val saleStarted: String,
    val paymentPlan: String,
    val title: String,
    val location: String,
    val price: String,
    val imageUrl: String,
    val listedDays: String,
    val isWhatsapp: Boolean,
    val bed: String,
    val bath: String,
    val sqft: String,
    val isShowPriceTxt: Boolean,
    val bottomAmenityOrientation: String,
)
