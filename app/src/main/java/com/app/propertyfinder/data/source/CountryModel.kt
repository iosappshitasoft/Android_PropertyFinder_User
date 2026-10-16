package com.app.propertyfinder.data.source

data class CountryModel(
    val name: String,
    val code: String, // Dialing code e.g. +91
    val isoCode: String, // ISO code e.g. IN
    val flagRes: Int
)
