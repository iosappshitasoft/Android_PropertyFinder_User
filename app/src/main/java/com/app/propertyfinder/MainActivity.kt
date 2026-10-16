package com.app.propertyfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.base.BaseActivity
import com.app.propertyfinder.navigation.SetupNavGraph
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent()
        }
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {

    }
}

@Composable
fun AppContent() {
    PropertyFinderTheme {
        val navController = rememberNavController()

        SetupNavGraph(navController = navController)
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {

    PropertyFinderTheme {

        Greeting("Android")
    }
}