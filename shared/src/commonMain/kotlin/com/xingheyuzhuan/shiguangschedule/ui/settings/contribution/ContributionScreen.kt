package com.xingheyuzhuan.shiguangschedule.ui.settings.contribution

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.xingheyuzhuan.shiguangschedule.data.model.ContributionList
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back_to_previous
import shiguangschedule.shared.generated.resources.a11y_contributor_avatar
import shiguangschedule.shared.generated.resources.action_retry
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.label_github
import shiguangschedule.shared.generated.resources.tab_adapter_development
import shiguangschedule.shared.generated.resources.tab_app_development
import shiguangschedule.shared.generated.resources.text_loading_failed
import shiguangschedule.shared.generated.resources.text_no_contributors
import shiguangschedule.shared.generated.resources.title_contribution_list

/**
 * 贡献者列表主界面
 *
 * @param onBack 返回上一页回调
 * @param viewModel 状态管理 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionScreen(
    onBack: () -> Unit,
    viewModel: ContributionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val uriHandler = LocalUriHandler.current
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX

    Scaffold(
        containerColor = if (useMiuix) MiuixTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
        topBar = {
            if (useMiuix) top.yukonga.miuix.kmp.basic.TopAppBar(
                title = stringResource(Res.string.title_contribution_list),
                largeTitle = stringResource(Res.string.title_contribution_list),
                color = MiuixTheme.colorScheme.surface,
                navigationIcon = {
                    top.yukonga.miuix.kmp.basic.IconButton(onBack) {
                        top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.arrow_back_24px), stringResource(Res.string.a11y_back_to_previous), tint = MiuixTheme.colorScheme.onSurface)
                    }
                },
                defaultWindowInsetsPadding = true,
            ) else TopAppBar(
                title = { Text(stringResource(Res.string.title_contribution_list)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = vectorResource(Res.drawable.arrow_back_24px), contentDescription = stringResource(Res.string.a11y_back_to_previous))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ContributionTabs(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = viewModel::selectTab,
                useMiuix = useMiuix
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                when (val state = uiState) {
                    ContributionUiState.Loading -> {
                        if (useMiuix) top.yukonga.miuix.kmp.basic.CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        else CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is ContributionUiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ErrorMessage(
                                message = state.message,
                                onRetry = viewModel::loadContributions
                            )
                        }
                    }

                    is ContributionUiState.Success -> {
                        val listToShow = when (selectedTabIndex) {
                            0 -> state.data.jiaowuadapter
                            1 -> state.data.appDev
                            else -> emptyList()
                        }
                        ContributorListContent(
                            list = listToShow,
                            useMiuix = useMiuix,
                            onContributorClick = { url ->
                                uriHandler.openUri(url)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 分类选项卡栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    useMiuix: Boolean
) {
    val tabs = listOf(
        stringResource(Res.string.tab_adapter_development),
        stringResource(Res.string.tab_app_development)
    )

    if (useMiuix) {
        top.yukonga.miuix.kmp.basic.TabRow(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    } else PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = if (selectedTabIndex == index) {
                            MaterialTheme.typography.titleSmall
                        } else {
                            MaterialTheme.typography.bodyMedium
                        }
                    )
                }
            )
        }
    }
}

/**
 * 贡献者列表内容
 */
@Composable
fun ContributorListContent(
    list: List<ContributionList.Contributor>,
    useMiuix: Boolean,
    onContributorClick: (String) -> Unit
) {
    if (list.isEmpty()) {
        val emptyText = stringResource(Res.string.text_no_contributors)
        if (useMiuix) top.yukonga.miuix.kmp.basic.Text(
            text = emptyText,
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.padding(top = 24.dp)
        ) else Text(text = emptyText, modifier = Modifier.padding(top = 16.dp))
        return
    }

    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(list, key = { it.url }) { contributor ->
            ContributorCard(
                contributor = contributor,
                useMiuix = useMiuix,
                onContributorClick = onContributorClick
            )
        }
    }
}

/**
 * 单个贡献者卡片组件
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun ContributorCard(
    contributor: ContributionList.Contributor,
    useMiuix: Boolean,
    onContributorClick: (String) -> Unit
) {
    val a11yAvatar = stringResource(Res.string.a11y_contributor_avatar, contributor.name)
    val labelGithub = stringResource(Res.string.label_github)

    val avatarBytes by produceState<ByteArray?>(initialValue = null, contributor.avatar) {
        value = try {
            Res.readBytes("files/contributors_data/${contributor.avatar}")
        } catch (_: Exception) {
            null
        }
    }

    if (useMiuix) top.yukonga.miuix.kmp.basic.Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        onClick = { onContributorClick(contributor.url) },
    ) {
        ContributorRow(avatarBytes, a11yAvatar, contributor.name, labelGithub, true)
    } else Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onContributorClick(contributor.url) }
    ) {
        ContributorRow(avatarBytes, a11yAvatar, contributor.name, labelGithub, false)
    }
}

@Composable
private fun ContributorRow(
    avatarBytes: ByteArray?,
    avatarDescription: String,
    name: String,
    trailing: String,
    useMiuix: Boolean,
) {
    Row(
        modifier = if (useMiuix) Modifier.fillMaxWidth() else Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarBytes,
            contentDescription = avatarDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(44.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(14.dp))
        if (useMiuix) top.yukonga.miuix.kmp.basic.Text(name, style = MiuixTheme.textStyles.body1)
        else Text(name, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        if (useMiuix) top.yukonga.miuix.kmp.basic.Text(trailing, style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
        else Text(trailing, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
    }
}

/**
 * 加载失败的错误提示页面
 */
@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    val textLoadingFailed = stringResource(Res.string.text_loading_failed, message)
    val actionRetry = stringResource(Res.string.action_retry)
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (useMiuix) {
            top.yukonga.miuix.kmp.basic.Text(
                text = textLoadingFailed,
                color = MiuixTheme.colorScheme.error,
                style = MiuixTheme.textStyles.body1,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            top.yukonga.miuix.kmp.basic.Button(onClick = onRetry) { top.yukonga.miuix.kmp.basic.Text(actionRetry) }
        } else {
            Text(text = textLoadingFailed, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            Button(onClick = onRetry) { Text(actionRetry) }
        }
    }
}
