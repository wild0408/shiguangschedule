package com.xingheyuzhuan.shiguangschedule.ui.miuix.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.data.model.StartScreen
import com.xingheyuzhuan.shiguangschedule.tool.UpdateChecker
import com.xingheyuzhuan.shiguangschedule.tool.UpdatePlatform
import com.xingheyuzhuan.shiguangschedule.tool.UpdateStatus
import com.xingheyuzhuan.shiguangschedule.ui.components.LocalNavigationHostPadding
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.HyperLiquidTopBarButton
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.rememberSharedScrollBehavior
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.chrome.HyperGlassTopBar
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.utils.overScrollVertical
import com.xingheyuzhuan.shiguangschedule.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_confirm
import shiguangschedule.shared.generated.resources.app_name
import shiguangschedule.shared.generated.resources.btn_download_update
import shiguangschedule.shared.generated.resources.code_24px
import shiguangschedule.shared.generated.resources.dialog_checking_update
import shiguangschedule.shared.generated.resources.dialog_current_version_latest
import shiguangschedule.shared.generated.resources.dialog_new_version_found
import shiguangschedule.shared.generated.resources.dialog_select_start_screen
import shiguangschedule.shared.generated.resources.dialog_select_update_channel
import shiguangschedule.shared.generated.resources.dialog_update_check_failed
import shiguangschedule.shared.generated.resources.home_24px
import shiguangschedule.shared.generated.resources.item_check_software_update
import shiguangschedule.shared.generated.resources.item_contributors
import shiguangschedule.shared.generated.resources.item_github_repo
import shiguangschedule.shared.generated.resources.item_language_settings
import shiguangschedule.shared.generated.resources.item_open_source_licenses
import shiguangschedule.shared.generated.resources.item_start_screen_settings
import shiguangschedule.shared.generated.resources.item_update_repo
import shiguangschedule.shared.generated.resources.label_error_message
import shiguangschedule.shared.generated.resources.label_version_prefix
import shiguangschedule.shared.generated.resources.language_24px
import shiguangschedule.shared.generated.resources.list_alt_24px
import shiguangschedule.shared.generated.resources.palette_24px
import shiguangschedule.shared.generated.resources.people_alt_24px
import shiguangschedule.shared.generated.resources.tip_please_wait
import shiguangschedule.shared.generated.resources.theme_settings_title
import shiguangschedule.shared.generated.resources.title_more_options
import shiguangschedule.shared.generated.resources.update_24px
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.RadioButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val SHIGUANG_REPOSITORY_URL = "https://github.com/XingHeYuZhuan/shiguangschedule"

@Composable
internal fun MiuixMoreOptionsScreen(
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
    updateChecker: UpdateChecker = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val version: String = koinInject(named("AppVersionName"))
    val scope = rememberCoroutineScope()
    var updateStatus by remember { mutableStateOf<UpdateStatus>(UpdateStatus.Idle) }
    var updatePlatform by remember { mutableStateOf(UpdatePlatform.GITEE) }
    var showUpdate by remember { mutableStateOf(false) }
    var showChannel by remember { mutableStateOf(false) }
    var showStartScreen by remember { mutableStateOf(false) }
    val background = MiuixTheme.colorScheme.surface
    val scrollBehavior = rememberSharedScrollBehavior()
    val backdrop = rememberLayerBackdrop { drawRect(background); drawContent() }
    val hostPadding = LocalNavigationHostPadding.current
    val direction = LocalLayoutDirection.current
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = background,
        topBar = {
            HyperGlassTopBar(
                title = stringResource(Res.string.title_more_options),
                backdrop = backdrop,
                scrollBehavior = scrollBehavior,
                startAction = { a, s -> HyperLiquidTopBarButton(onClick = onBack, backdrop = backdrop, icon = MiuixIcons.ChevronBackward, contentDescription = stringResource(Res.string.a11y_back), backdropAlpha = a, shadowAlpha = s) },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().background(background).layerBackdrop(backdrop)) {
            LazyColumn(
                Modifier.fillMaxSize().overScrollVertical().nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    start = padding.calculateLeftPadding(direction) + 20.dp,
                    top = padding.calculateTopPadding() + 12.dp,
                    end = padding.calculateRightPadding(direction) + 20.dp,
                    bottom = padding.calculateBottomPadding() + hostPadding.calculateBottomPadding() + 20.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(stringResource(Res.string.app_name), style = MiuixTheme.textStyles.title1, fontWeight = FontWeight.Bold)
                        Text(stringResource(Res.string.label_version_prefix, version), color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        insideMargin = PaddingValues(vertical = 8.dp),
                        colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainer),
                    ) {
                        MoreRow(Res.drawable.update_24px, stringResource(Res.string.item_check_software_update)) { showChannel = true }
                        MoreRow(Res.drawable.language_24px, stringResource(Res.string.item_language_settings)) { onNavigate(Destination.LanguageSettings) }
                        MoreRow(Res.drawable.palette_24px, stringResource(Res.string.theme_settings_title)) { onNavigate(Destination.ThemeSettings) }
                        MoreRow(Res.drawable.home_24px, stringResource(Res.string.item_start_screen_settings), stringResource(state.appSettings.startScreen.labelRes)) { showStartScreen = true }
                        MoreRow(Res.drawable.code_24px, stringResource(Res.string.item_github_repo)) { uriHandler.openUri(SHIGUANG_REPOSITORY_URL) }
                        MoreRow(Res.drawable.list_alt_24px, stringResource(Res.string.item_open_source_licenses)) { onNavigate(Destination.OpenSourceLicenses) }
                        MoreRow(Res.drawable.update_24px, stringResource(Res.string.item_update_repo)) { onNavigate(Destination.UpdateRepo) }
                        MoreRow(Res.drawable.people_alt_24px, stringResource(Res.string.item_contributors)) { onNavigate(Destination.ContributionList) }
                    }
                }
            }
        }
        StartScreenDialog(showStartScreen, state.appSettings.startScreen, { showStartScreen = false }) {
            viewModel.onStartScreenChanged(it)
            showStartScreen = false
        }
        UpdateChannelDialog(showChannel, updatePlatform, { showChannel = false }) { platform ->
            updatePlatform = platform
            showChannel = false
            updateStatus = UpdateStatus.Checking
            showUpdate = true
            scope.launch { updateStatus = updateChecker.checkUpdate(platform, version) }
        }
        UpdateResultMiuixDialog(showUpdate, updateStatus, { showUpdate = false }) { url -> updateChecker.launchUpdate(url) }
    }
}

