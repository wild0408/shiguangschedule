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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.DpSize
import com.xingheyuzhuan.shiguangschedule.data.api.electricity.ElectricityLocation
import com.xingheyuzhuan.shiguangschedule.ui.components.ToastManager
import com.xingheyuzhuan.shiguangschedule.ui.components.LocalNavigationHostPadding
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.HyperLiquidTopBarButton
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.basic.rememberSharedScrollBehavior
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.chrome.HyperGlassTopBar
import com.xingheyuzhuan.shiguangschedule.ui.miuix.hyper.utils.overScrollVertical
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xingheyuzhuan.shiguangschedule.ui.service.ElectricityViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_save_query
import shiguangschedule.shared.generated.resources.label_building
import shiguangschedule.shared.generated.resources.label_campus
import shiguangschedule.shared.generated.resources.label_password
import shiguangschedule.shared.generated.resources.label_room
import shiguangschedule.shared.generated.resources.label_student_id
import shiguangschedule.shared.generated.resources.refresh_24px
import shiguangschedule.shared.generated.resources.title_electricity
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog

@Composable
internal fun MiuixElectricityCenterScreen(
    onBack: () -> Unit,
    viewModel: ElectricityViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showLoginDialog by remember { mutableStateOf(!state.isLoggedIn) }
    val configuring = !state.isConfigured || state.editing
    val room = listOfNotNull(state.selectedCampus?.name, state.selectedBuilding?.name, state.selectedRoom?.name).joinToString(" ")

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.takeIf(String::isNotBlank)?.let {
            ToastManager.show("查询失败：$it")
            viewModel.dismissError()
        }
    }
    LaunchedEffect(state.isLoading) {
        if (state.isLoading) ToastManager.show("正在查询电量，请稍候")
    }

    LaunchedEffect(state.isLoggedIn) { if (state.isLoggedIn) showLoginDialog = false }

    val background = MiuixTheme.colorScheme.surface
    val scrollBehavior = rememberSharedScrollBehavior()
    val backdrop = rememberLayerBackdrop { drawRect(background); drawContent() }
    val direction = LocalLayoutDirection.current
    val hostPadding = LocalNavigationHostPadding.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = background,
        topBar = {
            HyperGlassTopBar(
                title = stringResource(Res.string.title_electricity),
                backdrop = backdrop,
                scrollBehavior = scrollBehavior,
                startAction = { a, s -> HyperLiquidTopBarButton(onBack, backdrop, MiuixIcons.ChevronBackward, "返回", backdropAlpha = a, shadowAlpha = s) },
                endAction = { a, s -> HyperLiquidTopBarButton(viewModel::refresh, backdrop, vectorResource(Res.drawable.refresh_24px), "刷新", backdropAlpha = a, shadowAlpha = s) },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().background(background).layerBackdrop(backdrop)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().overScrollVertical().nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    start = padding.calculateLeftPadding(direction) + 20.dp,
                    top = padding.calculateTopPadding() + 12.dp,
                    end = padding.calculateRightPadding(direction) + 20.dp,
                    bottom = padding.calculateBottomPadding() + hostPadding.calculateBottomPadding() + 20.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
            if (configuring) {
                item { SmallTitle("账号与宿舍") }
                item { Button({ showLoginDialog = true }, Modifier.fillMaxWidth()) { Text(if (state.isLoggedIn) "更换账号" else "登录账号") } }
                if (state.isLoggedIn) {
                    item { LocationPreference(stringResource(Res.string.label_campus), state.campuses, state.selectedCampus, viewModel::selectCampus, viewModel::loadCampuses, state.isLoadingLocations) }
                    item { LocationPreference(stringResource(Res.string.label_building), state.buildings, state.selectedBuilding, viewModel::selectBuilding, loading = state.isLoadingLocations) }
                    item { LocationPreference(stringResource(Res.string.label_room), state.rooms, state.selectedRoom, viewModel::selectRoom, loading = state.isLoadingLocations) }
                }
                item {
                    Button(
                        onClick = viewModel::saveAndQuery,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading && state.isLoggedIn && state.selectedRoom != null,
                    ) { Text(stringResource(Res.string.action_save_query)) }
                }
            }
            state.balance?.let { balance ->
                item { BalanceCard(balance, room, state.lastUpdated) }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button({ viewModel.refresh() }, Modifier.weight(1f), enabled = !state.isLoading) { Text("刷新电量") }
                        Button(viewModel::editConfiguration, Modifier.weight(1f)) { Text("修改宿舍") }
                    }
                }
                item { SmallTitle("用电记录") }
                item { StatsCard(state.history) }
                item { HistoryCard(state.history) }
                item { TextButton("清除账号与记录", viewModel::clearConfiguration, Modifier.fillMaxWidth()) }
            }
            }
        }
        if (showLoginDialog) {
            ElectricityLoginDialog(
                studentId = state.studentId,
                password = state.password,
                loading = state.isLoadingLocations,
                onDismiss = { if (state.isLoggedIn) showLoginDialog = false },
                onLogin = { id, password ->
                    viewModel.updateStudentId(id)
                    viewModel.updatePassword(password)
                    viewModel.loginAndLoadCampuses()
                },
            )
        }
    }
}

