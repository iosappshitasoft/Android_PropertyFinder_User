package com.app.propertyfinder.base


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.propertyfinder.helper.LocaleManager
import com.app.propertyfinder.helper.connectivity.ConnectionLiveData
import com.app.propertyfinder.helper.connectivity.NetworkStatus

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    val job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var connectionLiveData: ConnectionLiveData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        connectionLiveData = ConnectionLiveData(this)
        observeInternet()
    }

    private fun observeInternet() {
        connectionLiveData.observe(this) { connected ->
            val isConnected = connected == true
            NetworkStatus.isConnected = connected
            onNetworkStateChanged(isConnected)
        }
    }

    /** Back press handling (used with BackHandler in Compose) */
    open fun onBackPressedAction() {
        finish()
    }

    // 🌍 Locale support
    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            super.attachBaseContext(LocaleManager.setLocale(newBase))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    // 🌗 Prevent system font scaling issues
    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    // 🔔 Toast helper
    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.coroutineContext.cancelChildren()
    }


    /** Override in child activity */
    abstract fun onNetworkStateChanged(isConnected: Boolean)

}