@Composable
private fun MoreRow(icon: org.jetbrains.compose.resources.DrawableResource, title: String, summary: String? = null, onClick: () -> Unit) {
    BasicComponent(title = title, summary = summary, onClick = onClick, startAction = { Icon(vectorResource(icon), null, tint = MiuixTheme.colorScheme.primary) })
}

@Composable
private fun StartScreenDialog(show: Boolean, current: StartScreen, onDismiss: () -> Unit, onConfirm: (StartScreen) -> Unit) {
    OverlayDialog(show = show, title = stringResource(Res.string.dialog_select_start_screen), onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            StartScreen.entries.forEach { screen ->
                BasicComponent(title = stringResource(screen.labelRes), onClick = { onConfirm(screen) }, startAction = { RadioButton(screen == current, onClick = null) })
            }
            TextButton(text = stringResource(Res.string.action_cancel), onClick = onDismiss, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun UpdateChannelDialog(show: Boolean, current: UpdatePlatform, onDismiss: () -> Unit, onConfirm: (UpdatePlatform) -> Unit) {
    var selected by remember(current) { mutableStateOf(current) }
    OverlayDialog(show = show, title = stringResource(Res.string.dialog_select_update_channel), onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            UpdatePlatform.entries.forEach { platform ->
                BasicComponent(title = platform.title, onClick = { selected = platform }, startAction = { RadioButton(platform == selected, onClick = null) })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TextButton(text = stringResource(Res.string.action_cancel), onClick = onDismiss, modifier = Modifier.weight(1f))
                Button(onClick = { onConfirm(selected) }, modifier = Modifier.weight(1f)) { Text(stringResource(Res.string.action_confirm)) }
            }
        }
    }
}

@Composable
private fun UpdateResultMiuixDialog(show: Boolean, status: UpdateStatus, onDismiss: () -> Unit, onDownload: (String) -> Unit) {
    val visible = show && status !is UpdateStatus.Idle && status !is UpdateStatus.NotSupported
    val title = when (status) {
        UpdateStatus.Checking -> stringResource(Res.string.dialog_checking_update)
        is UpdateStatus.Found -> stringResource(Res.string.dialog_new_version_found, status.versionName)
        is UpdateStatus.Latest -> stringResource(Res.string.dialog_current_version_latest)
        is UpdateStatus.Error -> stringResource(Res.string.dialog_update_check_failed)
        else -> ""
    }
    OverlayDialog(show = visible, title = title, onDismissRequest = if (status is UpdateStatus.Checking) ({}) else onDismiss) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            when (status) {
                UpdateStatus.Checking -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    CircularProgressIndicator()
                    Text(stringResource(Res.string.tip_please_wait))
                }
                is UpdateStatus.Found -> {
                    Column(Modifier.fillMaxWidth().heightIn(max = 350.dp).verticalScroll(rememberScrollState())) { Text(status.changelog, color = MiuixTheme.colorScheme.onSurfaceVariantSummary) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextButton(text = stringResource(Res.string.action_cancel), onClick = onDismiss, modifier = Modifier.weight(1f))
                        Button(onClick = { onDownload(status.targetUrl) }, modifier = Modifier.weight(1f)) { Text(stringResource(Res.string.btn_download_update)) }
                    }
                }
                is UpdateStatus.Latest -> { Text(stringResource(Res.string.label_version_prefix, status.currentVersion)); TextButton(text = stringResource(Res.string.action_confirm), onClick = onDismiss, modifier = Modifier.fillMaxWidth()) }
                is UpdateStatus.Error -> { Text(stringResource(Res.string.label_error_message, status.message), color = MiuixTheme.colorScheme.error); TextButton(text = stringResource(Res.string.action_confirm), onClick = onDismiss, modifier = Modifier.fillMaxWidth()) }
                else -> Unit
            }
        }
    }
}
