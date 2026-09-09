package com.xingheyuzhuan.shiguangschedule.ui.settings.additional

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.item_language_settings
import shiguangschedule.shared.generated.resources.language_follow_system
import shiguangschedule.shared.generated.resources.language_names
import shiguangschedule.shared.generated.resources.language_tags
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 跨平台语言设置契约声明
 */
expect object PlatformLocaleManager {
    fun setLanguageTag(tag: String)
    fun getCurrentLanguageTag(): String
}

/**
 * 语言数据实体（仅限本文件内部解析使用）
 */
private data class LanguageItem(
    val name: String,
    val tag: String
)

/**
 * 独立语言设置页面组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingScreen(
    onBack: () -> Unit
) {
    // 获取底层平台当前生效的语言 Tag
    var currentTag by remember {
        mutableStateOf(PlatformLocaleManager.getCurrentLanguageTag())
    }

    // 动态读取 languages.xml 中的两个 string-array 资源并组装
    val tags = stringArrayResource(Res.array.language_tags)
    val names = stringArrayResource(Res.array.language_names)
    val followSystemText = stringResource(Res.string.language_follow_system)
    val scrollState = rememberScrollState()

    val languageList = remember(tags, names, followSystemText) {
        buildList {
            add(LanguageItem(followSystemText, ""))
            addAll(names.zip(tags) { name, tag -> LanguageItem(name, tag) })
        }
    }
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX

    if (useMiuix) {
        MiuixLanguageSettingScreen(
            onBack = onBack,
            languageList = languageList,
            currentTag = currentTag,
            onLanguageSelected = { tag ->
                currentTag = tag
                PlatformLocaleManager.setLanguageTag(tag)
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.item_language_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = stringResource(Res.string.a11y_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            languageList.forEach { item ->
                // 判断单选选中状态
                val isSelected = if (item.tag.isEmpty()) {
                    currentTag.isEmpty()
                } else {
                    currentTag.startsWith(item.tag)
                }

                ListItem(
                    modifier = Modifier.clickable {
                        if (!isSelected) {
                            currentTag = item.tag
                            // 平台方法更新系统/平台语言
                            PlatformLocaleManager.setLanguageTag(item.tag)
                        }
                    },
                    headlineContent = { Text(text = item.name) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    leadingContent = {
                        RadioButton(
                            selected = isSelected,
                            onClick = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun MiuixLanguageSettingScreen(
    onBack: () -> Unit,
    languageList: List<LanguageItem>,
    currentTag: String,
    onLanguageSelected: (String) -> Unit,
) {
    val scrollBehavior = top.yukonga.miuix.kmp.basic.MiuixScrollBehavior()
    val title = stringResource(Res.string.item_language_settings)

    top.yukonga.miuix.kmp.basic.Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MiuixTheme.colorScheme.surface,
        topBar = {
            top.yukonga.miuix.kmp.basic.TopAppBar(
                title = title,
                largeTitle = title,
                color = MiuixTheme.colorScheme.surface,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    top.yukonga.miuix.kmp.basic.IconButton(onBack) {
                        top.yukonga.miuix.kmp.basic.Icon(
                            vectorResource(Res.drawable.arrow_back_24px),
                            stringResource(Res.string.a11y_back),
                            tint = MiuixTheme.colorScheme.onSurface
                        )
                    }
                },
                defaultWindowInsetsPadding = true,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        ) {
            top.yukonga.miuix.kmp.basic.Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = PaddingValues(vertical = 4.dp),
            ) {
                languageList.forEach { item ->
                    val isSelected = if (item.tag.isEmpty()) currentTag.isEmpty() else currentTag.startsWith(item.tag)
                    top.yukonga.miuix.kmp.basic.BasicComponent(
                        modifier = Modifier.fillMaxWidth(),
                        title = item.name,
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
                        endActions = {
                            top.yukonga.miuix.kmp.basic.RadioButton(
                                selected = isSelected,
                                onClick = null,
                            )
                        },
                        onClick = {
                            if (!isSelected) onLanguageSelected(item.tag)
                        },
                    )
                }
            }
        }
    }
}
