package com.xingheyuzhuan.shiguangschedule.ui.settings.additional

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.tool.UpdateChecker
import com.xingheyuzhuan.shiguangschedule.tool.UpdatePlatform
import com.xingheyuzhuan.shiguangschedule.tool.UpdateStatus
import com.xingheyuzhuan.shiguangschedule.ui.settings.SettingsViewModel
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.app_name
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.code_24px
import shiguangschedule.shared.generated.resources.home_24px
import shiguangschedule.shared.generated.resources.item_check_software_update
import shiguangschedule.shared.generated.resources.item_contributors
import shiguangschedule.shared.generated.resources.item_github_repo
import shiguangschedule.shared.generated.resources.item_language_settings
import shiguangschedule.shared.generated.resources.item_open_source_licenses
import shiguangschedule.shared.generated.resources.item_start_screen_settings
import shiguangschedule.shared.generated.resources.item_update_repo
import shiguangschedule.shared.generated.resources.label_version_prefix
import shiguangschedule.shared.generated.resources.language_24px
import shiguangschedule.shared.generated.resources.list_alt_24px
import shiguangschedule.shared.generated.resources.palette_24px
import shiguangschedule.shared.generated.resources.people_alt_24px
import shiguangschedule.shared.generated.resources.theme_settings_title
import shiguangschedule.shared.generated.resources.title_more_options
import shiguangschedule.shared.generated.resources.update_24px

private const val GITHUB_REPO_URL = "https://github.com/XingHeYuZhuan/shiguangschedule"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsScreen(
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
    updateChecker: UpdateChecker = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current

    // 从 Koin 动态获取注入的版本号
    val appVersionName: String = koinInject(named("AppVersionName"))

    // 状态观察
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDeveloperModeEnabled = uiState.appSettings.developerModeEnabled
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX
    val miuixScrollBehavior = if (useMiuix) top.yukonga.miuix.kmp.basic.MiuixScrollBehavior() else null

    // 更新逻辑相关状态
    var updateStatus by remember { mutableStateOf<UpdateStatus>(UpdateStatus.Idle) }
    var selectedPlatform by remember { mutableStateOf(UpdatePlatform.GITEE) }

    // 弹窗可见性控制
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showChannelDialog by remember { mutableStateOf(false) }
    var showStartScreenDialog by remember { mutableStateOf(false) }

    // 逻辑：执行更新检查
    val startUpdateCheck: (UpdatePlatform) -> Unit = { platform ->
        selectedPlatform = platform
        showChannelDialog = false
        updateStatus = UpdateStatus.Checking
        showUpdateDialog = true
        coroutineScope.launch {
            updateStatus = updateChecker.checkUpdate(platform, appVersionName)
        }
    }

    Scaffold(
        modifier = if (useMiuix) Modifier.nestedScroll(miuixScrollBehavior!!.nestedScrollConnection) else Modifier,
        containerColor = if (useMiuix) top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
        topBar = {
            if (useMiuix) {
                top.yukonga.miuix.kmp.basic.TopAppBar(
                    title = stringResource(Res.string.title_more_options),
               largeTitle = stringResource(Res.string.title_more_options),
               color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface,
                    scrollBehavior = miuixScrollBehavior,
                    navigationIcon = {
                        top.yukonga.miuix.kmp.basic.IconButton(onBack) {
                            top.yukonga.miuix.kmp.basic.Icon(
                                painterResource(Res.drawable.arrow_back_24px),
                           contentDescription = stringResource(Res.string.a11y_back)
                           , tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurface
                            )
                        }
                    },
                    defaultWindowInsetsPadding = true,
                )
            } else {
                TopAppBar(
                    title = { Text(text = stringResource(Res.string.title_more_options)) },
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 应用信息头部
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DynamicAppIconHeader(
                    isDeveloperModeEnabled = isDeveloperModeEnabled,
                    onTriggerDeveloperMode = { viewModel.onDeveloperModeChanged(true) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (useMiuix) {
                    top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.app_name), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title2, fontWeight = FontWeight.Bold)
                    top.yukonga.miuix.kmp.basic.Text(stringResource(Res.string.label_version_prefix, appVersionName), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
                } else {
                    Text(text = stringResource(Res.string.app_name), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text(text = stringResource(Res.string.label_version_prefix, appVersionName), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // 设置列表卡片
            val settingsContent: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit = {
                DeveloperModeSettingItem(isDeveloperModeEnabled = isDeveloperModeEnabled, onDeveloperModeChanged = { viewModel.onDeveloperModeChanged(it) })
                SettingListItem(icon = vectorResource(Res.drawable.update_24px), title = stringResource(Res.string.item_check_software_update), onClick = { showChannelDialog = true })
                SettingListItem(icon = vectorResource(Res.drawable.language_24px), title = stringResource(Res.string.item_language_settings), onClick = { onNavigate(Destination.LanguageSettings) })
                SettingListItem(icon = vectorResource(Res.drawable.palette_24px), title = stringResource(Res.string.theme_settings_title), onClick = { onNavigate(Destination.ThemeSettings) })
                SettingListItem(
                    icon = vectorResource(Res.drawable.home_24px),
                    title = stringResource(Res.string.item_start_screen_settings),
                    onClick = { showStartScreenDialog = true },
                    trailingContent = {
                        if (useMiuix) top.yukonga.miuix.kmp.basic.Text(stringResource(uiState.appSettings.startScreen.labelRes), style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary)
                        else Text(stringResource(uiState.appSettings.startScreen.labelRes), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                )
                SettingListItem(icon = vectorResource(Res.drawable.code_24px), title = stringResource(Res.string.item_github_repo), onClick = { uriHandler.openUri(GITHUB_REPO_URL) })
                SettingListItem(icon = vectorResource(Res.drawable.list_alt_24px), title = stringResource(Res.string.item_open_source_licenses), onClick = { onNavigate(Destination.OpenSourceLicenses) })
                SettingListItem(icon = vectorResource(Res.drawable.update_24px), title = stringResource(Res.string.item_update_repo), onClick = { onNavigate(Destination.UpdateRepo) })
                SettingListItem(icon = vectorResource(Res.drawable.people_alt_24px), title = stringResource(Res.string.item_contributors), onClick = { onNavigate(Destination.ContributionList) }, showDivider = false)
                AcknowledgmentContent()
            }
            if (useMiuix) top.yukonga.miuix.kmp.basic.Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                insideMargin = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp),
                colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surfaceContainer),
                content = settingsContent
            ) else Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.fillMaxWidth(), content = settingsContent)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- 弹窗逻辑 ---

    // 启动页切换弹窗
    StartScreenSelectionDialog(
        showDialog = showStartScreenDialog,
        currentSelected = uiState.appSettings.startScreen,
        onDismiss = { showStartScreenDialog = false },
        onConfirm = {
            viewModel.onStartScreenChanged(it)
            showStartScreenDialog = false
        }
    )

    // 检查更新结果弹窗
    UpdateResultDialog(
        showDialog = showUpdateDialog,
        updateStatus = updateStatus,
        onDismiss = {
            showUpdateDialog = false
            if (updateStatus !is UpdateStatus.Found) updateStatus = UpdateStatus.Idle
        },
        onDownloadClick = { targetUrl ->
            updateChecker.launchUpdate(targetUrl)
        }
    )

    // 更新渠道选择弹窗
    ChannelSelectionDialog(
        showDialog = showChannelDialog,
        currentSelected = selectedPlatform,
        onDismiss = { showChannelDialog = false },
        onConfirm = startUpdateCheck
    )
}
