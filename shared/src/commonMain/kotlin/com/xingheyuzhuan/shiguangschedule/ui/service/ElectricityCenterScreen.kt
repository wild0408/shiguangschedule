package com.xingheyuzhuan.shiguangschedule.ui.service

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.*
import com.xingheyuzhuan.shiguangschedule.data.api.electricity.ElectricityLocation
import com.xingheyuzhuan.shiguangschedule.ui.components.ToastManager
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ElectricityCenterScreen(onBack: () -> Unit, vm: ElectricityViewModel = koinViewModel()) {
    val state by vm.state.collectAsState()
    var showPassword by remember { mutableStateOf(false) }
    val room = listOfNotNull(state.selectedCampus?.name, state.selectedBuilding?.name, state.selectedRoom?.name).joinToString(" ")
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.takeIf { it.isNotBlank() }?.let { message ->
            ToastManager.show("查询失败：$message")
            vm.dismissError()
        }
    }
    LaunchedEffect(state.isLoading) {
        if (state.isLoading) ToastManager.show("正在查询电量，请稍候")
    }
    MiuixTheme {
        Scaffold(topBar = { TopAppBar(
            title = stringResource(Res.string.title_electricity),
            subtitle = if (state.isConfigured && !state.editing) "宿舍用电概览" else "配置账号与宿舍",
            navigationIcon = { IconButton(onBack) { Icon(painterResource(Res.drawable.arrow_back_24px), "返回") } },
            actions = { IconButton({ vm.refresh() }, enabled = state.isConfigured && !state.isLoading) { Icon(painterResource(Res.drawable.refresh_24px), "刷新") } },
        ) }) { insets ->
            LazyColumn(Modifier.fillMaxSize().padding(insets).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (!state.isConfigured || state.editing) {
                    item { Text("账号与宿舍", style = MiuixTheme.textStyles.title3, fontWeight = FontWeight.SemiBold) }
                    item { TextField(state.studentId, vm::updateStudentId, Modifier.fillMaxWidth(), label = stringResource(Res.string.label_student_id), singleLine = true) }
                    item { TextField(state.password, vm::updatePassword, Modifier.fillMaxWidth(), label = stringResource(Res.string.label_password), singleLine = true, visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { TextButton(text = if (showPassword) "隐藏" else "显示", onClick = { showPassword = !showPassword }) }) }
                    item { LocationMenu(stringResource(Res.string.label_campus), state.campuses, state.selectedCampus, vm::selectCampus, vm::loadCampuses) }
                    item { LocationMenu(stringResource(Res.string.label_building), state.buildings, state.selectedBuilding, vm::selectBuilding) }
                    item { LocationMenu(stringResource(Res.string.label_room), state.rooms, state.selectedRoom, vm::selectRoom) }
                    item { Button(vm::saveAndQuery, Modifier.fillMaxWidth(), enabled = !state.isLoading && state.studentId.isNotBlank() && state.password.isNotBlank() && state.selectedRoom != null) { Text(stringResource(Res.string.action_save_query)) } }
                }
                state.balance?.let { balance ->
                    item { BalanceCard(balance, room, state.lastUpdated) }
                    item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { Button({ vm.refresh() }, Modifier.weight(1f), enabled = !state.isLoading) { Text("刷新电量") }; Button(vm::editConfiguration, Modifier.weight(1f)) { Text("修改宿舍") } } }
                    item { Text("用电记录", style = MiuixTheme.textStyles.title3, fontWeight = FontWeight.SemiBold) }
                    item { StatsCard(state.history) }
                    item { HistoryCard(state.history) }
                    item { TextButton("清除账号与记录", vm::clearConfiguration, Modifier.fillMaxWidth()) }
                }
            }
        }
    }
}

@Composable private fun BalanceCard(balance: Double, room: String, updated: Long?) { val color = when { balance < 10 -> MiuixTheme.colorScheme.error; balance < 30 -> Color(0xFFB26A00); else -> MiuixTheme.colorScheme.primary }; Card(Modifier.fillMaxWidth()) { Text("当前剩余电量", style = MiuixTheme.textStyles.body1); Text(text = "%.2f 度".format(balance), style = MiuixTheme.textStyles.headline1.copy(fontSize = 38.sp, fontWeight = FontWeight.Bold), color = color, modifier = Modifier.padding(top = 4.dp)); Text(if (balance < 10) "电量偏低，请及时充值" else "电量正常", color = color, style = MiuixTheme.textStyles.footnote1); Text(text = room.ifBlank { "未选择宿舍" }, modifier = Modifier.padding(top = 14.dp)); Text("更新于 ${updated?.let(::formatTime) ?: "--"}", style = MiuixTheme.textStyles.footnote1) } }
@Composable private fun StatsCard(history: List<Pair<Long, Double>>) { val latest = history.firstOrNull(); val previous = history.getOrNull(1); Card(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Stat("查询次数", history.size.toString()); Stat("最近变化", if (latest != null && previous != null) "%+.2f 度".format(latest.second - previous.second) else "数据不足"); Stat("最近记录", latest?.first?.let(::formatTime) ?: "暂无") } } }
@Composable private fun Stat(label: String, value: String) { Column { Text(label, style = MiuixTheme.textStyles.footnote1); Text(value, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp)) } }
@Composable private fun HistoryCard(history: List<Pair<Long, Double>>) { Card(Modifier.fillMaxWidth()) { Text("最近 7 次", style = MiuixTheme.textStyles.title3); if (history.isEmpty()) Text(text = "成功查询后会在这里显示历史记录", style = MiuixTheme.textStyles.footnote1, modifier = Modifier.padding(top = 8.dp)) else history.take(7).forEach { record -> Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(formatTime(record.first), style = MiuixTheme.textStyles.footnote1); Text("%.2f 度".format(record.second), fontWeight = FontWeight.SemiBold) } } } }
private fun formatTime(ms: Long): String { val t = Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.currentSystemDefault()); return "%04d-%02d-%02d %02d:%02d".format(t.year, t.monthNumber, t.dayOfMonth, t.hour, t.minute) }
@Composable private fun LocationMenu(label: String, items: List<ElectricityLocation>, selected: ElectricityLocation?, onSelect: (ElectricityLocation) -> Unit, onOpen: (() -> Unit)? = null) { var expanded by remember { mutableStateOf(false) }; Box { Button({ expanded = true; onOpen?.invoke() }, Modifier.fillMaxWidth(), enabled = items.isNotEmpty() || onOpen != null) { Text(selected?.name ?: label) }; DropdownMenu(expanded, { expanded = false }) { items.forEach { location -> DropdownMenuItem({ Text(location.name) }, { onSelect(location); expanded = false }) } } } }