@Composable
private fun ElectricityLoginDialog(
    studentId: String,
    password: String,
    loading: Boolean,
    onDismiss: () -> Unit,
    onLogin: (String, String) -> Unit,
) {
    var id by remember(studentId) { mutableStateOf(studentId) }
    var pwd by remember(password) { mutableStateOf(password) }
    var visible by remember { mutableStateOf(false) }
    WindowDialog(show = true, title = "登录电费账号", onDismissRequest = onDismiss, insideMargin = DpSize(16.dp, 16.dp)) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TextField(id, { id = it }, Modifier.fillMaxWidth(), label = stringResource(Res.string.label_student_id), singleLine = true)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(pwd, { pwd = it }, Modifier.weight(1f), label = stringResource(Res.string.label_password), singleLine = true, visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation())
                TextButton(if (visible) "隐藏" else "显示", { visible = !visible }, modifier = Modifier.padding(top = 8.dp))
            }
            Button({ onLogin(id, pwd) }, Modifier.fillMaxWidth(), enabled = !loading && id.isNotBlank() && pwd.isNotBlank()) { Text(if (loading) "登录中" else "登录并加载宿舍") }
        }
    }
}

@Composable
private fun LocationPreference(
    title: String,
    items: List<ElectricityLocation>,
    selected: ElectricityLocation?,
    onSelect: (ElectricityLocation) -> Unit,
    onOpen: (() -> Unit)? = null,
    loading: Boolean = false,
) {
    LaunchedEffect(items.isEmpty(), selected?.value) {
        if (items.isEmpty() && selected == null) onOpen?.invoke()
    }
    WindowDropdownPreference(
        items = items.map(ElectricityLocation::name),
        selectedIndex = selected?.let(items::indexOf)?.takeIf { it >= 0 } ?: 0,
        title = title,
        summary = when {
            loading -> "正在加载$title"
            selected != null -> selected.name
            items.isEmpty() -> "请先填写账号密码"
            else -> "请选择$title"
        },
        modifier = Modifier.fillMaxWidth(),
        onSelectedIndexChange = { index -> items.getOrNull(index)?.let(onSelect) },
    )
}

@Composable
private fun BalanceCard(balance: Double, room: String, updated: Long?) {
    val color = when { balance < 10 -> MiuixTheme.colorScheme.error; balance < 30 -> Color(0xFFB26A00); else -> MiuixTheme.colorScheme.primary }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("当前剩余电量", style = MiuixTheme.textStyles.body1)
            Text("%.2f 度".format(balance), style = MiuixTheme.textStyles.headline1.copy(fontSize = 38.sp, fontWeight = FontWeight.Bold), color = color, modifier = Modifier.padding(top = 4.dp))
            Text(if (balance < 10) "电量偏低，请及时充值" else "电量正常", color = color, style = MiuixTheme.textStyles.footnote1)
            Text(room.ifBlank { "未选择宿舍" }, modifier = Modifier.padding(top = 14.dp))
            Text("更新于 ${updated?.let(::formatTime) ?: "--"}", style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
        }
    }
}

@Composable private fun StatsCard(history: List<Pair<Long, Double>>) { val latest = history.firstOrNull(); val previous = history.getOrNull(1); Card(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) { Stat("查询次数", history.size.toString()); Stat("最近变化", if (latest != null && previous != null) "%+.2f 度".format(latest.second - previous.second) else "数据不足"); Stat("最近记录", latest?.first?.let(::formatTime) ?: "暂无") } } }
@Composable private fun Stat(label: String, value: String) { Column { Text(label, style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary); Text(value, style = MiuixTheme.textStyles.body2, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp)) } }
@Composable private fun HistoryCard(history: List<Pair<Long, Double>>) { Card(Modifier.fillMaxWidth()) { Column(Modifier.fillMaxWidth().padding(16.dp)) { Text("最近 7 次", style = MiuixTheme.textStyles.title3); if (history.isEmpty()) Text("成功查询后会在这里显示历史记录", style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary, modifier = Modifier.padding(top = 8.dp)) else history.take(7).forEach { record -> Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(formatTime(record.first), style = MiuixTheme.textStyles.footnote1); Text("%.2f 度".format(record.second), fontWeight = FontWeight.SemiBold) } } } } }
private fun formatTime(ms: Long): String { val time = Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.currentSystemDefault()); return "%04d-%02d-%02d %02d:%02d".format(time.year, time.monthNumber, time.dayOfMonth, time.hour, time.minute) }
