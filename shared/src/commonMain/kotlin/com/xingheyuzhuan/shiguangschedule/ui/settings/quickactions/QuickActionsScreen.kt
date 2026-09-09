package com.xingheyuzhuan.shiguangschedule.ui.settings.quickactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.chevron_right_24px
import shiguangschedule.shared.generated.resources.desc_schedule_tweak
import shiguangschedule.shared.generated.resources.item_quick_actions
import shiguangschedule.shared.generated.resources.item_quick_delete
import shiguangschedule.shared.generated.resources.item_schedule_tweak
import shiguangschedule.shared.generated.resources.label_quick_action_category_schedule
import shiguangschedule.shared.generated.resources.quick_delete_subtitle

/**
 * 快捷操作二级页面
 * 用于收纳高频使用的工具类功能，如调课、临时改动等
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionsScreen(
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit
) {
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX
    val miuixScrollBehavior = if (useMiuix) top.yukonga.miuix.kmp.basic.MiuixScrollBehavior() else null
    Scaffold(
        modifier = if (useMiuix) Modifier.nestedScroll(miuixScrollBehavior!!.nestedScrollConnection) else Modifier,
        containerColor = if (useMiuix) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
        topBar = {
            if (useMiuix) top.yukonga.miuix.kmp.basic.TopAppBar(
                title = stringResource(Res.string.item_quick_actions),
                largeTitle = stringResource(Res.string.item_quick_actions),
                color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface,
                scrollBehavior = miuixScrollBehavior,
                navigationIcon = {
                    top.yukonga.miuix.kmp.basic.IconButton(onBack) {
                        top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.arrow_back_24px), stringResource(Res.string.a11y_back), tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurface)
                    }
                },
                defaultWindowInsetsPadding = true,
            ) else TopAppBar(
                title = { Text(stringResource(Res.string.item_quick_actions)) },
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            if (useMiuix) top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.label_quick_action_category_schedule), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title3, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
            else Text(text = stringResource(Res.string.label_quick_action_category_schedule), style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(8.dp))

            // 内容卡片
            if (useMiuix) top.yukonga.miuix.kmp.basic.Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = androidx.compose.foundation.layout.PaddingValues(0.dp),
                colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    MiuixQuickActionItem(stringResource(Res.string.item_schedule_tweak), stringResource(Res.string.desc_schedule_tweak)) { onNavigate(Destination.TweakSchedule) }
                    Spacer(Modifier.height(0.dp))
                    MiuixQuickActionItem(stringResource(Res.string.item_quick_delete), stringResource(Res.string.quick_delete_subtitle)) { onNavigate(Destination.QuickDelete) }
                }
            } else Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 调课功能项
                    QuickActionItem(
                        title = stringResource(Res.string.item_schedule_tweak),
                        subtitle = stringResource(Res.string.desc_schedule_tweak),
                        onClick = { onNavigate(Destination.TweakSchedule) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // 快速删除功能项
                    QuickActionItem(
                        title = stringResource(Res.string.item_quick_delete),
                        subtitle = stringResource(Res.string.quick_delete_subtitle),
                        onClick = { onNavigate(Destination.QuickDelete) }
                    )
                }
            }
        }
    }
}

/**
 * 快捷操作单项组件
 */
@Composable
private fun QuickActionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = vectorResource(Res.drawable.chevron_right_24px),
            contentDescription = null,
            modifier = Modifier.padding(start = 4.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MiuixQuickActionItem(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            top.yukonga.miuix.kmp.basic.Text(title, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body1, fontWeight = FontWeight.Medium)
            top.yukonga.miuix.kmp.basic.Text(subtitle, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
        }
        top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.chevron_right_24px), null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantActions)
    }
}
