package com.xingheyuzhuan.shiguangschedule.ui.settings.course

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xingheyuzhuan.shiguangschedule.data.model.DualColor
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalIsDarkTheme
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.resolveCourseColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.ui.unit.DpSize
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_cancel
import shiguangschedule.shared.generated.resources.action_confirm
import shiguangschedule.shared.generated.resources.action_double_week
import shiguangschedule.shared.generated.resources.action_select_all
import shiguangschedule.shared.generated.resources.action_single_week
import shiguangschedule.shared.generated.resources.check_24px
import shiguangschedule.shared.generated.resources.label_course_weeks
import shiguangschedule.shared.generated.resources.label_none
import shiguangschedule.shared.generated.resources.text_weeks_selected
import shiguangschedule.shared.generated.resources.title_select_color
import shiguangschedule.shared.generated.resources.title_select_weeks


@Composable
fun WeekSection(
    selectedWeeks: Set<Int>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(Res.string.label_course_weeks)
    val noneSelected = stringResource(Res.string.label_none)

    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.basic.Card(modifier = modifier.clickable(onClick = onClick), colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = MiuixTheme.colorScheme.secondaryContainer)) {
            Column(Modifier.padding(12.dp)) {
                top.yukonga.miuix.kmp.basic.Text(label, style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.primary)
                top.yukonga.miuix.kmp.basic.Text(if (selectedWeeks.isEmpty()) noneSelected else stringResource(Res.string.text_weeks_selected, selectedWeeks.sorted().joinToString(", ")), style = MiuixTheme.textStyles.body2, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }
        }
        return
    }
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            if (selectedWeeks.isEmpty()) {
                Text(text = noneSelected, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            } else {
                Text(
                    text = stringResource(Res.string.text_weeks_selected, selectedWeeks.sorted().joinToString(", ")),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun TimeSection(
    dayName: String,
    timeDesc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.basic.Card(modifier = modifier.clickable(onClick = onClick), colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = MiuixTheme.colorScheme.secondaryContainer)) {
            Column(Modifier.padding(12.dp)) {
                top.yukonga.miuix.kmp.basic.Text(dayName, style = MiuixTheme.textStyles.footnote1, color = MiuixTheme.colorScheme.primary)
                top.yukonga.miuix.kmp.basic.Text(timeDesc, style = MiuixTheme.textStyles.body2, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }
        }
        return
    }
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = dayName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = timeDesc, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ColorIndicatorSection(
    colorIndex: Int,
    colorMaps: List<DualColor>,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val displayColor = resolveCourseColor(
        colorIndex = colorIndex,
        colorMaps = colorMaps,
        isDark = isDark,
        useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(16.dp)
            .background(displayColor)
            .clickable(onClick = onClick)
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WeekSelectorBottomSheet(
    totalWeeks: Int,
    selectedWeeks: Set<Int>,
    onDismissRequest: () -> Unit,
    onConfirm: (Set<Int>) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tempSelectedWeeks by remember { mutableStateOf(selectedWeeks) }
    val titleSelectWeeks = stringResource(Res.string.title_select_weeks)
    val actionSelectAll = stringResource(Res.string.action_select_all)
    val actionSingleWeek = stringResource(Res.string.action_single_week)
    val actionDoubleWeek = stringResource(Res.string.action_double_week)
    val actionCancel = stringResource(Res.string.action_cancel)
    val actionConfirm = stringResource(Res.string.action_confirm)
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = titleSelectWeeks, onDismissRequest = onDismissRequest, insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LazyVerticalGrid(columns = GridCells.Adaptive(48.dp), modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp), contentPadding = PaddingValues(4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(totalWeeks) { index ->
                        val week = index + 1
                        val selected = week in tempSelectedWeeks
                        top.yukonga.miuix.kmp.basic.Card(modifier = Modifier.size(48.dp).clickable { tempSelectedWeeks = if (selected) tempSelectedWeeks - week else tempSelectedWeeks + week }, colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = if (selected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.surfaceContainer)) {
                            Box(Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center) { top.yukonga.miuix.kmp.basic.Text(week.toString(), color = if (selected) MiuixTheme.colorScheme.onPrimary else MiuixTheme.colorScheme.onSurface) }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(actionSelectAll, { tempSelectedWeeks = if (tempSelectedWeeks.size == totalWeeks) emptySet() else (1..totalWeeks).toSet() }, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(actionSingleWeek, { tempSelectedWeeks = (1..totalWeeks).filter { it % 2 != 0 }.toSet() }, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(actionDoubleWeek, { tempSelectedWeeks = (1..totalWeeks).filter { it % 2 == 0 }.toSet() }, Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(actionCancel, onDismissRequest, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(actionConfirm, { onConfirm(tempSelectedWeeks) }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
                }
            }
        }
        return
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titleSelectWeeks,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 48.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(totalWeeks) { index ->
                    val weekNumber = index + 1
                    val isSelected = tempSelectedWeeks.contains(weekNumber)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                tempSelectedWeeks = if (isSelected) {
                                    tempSelectedWeeks - weekNumber
                                } else {
                                    tempSelectedWeeks + weekNumber
                                }
                            }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = weekNumber.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = tempSelectedWeeks.size == totalWeeks && totalWeeks > 0,
                    onClick = {
                        tempSelectedWeeks = if (tempSelectedWeeks.size == totalWeeks) emptySet()
                        else (1..totalWeeks).toSet()
                    },
                    label = { Text(actionSelectAll) }
                )
                FilterChip(
                    selected = tempSelectedWeeks.isNotEmpty() && tempSelectedWeeks.all { it % 2 != 0 },
                    onClick = {
                        tempSelectedWeeks = (1..totalWeeks).filter { it % 2 != 0 }.toSet()
                    },
                    label = { Text(actionSingleWeek) }
                )
                FilterChip(
                    selected = tempSelectedWeeks.isNotEmpty() && tempSelectedWeeks.all { it % 2 == 0 },
                    onClick = {
                        tempSelectedWeeks = (1..totalWeeks).filter { it % 2 == 0 }.toSet()
                    },
                    label = { Text(actionDoubleWeek) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onDismissRequest, modifier = Modifier.weight(1f)) {
                    Text(actionCancel)
                }
                Button(
                    onClick = {
                        onConfirm(tempSelectedWeeks)
                        coroutineScope.launch { modalBottomSheetState.hide() }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) onDismissRequest()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(actionConfirm)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    colorMaps: List<DualColor>,
    selectedIndex: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tempSelectedIndex by remember { mutableIntStateOf(selectedIndex) }

    val isDark = LocalIsDarkTheme.current
    val actionCancel = stringResource(Res.string.action_cancel)
    val actionConfirm = stringResource(Res.string.action_confirm)
    if (LocalUiStyle.current == AppUiStyle.MIUIX) {
        top.yukonga.miuix.kmp.window.WindowDialog(show = true, title = stringResource(Res.string.title_select_color), onDismissRequest = onDismissRequest, insideMargin = DpSize(16.dp, 16.dp)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(colorMaps) { index, _ ->
                        val color = resolveCourseColor(index, colorMaps, isDark, useMiuix = true)
                        val selected = tempSelectedIndex == index
                        Box(Modifier.aspectRatio(1f).clip(CircleShape).clickable { tempSelectedIndex = index }.then(if (selected) Modifier.border(3.dp, MiuixTheme.colorScheme.primary, CircleShape) else Modifier).padding(4.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
                            if (selected) top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.check_24px), null, Modifier.size(20.dp), tint = MiuixTheme.colorScheme.onSurface)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    top.yukonga.miuix.kmp.basic.TextButton(actionCancel, onDismissRequest, Modifier.weight(1f))
                    top.yukonga.miuix.kmp.basic.TextButton(actionConfirm, { onConfirm(tempSelectedIndex) }, Modifier.weight(1f), colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary())
                }
            }
        }
        return
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.title_select_color),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(colorMaps) { index, _ ->
                    val color = resolveCourseColor(index, colorMaps, isDark, useMiuix = false)
                    val isSelected = tempSelectedIndex == index

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .clickable { tempSelectedIndex = index }
                            .then(
                                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier
                            )
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.check_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismissRequest, modifier = Modifier.weight(1f)) {
                    Text(actionCancel)
                }
                Button(
                    onClick = {
                        onConfirm(tempSelectedIndex)
                        coroutineScope.launch { modalBottomSheetState.hide() }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) onDismissRequest()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(actionConfirm)
                }
            }
        }
    }
}
