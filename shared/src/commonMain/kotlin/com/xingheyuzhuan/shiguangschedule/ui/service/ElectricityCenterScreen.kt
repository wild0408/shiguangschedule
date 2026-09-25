package com.xingheyuzhuan.shiguangschedule.ui.service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xingheyuzhuan.shiguangschedule.data.api.electricity.ElectricityLocation
import com.xingheyuzhuan.shiguangschedule.ui.components.ToastManager
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_save_query
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.label_building
import shiguangschedule.shared.generated.resources.label_campus
import shiguangschedule.shared.generated.resources.label_password
import shiguangschedule.shared.generated.resources.label_room
import shiguangschedule.shared.generated.resources.label_student_id
import shiguangschedule.shared.generated.resources.refresh_24px
import shiguangschedule.shared.generated.resources.title_electricity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectricityCenterScreen(
    onBack: () -> Unit,
    vm: ElectricityViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsState()
    var showPassword by remember { mutableStateOf(false) }
    val room = listOfNotNull(
        state.selectedCampus?.name,
        state.selectedBuilding?.name,
        state.selectedRoom?.name,
    ).joinToString(" ")

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.takeIf(String::isNotBlank)?.let { message ->
            ToastManager.show("查询失败：$message")
            vm.dismissError()
        }
    }
    LaunchedEffect(state.isLoading) {
        if (state.isLoading) ToastManager.show("正在查询电量，请稍候")
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(Res.string.title_electricity)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = stringResource(Res.string.a11y_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = vm::refresh, enabled = state.isConfigured && !state.isLoading) {
                        Icon(vectorResource(Res.drawable.refresh_24px), contentDescription = "刷新电量")
                    }
                },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (!state.isConfigured || state.editing) {
                item { Text("账号与宿舍", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
                item {
                    TextField(
                        value = state.studentId,
                        onValueChange = vm::updateStudentId,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(Res.string.label_student_id)) },
                        singleLine = true,
                    )
                }
                item {
                    TextField(
                        value = state.password,
                        onValueChange = vm::updatePassword,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(Res.string.label_password)) },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { showPassword = !showPassword }) {
                                Text(if (showPassword) "隐藏" else "显示")
                            }
                        },
                    )
                }
                item { LocationMenu(stringResource(Res.string.label_campus), state.campuses, state.selectedCampus, vm::selectCampus, vm::loadCampuses) }
                item { LocationMenu(stringResource(Res.string.label_building), state.buildings, state.selectedBuilding, vm::selectBuilding) }
                item { LocationMenu(stringResource(Res.string.label_room), state.rooms, state.selectedRoom, vm::selectRoom) }
                item {
                    Button(
                        onClick = vm::saveAndQuery,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading && state.studentId.isNotBlank() && state.password.isNotBlank() && state.selectedRoom != null,
                    ) {
                        Text(stringResource(Res.string.action_save_query))
                    }
                }
            }
            state.balance?.let { balance ->
                item { BalanceCard(balance, room, state.lastUpdated) }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(vm::refresh, Modifier.weight(1f), enabled = !state.isLoading) { Text("刷新电量") }
                        Button(vm::editConfiguration, Modifier.weight(1f)) { Text("修改宿舍") }
                    }
                }
                item { Text("用电记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
                item { StatsCard(state.history) }
                item { HistoryCard(state.history) }
                item { TextButton(onClick = vm::clearConfiguration, modifier = Modifier.fillMaxWidth()) { Text("清除账号与记录") } }
            }
        }
    }
}

@Composable
private fun BalanceCard(balance: Double, room: String, updated: Long?) {
    val color = when {
        balance < 10 -> MaterialTheme.colorScheme.error
        balance < 30 -> Color(0xFFB26A00)
        else -> MaterialTheme.colorScheme.primary
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("当前剩余电量", style = MaterialTheme.typography.bodyLarge)
            Text("%.2f 度".format(balance), style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp, fontWeight = FontWeight.Bold), color = color)
            Text(if (balance < 10) "电量偏低，请及时充值" else "电量正常", color = color, style = MaterialTheme.typography.labelMedium)
            Text(room.ifBlank { "未选择宿舍" }, modifier = Modifier.padding(top = 14.dp))
            Text("更新于 ${updated?.let(::formatTime) ?: "--"}", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun StatsCard(history: List<Pair<Long, Double>>) {
    val latest = history.firstOrNull()
    val previous = history.getOrNull(1)
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Stat("查询次数", history.size.toString())
            Stat("最近变化", if (latest != null && previous != null) "%+.2f 度".format(latest.second - previous.second) else "数据不足")
            Stat("最近记录", latest?.first?.let(::formatTime) ?: "暂无")
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun HistoryCard(history: List<Pair<Long, Double>>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("最近 7 次", style = MaterialTheme.typography.titleMedium)
            if (history.isEmpty()) {
                Text("成功查询后会在这里显示历史记录", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
            } else {
                history.take(7).forEach { record ->
                    Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(formatTime(record.first), style = MaterialTheme.typography.labelMedium)
                        Text("%.2f 度".format(record.second), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val time = Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.currentSystemDefault())
    return "%04d-%02d-%02d %02d:%02d".format(time.year, time.monthNumber, time.dayOfMonth, time.hour, time.minute)
}

@Composable
private fun LocationMenu(
    label: String,
    items: List<ElectricityLocation>,
    selected: ElectricityLocation?,
    onSelect: (ElectricityLocation) -> Unit,
    onOpen: (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(
            onClick = { expanded = true; onOpen?.invoke() },
            modifier = Modifier.fillMaxWidth(),
            enabled = items.isNotEmpty() || onOpen != null,
        ) {
            Text(selected?.name ?: label)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { location ->
                DropdownMenuItem(
                    text = { Text(location.name) },
                    onClick = { onSelect(location); expanded = false },
                )
            }
        }
    }
}
