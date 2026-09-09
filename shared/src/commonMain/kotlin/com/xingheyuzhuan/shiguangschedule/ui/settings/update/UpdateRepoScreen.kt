package com.xingheyuzhuan.shiguangschedule.ui.settings.update

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xingheyuzhuan.shiguangschedule.data.model.RepoType
import com.xingheyuzhuan.shiguangschedule.data.model.RepositoryInfo
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.action_update
import shiguangschedule.shared.generated.resources.action_updating
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.arrow_drop_down_24px
import shiguangschedule.shared.generated.resources.label_password_or_token_value
import shiguangschedule.shared.generated.resources.label_private_repo_credentials
import shiguangschedule.shared.generated.resources.label_repo_branch
import shiguangschedule.shared.generated.resources.label_repo_url
import shiguangschedule.shared.generated.resources.label_select_repo
import shiguangschedule.shared.generated.resources.label_username_or_token_key
import shiguangschedule.shared.generated.resources.text_select_repo_hint
import shiguangschedule.shared.generated.resources.title_update_log
import shiguangschedule.shared.generated.resources.title_update_repo_screen
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateRepoScreen(
    onBack: () -> Unit,
    viewModel: UpdateRepoViewModel = koinViewModel()
) {
    // 观察 ViewModel 的 uiState
    val uiState by viewModel.uiState.collectAsState()
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX

    if (useMiuix) {
        MiuixUpdateRepoScreen(
            onBack = onBack,
            repoList = uiState.repoList,
            selectedRepo = uiState.selectedRepo,
            currentUrl = uiState.currentEditableUrl,
            currentBranch = uiState.currentEditableBranch,
            currentUsername = uiState.currentEditableUsername,
            currentPassword = uiState.currentEditablePassword,
            isUpdating = uiState.isUpdating,
            isDeveloperModeEnabled = uiState.isDeveloperModeEnabled,
            logs = uiState.logs,
            onRepoSelected = viewModel::selectRepository,
            onUrlChanged = viewModel::updateCurrentUrl,
            onBranchChanged = viewModel::updateCurrentBranch,
            onUsernameChanged = viewModel::updateCurrentUsername,
            onPasswordChanged = viewModel::updateCurrentPassword,
            onUpdateClicked = viewModel::startUpdate,
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.title_update_repo_screen)) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 仓库选择与操作卡片
            RepoSelectionCard(
                repoList = uiState.repoList,
                selectedRepo = uiState.selectedRepo,
                currentUrl = uiState.currentEditableUrl,
                currentBranch = uiState.currentEditableBranch,
                currentUsername = uiState.currentEditableUsername,
                currentPassword = uiState.currentEditablePassword,
                isUpdating = uiState.isUpdating,
                isDeveloperModeEnabled = uiState.isDeveloperModeEnabled,
                onRepoSelected = { repo -> viewModel.selectRepository(repo) },
                onUrlChanged = { url -> viewModel.updateCurrentUrl(url) },
                onBranchChanged = { branch -> viewModel.updateCurrentBranch(branch) },
                onUsernameChanged = { username -> viewModel.updateCurrentUsername(username) },
                onPasswordChanged = { password -> viewModel.updateCurrentPassword(password) },
                onUpdateClicked = { viewModel.startUpdate() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 日志显示卡片
            LogDisplayCard(logs = uiState.logs)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoSelectionCard(
    repoList: List<RepositoryInfo>,
    selectedRepo: RepositoryInfo?,
    currentUrl: String,
    currentBranch: String,
    currentUsername: String,
    currentPassword: String,
    isUpdating: Boolean,
    isDeveloperModeEnabled: Boolean,
    onRepoSelected: (RepositoryInfo) -> Unit,
    onUrlChanged: (String) -> Unit,
    onBranchChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onUpdateClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // 统一定义普通 TextField 的颜色方案
    val commonTextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    // 统一定义下拉框 TextField 的颜色方案
    val dropdownTextFieldColors = ExposedDropdownMenuDefaults.textFieldColors(
        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = Color.Transparent,
    )

    Card(
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
            Text(
                text = stringResource(Res.string.label_select_repo),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedRepo?.name ?: stringResource(Res.string.text_select_repo_hint),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                        .fillMaxWidth(),
                    colors = dropdownTextFieldColors,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    val displayRepos = repoList.filter { repo ->
                        if (!isDeveloperModeEnabled) {
                            repo.repoType != RepoType.CUSTOM && repo.repoType != RepoType.PRIVATE_REPO
                        } else {
                            true
                        }
                    }

                    displayRepos.forEach { repo ->
                        DropdownMenuItem(
                            text = { Text(repo.name) },
                            onClick = {
                                onRepoSelected(repo)
                                expanded = false
                            }
                        )
                    }
                }
            }

            RepoEditOptions(
                selectedRepo = selectedRepo,
                currentUrl = currentUrl,
                currentBranch = currentBranch,
                onUrlChanged = onUrlChanged,
                onBranchChanged = onBranchChanged,
                currentUsername = currentUsername,
                currentPassword = currentPassword,
                onUsernameChanged = onUsernameChanged,
                onPasswordChanged = onPasswordChanged,
                textFieldColors = commonTextFieldColors
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onUpdateClicked,
                enabled = !isUpdating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isUpdating) {
                        stringResource(Res.string.action_updating)
                    } else {
                        stringResource(Res.string.action_update)
                    }
                )
            }
            AnimatedVisibility(visible = isUpdating) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

/**
 * 根据仓库类型和可编辑性显示编辑选项
 */
@Composable
fun RepoEditOptions(
    selectedRepo: RepositoryInfo?,
    currentUrl: String,
    currentBranch: String,
    onUrlChanged: (String) -> Unit,
    onBranchChanged: (String) -> Unit,
    currentUsername: String,
    currentPassword: String,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    textFieldColors: TextFieldColors
) {
    // 只有在仓库被选中且可编辑时才显示编辑框
    if (selectedRepo?.editable == true) {

        Spacer(modifier = Modifier.height(16.dp))

        // URL 编辑框 (适用于 CUSTOM, PRIVATE_REPO)
        TextField(
            value = currentUrl,
            onValueChange = onUrlChanged,
            label = { Text(stringResource(Res.string.label_repo_url)) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Branch 编辑框 (适用于 CUSTOM, PRIVATE_REPO)
        TextField(
            value = currentBranch,
            onValueChange = onBranchChanged,
            label = { Text(stringResource(Res.string.label_repo_branch)) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        // 实现私有仓库的凭证输入
        if (selectedRepo.repoType == RepoType.PRIVATE_REPO) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.label_private_repo_credentials),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 用户名输入框
            TextField(
                value = currentUsername,
                onValueChange = onUsernameChanged,
                label = { Text(stringResource(Res.string.label_username_or_token_key)) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = currentPassword,
                onValueChange = onPasswordChanged,
                label = { Text(stringResource(Res.string.label_password_or_token_value)) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
        }
    }
}

@Composable
fun LogDisplayCard(logs: String) {
    val scrollState = rememberScrollState()

    LaunchedEffect(logs) {
        if (logs.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.title_update_log),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            SelectionContainer {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ){
                    Text(
                        text = logs,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .verticalScroll(scrollState),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MiuixUpdateRepoScreen(
    onBack: () -> Unit,
    repoList: List<RepositoryInfo>,
    selectedRepo: RepositoryInfo?,
    currentUrl: String,
    currentBranch: String,
    currentUsername: String,
    currentPassword: String,
    isUpdating: Boolean,
    isDeveloperModeEnabled: Boolean,
    logs: String,
    onRepoSelected: (RepositoryInfo) -> Unit,
    onUrlChanged: (String) -> Unit,
    onBranchChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onUpdateClicked: () -> Unit,
) {
    var showRepoPicker by remember { mutableStateOf(false) }
    val visibleRepos = repoList.filter { isDeveloperModeEnabled || it.repoType != RepoType.CUSTOM && it.repoType != RepoType.PRIVATE_REPO }
    val scrollState = rememberScrollState()
    val title = stringResource(Res.string.title_update_repo_screen)

    top.yukonga.miuix.kmp.basic.Scaffold(
        containerColor = MiuixTheme.colorScheme.surface,
        topBar = {
            top.yukonga.miuix.kmp.basic.TopAppBar(
                title = title,
                largeTitle = title,
                color = MiuixTheme.colorScheme.surface,
                navigationIcon = {
                    top.yukonga.miuix.kmp.basic.IconButton(onBack) {
                        top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.arrow_back_24px), stringResource(Res.string.a11y_back), tint = MiuixTheme.colorScheme.onSurface)
                    }
                },
                defaultWindowInsetsPadding = true,
            )
        }
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding).verticalScroll(scrollState).padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            top.yukonga.miuix.kmp.basic.SmallTitle(stringResource(Res.string.label_select_repo))
            top.yukonga.miuix.kmp.basic.Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                onClick = { showRepoPicker = true },
            ) {
                androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        top.yukonga.miuix.kmp.basic.Text(selectedRepo?.name ?: stringResource(Res.string.text_select_repo_hint), style = MiuixTheme.textStyles.body1)
                        selectedRepo?.let { repo ->
                            top.yukonga.miuix.kmp.basic.Text(repo.repoType.name, style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                        }
                    }
                    top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.arrow_drop_down_24px), null, tint = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                }
            }

            if (selectedRepo?.editable == true) {
                Spacer(Modifier.height(16.dp))
                top.yukonga.miuix.kmp.basic.SmallTitle(stringResource(Res.string.label_repo_url))
                top.yukonga.miuix.kmp.basic.TextField(currentUrl, onUrlChanged, Modifier.fillMaxWidth(), label = stringResource(Res.string.label_repo_url), singleLine = true)
                Spacer(Modifier.height(10.dp))
                top.yukonga.miuix.kmp.basic.TextField(currentBranch, onBranchChanged, Modifier.fillMaxWidth(), label = stringResource(Res.string.label_repo_branch), singleLine = true)

                if (selectedRepo.repoType == RepoType.PRIVATE_REPO) {
                    Spacer(Modifier.height(16.dp))
                    top.yukonga.miuix.kmp.basic.SmallTitle(stringResource(Res.string.label_private_repo_credentials))
                    top.yukonga.miuix.kmp.basic.TextField(currentUsername, onUsernameChanged, Modifier.fillMaxWidth(), label = stringResource(Res.string.label_username_or_token_key), singleLine = true)
                    Spacer(Modifier.height(10.dp))
                    top.yukonga.miuix.kmp.basic.TextField(currentPassword, onPasswordChanged, Modifier.fillMaxWidth(), label = stringResource(Res.string.label_password_or_token_value), singleLine = true)
                }
            }

            Spacer(Modifier.height(20.dp))
            top.yukonga.miuix.kmp.basic.Button(onClick = onUpdateClicked, enabled = !isUpdating, modifier = Modifier.fillMaxWidth()) {
                top.yukonga.miuix.kmp.basic.Text(if (isUpdating) stringResource(Res.string.action_updating) else stringResource(Res.string.action_update))
            }
            AnimatedVisibility(isUpdating) {
                top.yukonga.miuix.kmp.basic.LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 12.dp))
            }

            Spacer(Modifier.height(20.dp))
            MiuixUpdateLog(logs)
            Spacer(Modifier.height(24.dp))
        }

        top.yukonga.miuix.kmp.overlay.OverlayDialog(
            title = stringResource(Res.string.label_select_repo),
            show = showRepoPicker,
            onDismissRequest = { showRepoPicker = false },
        ) {
            Column(Modifier.fillMaxWidth()) {
                visibleRepos.forEach { repo ->
                    top.yukonga.miuix.kmp.basic.Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        insideMargin = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(
                            color = if (repo == selectedRepo) MiuixTheme.colorScheme.primaryContainer.copy(alpha = 0.22f) else MiuixTheme.colorScheme.surfaceContainer
                        ),
                        onClick = { onRepoSelected(repo); showRepoPicker = false },
                    ) {
                        top.yukonga.miuix.kmp.basic.Text(repo.name, style = MiuixTheme.textStyles.body1)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiuixUpdateLog(logs: String) {
    val logScrollState = rememberScrollState()
    LaunchedEffect(logs) {
        if (logs.isNotEmpty()) logScrollState.animateScrollTo(logScrollState.maxValue)
    }
    top.yukonga.miuix.kmp.basic.SmallTitle(stringResource(Res.string.title_update_log))
    top.yukonga.miuix.kmp.basic.Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = androidx.compose.foundation.layout.PaddingValues(12.dp),
    ) {
        SelectionContainer {
            top.yukonga.miuix.kmp.basic.Surface(
                modifier = Modifier.fillMaxWidth().height(260.dp),
                color = MiuixTheme.colorScheme.surfaceContainerHigh,
            ) {
                top.yukonga.miuix.kmp.basic.Text(
                    text = logs,
                    modifier = Modifier.fillMaxSize().padding(10.dp).verticalScroll(logScrollState),
                    style = MiuixTheme.textStyles.footnote1.copy(fontFamily = FontFamily.Monospace),
                )
            }
        }
    }
}
