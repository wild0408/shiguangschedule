package com.xingheyuzhuan.shiguangschedule.ui.settings.style

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xingheyuzhuan.shiguangschedule.tool.FileManagerCallbacks
import com.xingheyuzhuan.shiguangschedule.tool.rememberFileManager
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.components.AdvancedColorPicker
import com.xingheyuzhuan.shiguangschedule.ui.components.ColorPickerConfig
import com.xingheyuzhuan.shiguangschedule.ui.components.ImageCropper
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.item_personalization
import shiguangschedule.shared.generated.resources.title_dark_color_pool
import shiguangschedule.shared.generated.resources.title_light_color_pool
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleSettingsScreen(
    onBack: () -> Unit,
    viewModel: StyleSettingsViewModel = koinViewModel()
) {
    val styleState by viewModel.styleState.collectAsStateWithLifecycle()
    val demoUiState by viewModel.demoUiState.collectAsStateWithLifecycle()

    val containerSize = LocalWindowInfo.current.containerSize
    val isLandscape = containerSize.width > containerSize.height

    var showColorPicker by remember { mutableStateOf(false) }
    var isDarkTarget by remember { mutableStateOf(false) }
    var selectedColorIndex by remember { mutableIntStateOf(0) }

    val sheetState = rememberModalBottomSheetState()

    var loadedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var showCropper by remember { mutableStateOf(false) }

    // 获取底部系统导航栏高度及 Card 背景颜色
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val cardContainerColor = CardDefaults.cardColors().containerColor
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX

    // 1. 对接全局统一的平台资源管理器
    val fileManager = rememberFileManager(
        callbacks = FileManagerCallbacks(
            onImagePicked = { bitmap ->
                if (bitmap != null) {
                    loadedBitmap = bitmap
                    showCropper = true
                }
            }
        )
    )

    // 2. 挂载裁切组件（内部自动判断当前平台是否需要展示裁切 UI 或静默处理）
    if (showCropper && loadedBitmap != null) {
        val screenAspectRatio = if (containerSize.height > 0) {
            containerSize.width.toFloat() / containerSize.height.toFloat()
        } else {
            1f
        }

        ImageCropper(
            imageBitmap = loadedBitmap,
            aspectRatio = screenAspectRatio,
            onCropConfirmed = { bytes ->
                // ImageCropper 已直接输出压缩好的 ByteArray，直接存入 ViewModel
                viewModel.saveCroppedWallpaper(bytes)
                showCropper = false
                loadedBitmap = null
            },
            onDismiss = {
                showCropper = false
                loadedBitmap = null
            }
        )
    }

    if (useMiuix) {
        val currentStyle = styleState
        if (currentStyle == null) {
            Box(Modifier.fillMaxSize().background(MiuixTheme.colorScheme.surface), contentAlignment = Alignment.Center) {
                top.yukonga.miuix.kmp.basic.CircularProgressIndicator()
            }
            return
        }
        val miuixScrollBehavior = top.yukonga.miuix.kmp.basic.MiuixScrollBehavior()
        val previewContent = @Composable { modifier: Modifier ->
            val density = LocalDensity.current
            val windowWidthDp = with(density) { containerSize.width.toDp() }
            Box(
                modifier = modifier
                    .background(MiuixTheme.colorScheme.surfaceContainerHigh)
                    .horizontalScroll(rememberScrollState())
                    .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
            ) {
                Box(Modifier.requiredWidth(windowWidthDp)) { ScheduleGridContent(currentStyle, demoUiState) }
            }
        }

        top.yukonga.miuix.kmp.basic.Scaffold(
            modifier = Modifier.nestedScroll(miuixScrollBehavior.nestedScrollConnection),
            containerColor = MiuixTheme.colorScheme.surface,
            topBar = {
                top.yukonga.miuix.kmp.basic.TopAppBar(
                    title = stringResource(Res.string.item_personalization),
                    largeTitle = stringResource(Res.string.item_personalization),
                    color = MiuixTheme.colorScheme.surface,
                    scrollBehavior = miuixScrollBehavior,
                    navigationIcon = {
                        top.yukonga.miuix.kmp.basic.IconButton(onBack) {
                            top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.arrow_back_24px), stringResource(Res.string.a11y_back), tint = MiuixTheme.colorScheme.onSurface)
                        }
                    },
                    defaultWindowInsetsPadding = true,
                )
            }
        ) { paddingValues ->
            val contentModifier = Modifier.padding(paddingValues).fillMaxSize()
            if (isLandscape) {
                Row(contentModifier) {
                    previewContent(Modifier.fillMaxHeight().weight(0.42f))
                    top.yukonga.miuix.kmp.basic.Card(
                        modifier = Modifier.fillMaxHeight().weight(0.58f).padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                        insideMargin = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    ) {
                        MiuixSettingsListContent(currentStyle, viewModel, { fileManager.pickImage() }) { dark, index ->
                            isDarkTarget = dark; selectedColorIndex = index; showColorPicker = true
                        }
                    }
                }
            } else {
                Column(contentModifier) {
                    previewContent(Modifier.fillMaxWidth().weight(0.38f))
                    top.yukonga.miuix.kmp.basic.Card(
                        modifier = Modifier.fillMaxWidth().weight(0.62f).padding(start = 12.dp, end = 12.dp, top = 12.dp),
                        insideMargin = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    ) {
                        MiuixSettingsListContent(currentStyle, viewModel, { fileManager.pickImage() }) { dark, index ->
                            isDarkTarget = dark; selectedColorIndex = index; showColorPicker = true
                        }
                    }
                }
            }

            if (showColorPicker) {
                val initialColor = currentStyle.courseColorMaps.getOrNull(selectedColorIndex)?.let { if (isDarkTarget) it.dark else it.light } ?: Color.Gray
                top.yukonga.miuix.kmp.overlay.OverlayDialog(
                    title = if (isDarkTarget) stringResource(Res.string.title_dark_color_pool) else stringResource(Res.string.title_light_color_pool),
                    show = true,
                    onDismissRequest = { showColorPicker = false },
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        AdvancedColorPicker(
                            initialColor = initialColor,
                            config = ColorPickerConfig(showAlpha = false),
                            onColorChanged = { viewModel.updatePrimaryColor(selectedColorIndex, it, isDarkTarget) },
                            previewContent = { ColorPreviewBox(initialColor, !isDarkTarget) }
                        )
                    }
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.item_personalization), style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(vectorResource(Res.drawable.arrow_back_24px), contentDescription = stringResource(Res.string.a11y_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        styleState?.let { currentStyle ->
            val contentModifier = Modifier.padding(paddingValues).fillMaxSize()

            val previewContent = @Composable { modifier: Modifier ->
                val density = LocalDensity.current
                val windowWidthDp = with(density) { containerSize.width.toDp() }
                Box(
                    modifier = modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .horizontalScroll(rememberScrollState())
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent()
                                }
                            }
                        }
                ) {
                    Box(modifier = Modifier.requiredWidth(windowWidthDp)) {
                        ScheduleGridContent(currentStyle, demoUiState)
                    }
                }
            }

            if (isLandscape) {
                Row(modifier = contentModifier) {
                    previewContent(Modifier.fillMaxHeight().weight(0.4f))
                    Card(
                        modifier = Modifier.fillMaxHeight().weight(0.6f),
                        shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                    ) {
                        SettingsListContent(
                            currentStyle = currentStyle,
                            viewModel = viewModel,
                            onWallpaperClick = {
                                fileManager.pickImage()
                            }
                        ) { isDark, idx ->
                            isDarkTarget = isDark
                            selectedColorIndex = idx
                            showColorPicker = true
                        }
                    }
                }
            } else {
                Column(modifier = contentModifier) {
                    previewContent(Modifier.fillMaxWidth().weight(0.45f))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.55f)
                            .drawWithContent {
                                drawContent()
                                val navBarPx = navigationBarHeight.toPx()
                                if (navBarPx > 0) {
                                    drawRect(
                                        color = cardContainerColor,
                                        topLeft = Offset(0f, size.height),
                                        size = Size(size.width, navBarPx)
                                    )
                                }
                            },
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        SettingsListContent(
                            currentStyle = currentStyle,
                            viewModel = viewModel,
                            onWallpaperClick = {
                                fileManager.pickImage()
                            }
                        ) { isDark, idx ->
                            isDarkTarget = isDark
                            selectedColorIndex = idx
                            showColorPicker = true
                        }
                    }
                }
            }

            if (showColorPicker) {
                ModalBottomSheet(onDismissRequest = { showColorPicker = false }, sheetState = sheetState) {
                    val initialColor = styleState?.courseColorMaps?.getOrNull(selectedColorIndex)?.let { pair ->
                        if (isDarkTarget) pair.dark else pair.light
                    } ?: Color.Gray

                    var currentColorInPicker by remember { mutableStateOf(initialColor) }

                    AdvancedColorPicker(
                        initialColor = initialColor,
                        config = ColorPickerConfig(showAlpha = false),
                        onColorChanged = { newColor ->
                            currentColorInPicker = newColor
                            viewModel.updatePrimaryColor(selectedColorIndex, newColor, isDarkTarget)
                        },
                        previewContent = {
                            ColorPreviewBox(currentColorInPicker, !isDarkTarget)
                        }
                    )
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    }
}
