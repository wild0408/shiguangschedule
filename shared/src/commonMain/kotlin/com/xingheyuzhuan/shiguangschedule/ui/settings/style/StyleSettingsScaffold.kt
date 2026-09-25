package com.xingheyuzhuan.shiguangschedule.ui.settings.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
expect fun MiuixStyleSettingsScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
)
