package com.xingheyuzhuan.shiguangschedule.ui.miuix.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xingheyuzhuan.shiguangschedule.data.model.ContributionList
import com.xingheyuzhuan.shiguangschedule.ui.components.LocalNavigationHostPadding
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.HyperLiquidTopBarButton
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.rememberSharedScrollBehavior
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.chrome.HyperGlassTopBar
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.utils.overScrollVertical
import com.xingheyuzhuan.shiguangschedule.ui.settings.contribution.ContributionUiState
import com.xingheyuzhuan.shiguangschedule.ui.settings.contribution.ContributionViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.a11y_contributor_avatar
import shiguangschedule.shared.generated.resources.action_retry
import shiguangschedule.shared.generated.resources.label_github
import shiguangschedule.shared.generated.resources.tab_adapter_development
import shiguangschedule.shared.generated.resources.tab_app_development
import shiguangschedule.shared.generated.resources.text_loading_failed
import shiguangschedule.shared.generated.resources.text_no_contributors
import shiguangschedule.shared.generated.resources.title_contribution_list
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
internal fun MiuixContributionScreen(
    onBack: () -> Unit,
    viewModel: ContributionViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTabIndex.collectAsState()
    val tabs = listOf(
        stringResource(Res.string.tab_adapter_development),
        stringResource(Res.string.tab_app_development),
    )
    val uriHandler = LocalUriHandler.current
    val title = stringResource(Res.string.title_contribution_list)
    val background = MiuixTheme.colorScheme.surface
    val scrollBehavior = rememberSharedScrollBehavior()
    val backdrop = rememberLayerBackdrop {
        drawRect(background)
        drawContent()
    }
    val hostPadding = LocalNavigationHostPadding.current
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = background,
        topBar = {
            HyperGlassTopBar(
                title = title,
                largeTitle = title,
                backdrop = backdrop,
                scrollBehavior = scrollBehavior,
                startAction = { backdropAlpha, shadowAlpha ->
                    HyperLiquidTopBarButton(
                        onClick = onBack,
                        backdrop = backdrop,
                        icon = MiuixIcons.ChevronBackward,
                        contentDescription = stringResource(Res.string.a11y_back),
                        backdropAlpha = backdropAlpha,
                        shadowAlpha = shadowAlpha,
                    )
                },
            )
        },
    ) { scaffoldPadding ->
        val contentPadding = PaddingValues(
            start = scaffoldPadding.calculateLeftPadding(layoutDirection) + 20.dp,
            top = scaffoldPadding.calculateTopPadding() + 12.dp,
            end = scaffoldPadding.calculateRightPadding(layoutDirection) + 20.dp,
            bottom = scaffoldPadding.calculateBottomPadding() +
                hostPadding.calculateBottomPadding() + 20.dp,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .layerBackdrop(backdrop),
        ) {
        when (val value = state) {
            ContributionUiState.Loading -> Box(Modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is ContributionUiState.Error -> Box(Modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(Res.string.text_loading_failed, value.message), color = MiuixTheme.colorScheme.error)
                    Button(onClick = viewModel::loadContributions) { Text(stringResource(Res.string.action_retry)) }
                }
            }
            is ContributionUiState.Success -> {
                val contributors = if (selectedTab == 0) value.data.jiaowuadapter else value.data.appDev
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .overScrollVertical()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    item {
                        TabRow(tabs = tabs, selectedTabIndex = selectedTab, onTabSelected = viewModel::selectTab)
                    }
                    if (contributors.isEmpty()) {
                        item { Text(stringResource(Res.string.text_no_contributors), color = MiuixTheme.colorScheme.onSurfaceVariantSummary) }
                    } else {
                        items(contributors, key = ContributionList.Contributor::url) { contributor ->
                            MiuixContributorCard(contributor) { uriHandler.openUri(contributor.url) }
                        }
                    }
                }
            }
        }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun MiuixContributorCard(contributor: ContributionList.Contributor, onClick: () -> Unit) {
    val avatar by produceState<ByteArray?>(null, contributor.avatar) {
        value = runCatching { Res.readBytes("files/contributors_data/${contributor.avatar}") }.getOrNull()
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        insideMargin = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AsyncImage(
                model = avatar,
                contentDescription = stringResource(Res.string.a11y_contributor_avatar, contributor.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(44.dp).clip(CircleShape),
            )
            Text(contributor.name, modifier = Modifier.weight(1f), style = MiuixTheme.textStyles.body1)
            Text(stringResource(Res.string.label_github), color = MiuixTheme.colorScheme.onSurfaceVariantSummary, style = MiuixTheme.textStyles.footnote1)
        }
    }
}
