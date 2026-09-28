package com.xingheyuzhuan.shiguangschedule.ui.miuix.screens

import androidx.compose.runtime.Composable
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.ui.schoolselection.web.WebViewScreen

/**
 * WebView is intentionally kept visually unchanged. The Android Miuix host
 * owns the destination, while the existing cross-platform WebView UI and
 * bridge remain the source of truth for browser/import behaviour.
 */
@Composable
internal fun MiuixWebViewScreen(
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit,
    initialUrl: String?,
    assetJsPath: String?,
) {
    WebViewScreen(
        onNavigate = onNavigate,
        onBack = onBack,
        initialUrl = initialUrl,
        assetJsPath = assetJsPath,
    )
}
