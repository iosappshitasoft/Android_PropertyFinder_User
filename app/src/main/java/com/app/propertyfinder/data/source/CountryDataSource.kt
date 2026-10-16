package com.app.propertyfinder.data.source

import com.app.propertyfinder.R

object CountryDataSource {
    val countries = listOf(
        CountryModel("United Arab Emirates", "+971", "AE", R.drawable.login_logo),
        CountryModel("India", "+91", "IN", R.drawable.login_logo),
        CountryModel("United States", "+1", "US", R.drawable.login_logo),
        CountryModel("United Kingdom", "+44", "GB", R.drawable.login_logo),
        CountryModel("Saudi Arabia", "+966", "SA", R.drawable.login_logo),
        CountryModel("Oman", "+968", "OM", R.drawable.login_logo),
        CountryModel("Qatar", "+974", "QA", R.drawable.login_logo),
        CountryModel("Kuwait", "+965", "KW", R.drawable.login_logo),
        CountryModel("New Zealand", "+64", "NZ", R.drawable.login_logo),
        CountryModel("Afghanistan", "+93", "AF", R.drawable.login_logo),
        CountryModel("Albania", "+355", "AL", R.drawable.login_logo),
        CountryModel("Algeria", "+213", "DZ", R.drawable.login_logo),
        CountryModel("Andorra", "+376", "AD", R.drawable.login_logo),
        CountryModel("Angola", "+244", "AO", R.drawable.login_logo)
    ).sortedBy { it.name }
}
