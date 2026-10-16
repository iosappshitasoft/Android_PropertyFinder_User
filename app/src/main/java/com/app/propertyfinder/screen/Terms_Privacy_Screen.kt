package com.app.propertyfinder.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.TextPrimary

@Preview (showBackground = true, showSystemUi = true)
@Composable
fun TermsPrivacyScreenPreview(){
    val navController = rememberNavController()
    TermsPrivacyScreen(navController,"terms")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsPrivacyScreen(navController: NavController, fromScreen: String) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(
                        text = if (fromScreen == "terms") {
                            stringResource(R.string.terms_conditions)
                        } else {
                            stringResource(R.string.privacy_policy)
                        },
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .background(colorResource(R.color.screen_bg_color))
        ) {
            TermsWebViewScreen(fromScreen)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TermsWebViewScreen(from: String) {

    var isLoading by remember { mutableStateOf(true) }

    val pageUrl = remember(from) {
        when (from.lowercase()) {
            "privacy" -> "https://molumulk.tj/privacypolicy"
            else -> "https://molumulk.tj/terms"
        }
    }

    val hideHeaderFooterJs = remember {
        """
        (function() {
            var selectors = [
                '.pf-header',
                '.pf-header__container',
                'header',
                '.pf-footer',
                'footer',
                '.pf-breadcrumbs',
                '.pf-breadcrumb',
                '.pf-breadcrumbs__container',
                '.MuiBreadcrumbs-root'
            ];

            function hideAndReset() {
                // Hide unwanted elements
                selectors.forEach(function(selector) {
                    var elements = document.querySelectorAll(selector);
                    elements.forEach(function(el) {
                        el.style.setProperty('display', 'none', 'important');
                        el.style.setProperty('visibility', 'hidden', 'important');
                        el.style.setProperty('height', '0', 'important');
                        el.style.setProperty('min-height', '0', 'important');
                        el.style.setProperty('margin', '0', 'important');
                        el.style.setProperty('padding', '0', 'important');
                    });
                });

                // Remove extra top/bottom spacing from html/body
                [document.documentElement, document.body].forEach(function(el) {
                    if (el) {
                        el.style.setProperty('margin', '0', 'important');
                        el.style.setProperty('padding', '0', 'important');
                    }
                });

                // Main content wrapper
                var main =
                    document.querySelector('main') ||
                    document.querySelector('#root') ||
                    document.querySelector('#root > div') ||
                    document.querySelector('#root > div > div');

                if (main) {
                    main.style.setProperty('margin-top', '0', 'important');
                    main.style.setProperty('padding-top', '0', 'important');
                }

                // Remove suspicious spacer blocks near top
                var allElements = document.querySelectorAll('*');
                allElements.forEach(function(el) {
                    var style = window.getComputedStyle(el);
                    var paddingTop = parseInt(style.paddingTop) || 0;
                    var marginTop = parseInt(style.marginTop) || 0;
                    var rect = el.getBoundingClientRect();

                    if ((paddingTop > 50 || marginTop > 50) && rect.top < 200) {
                        el.style.setProperty('padding-top', '0', 'important');
                        el.style.setProperty('margin-top', '0', 'important');
                    }
                });
            }

            hideAndReset();

            if (!window.customHideObserver) {
                window.customHideObserver = new MutationObserver(function() {
                    hideAndReset();
                });
                window.customHideObserver.observe(document.body, {
                    childList: true,
                    subtree: true
                });
            }
        })();
        """.trimIndent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadsImagesAutomatically = true
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                    webChromeClient = WebChromeClient()

                    webViewClient = object : WebViewClient() {

                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            view?.evaluateJavascript(hideHeaderFooterJs, null)
                            isLoading = false
                        }
                    }

                    loadUrl(pageUrl)
                }
            },
            update = { webView ->
                webView.loadUrl(pageUrl)
            }
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun TopChipView(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) Color.Black else Color(0xFFEDEDED)
    ) {
        AppTextSemiBold(
            text = text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else TextPrimary,
            fontSize = 12.sp
        )
    }
}

