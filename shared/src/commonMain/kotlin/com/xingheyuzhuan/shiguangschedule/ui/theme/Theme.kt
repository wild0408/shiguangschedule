package com.xingheyuzhuan.shiguangschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.xingheyuzhuan.shiguangschedule.data.model.AppSettingsModel
import com.xingheyuzhuan.shiguangschedule.data.model.AppThemeMode
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import top.yukonga.miuix.kmp.theme.Colors as MiuixColors
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.lightColorScheme as miuixLightColorScheme
import top.yukonga.miuix.kmp.theme.darkColorScheme as miuixDarkColorScheme

/**
 * 定义一个用于全局同步深色模式状态的 Local 变量
 */
val LocalIsDarkTheme = staticCompositionLocalOf { false }
val LocalUiStyle = staticCompositionLocalOf { AppUiStyle.MIUIX }

/**
 * 外部调用的快捷主题函数
 * 自动根据 AppSettingsModel 处理所有主题逻辑
 */
@Composable
fun ShiguangScheduleTheme(
    settings: AppSettingsModel,
    content: @Composable () -> Unit
) {
    val darkTheme = when (settings.themeMode) {
        AppThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme, LocalUiStyle provides settings.uiStyle) {
        ShiguangScheduleTheme(
            darkTheme = darkTheme,
            dynamicColor = settings.useDynamicColor,
            customLightPrimary = Color(settings.customLightPrimary),
            customDarkPrimary = Color(settings.customDarkPrimary),
            themeMode = settings.themeMode,
            uiStyle = settings.uiStyle,
            content = content
        )
    }
}

/**
 * 核心主题实现函数（跨平台通用）
 */
@Composable
fun ShiguangScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    customLightPrimary: Color = DefaultThemeColor,
    customDarkPrimary: Color = DefaultThemeColor,
    themeMode: AppThemeMode = AppThemeMode.FOLLOW_SYSTEM,
    uiStyle: AppUiStyle = AppUiStyle.MIUIX,
    content: @Composable () -> Unit
) {
    val colorScheme = rememberColorScheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor && uiStyle == AppUiStyle.MATERIAL,
        customLightPrimary = if (uiStyle == AppUiStyle.MATERIAL) customLightPrimary else DefaultThemeColor,
        customDarkPrimary = if (uiStyle == AppUiStyle.MATERIAL) customDarkPrimary else DefaultThemeColor
    )

    // 应用平台特定的窗口与系统栏外观控制
    SetupPlatformThemeEffects(
        colorScheme = colorScheme,
        darkTheme = darkTheme,
        themeMode = themeMode
    )

    val miuixColors = if (uiStyle == AppUiStyle.MIUIX) {
        if (darkTheme) miuixDarkColorScheme() else miuixLightColorScheme()
    } else {
        rememberMiuixColors(colorScheme, darkTheme)
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography) {
        if (uiStyle == AppUiStyle.MIUIX) {
            MiuixTheme(colors = miuixColors, content = content)
        } else {
            content()
        }
    }
}

@Composable
private fun rememberMiuixColors(scheme: ColorScheme, dark: Boolean): MiuixColors {
    val base = if (dark) miuixDarkColorScheme() else miuixLightColorScheme()
    return remember(scheme, dark) {
        base.copy(
            primary = scheme.primary,
            onPrimary = scheme.onPrimary,
            primaryContainer = scheme.primaryContainer,
            onPrimaryContainer = scheme.onPrimaryContainer,
            secondary = scheme.secondary,
            onSecondary = scheme.onSecondary,
            secondaryContainer = scheme.secondaryContainer,
            onSecondaryContainer = scheme.onSecondaryContainer,
            error = scheme.error,
            onError = scheme.onError,
            errorContainer = scheme.errorContainer,
            onErrorContainer = scheme.onErrorContainer,
            background = scheme.background,
            onBackground = scheme.onBackground,
            surface = scheme.surface,
            onSurface = scheme.onSurface,
            surfaceVariant = scheme.surfaceVariant,
            outline = scheme.outline,
            dividerLine = scheme.outlineVariant,
        )
    }
}

/**
 * 共享的 MaterialKolor 动态配色方案生成函数
 */
@Composable
fun rememberMaterialKolorScheme(
    darkTheme: Boolean,
    seedColor: Color,
    style: PaletteStyle = PaletteStyle.TonalSpot
): ColorScheme {
    return rememberDynamicColorScheme(
        seedColor = seedColor,
        isDark = darkTheme,
        style = style
    )
}

/**
 * 平台特定的配色生成声明
 */
@Composable
expect fun rememberColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    customLightPrimary: Color,
    customDarkPrimary: Color
): ColorScheme

/**
 * 平台特定的窗口与系统栏外观控制声明
 */
@Composable
expect fun SetupPlatformThemeEffects(
    colorScheme: ColorScheme,
    darkTheme: Boolean,
    themeMode: AppThemeMode
)

/**
 * 平台特定的能力：当前系统/平台是否支持 Dynamic Color (动态取色)
 */
expect val supportsDynamicColor: Boolean