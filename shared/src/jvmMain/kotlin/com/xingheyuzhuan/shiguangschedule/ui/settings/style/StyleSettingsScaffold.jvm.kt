package com.xingheyuzhuan.shiguangschedule.ui.settings.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward

@Composable
actual fun MiuixStyleSettingsScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollBehavior = top.yukonga.miuix.kmp.basic.MiuixScrollBehavior()
    Scaffold(
        containerColor = MiuixTheme.colorScheme.surface,
        modifier = androidx.compose.ui.Modifier,
        topBar = {
            TopAppBar(
                title = title,
                largeTitle = title,
                color = MiuixTheme.colorScheme.surface,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onBack) {
                        Icon(MiuixIcons.ChevronBackward, contentDescription = null, tint = MiuixTheme.colorScheme.onSurface)
                    }
                },
            )
        },
        content = content,
    )
}
