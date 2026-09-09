package com.xingheyuzhuan.shiguangschedule.ui.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.data.db.main.CourseWithWeeks
import com.xingheyuzhuan.shiguangschedule.data.model.schedule_style.ScheduleModeProto
import com.xingheyuzhuan.shiguangschedule.ui.components.AdaptiveNavigationScaffold
import com.xingheyuzhuan.shiguangschedule.ui.components.CourseTablePickerDialog
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.AndroidLiquidGlassButton
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.AndroidLiquidGlassTopBar
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.CourseBlock
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.FloatingCourseBar
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.ScheduleGridStyleComposed
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.TimeColumn
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.calculateSingleSchedulables
import com.xingheyuzhuan.shiguangschedule.ui.schedule.components.rearrangeDays
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.action_select_table
import shiguangschedule.shared.generated.resources.archive_24px
import shiguangschedule.shared.generated.resources.calendar_today_24px
import shiguangschedule.shared.generated.resources.class_24px
import shiguangschedule.shared.generated.resources.double_arrow_24px
import shiguangschedule.shared.generated.resources.edit_24px
import shiguangschedule.shared.generated.resources.location_on_24px
import shiguangschedule.shared.generated.resources.more_vert_24px
import shiguangschedule.shared.generated.resources.palette_24px
import shiguangschedule.shared.generated.resources.person_24px
import shiguangschedule.shared.generated.resources.refresh_24px
import shiguangschedule.shared.generated.resources.schedule_24px
import shiguangschedule.shared.generated.resources.sticky_note_2_24px
import shiguangschedule.shared.generated.resources.swap_horiz_24px
import shiguangschedule.shared.generated.resources.week_days_short_names
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.window.WindowDialog
import kotlin.math.roundToInt
import kotlinx.datetime.number
import kotlinx.datetime.isoDayNumber
import kotlin.time.Clock

private const val MIUIX_PAGER_CENTER = 50
private const val MIUIX_PAGER_PAGE_COUNT = 101

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MiuixWeeklyScheduleScreen(
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit,
    viewModel: WeeklyScheduleViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = MIUIX_PAGER_CENTER,
        pageCount = { MIUIX_PAGER_PAGE_COUNT }
    )
    var activeScrollOffset by remember { mutableIntStateOf(0) }
    var showTableSwitcher by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showWeekSelector by remember { mutableStateOf(false) }
    var selectedBlock by remember { mutableStateOf<MergedCourseBlock?>(null) }

    fun weekStart(date: LocalDate, firstDay: Int): LocalDate {
        val target = DayOfWeek(firstDay.coerceIn(1, 7)).isoDayNumber
        val diff = (date.dayOfWeek.isoDayNumber - target + 7) % 7
        return date.plus(-diff, DateTimeUnit.DAY)
    }

    LaunchedEffect(pagerState, uiState.firstDayOfWeek) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                val offset = (page - MIUIX_PAGER_CENTER).toLong()
                viewModel.updatePagerDate(weekStart(today, uiState.firstDayOfWeek).plus(offset * 7, DateTimeUnit.DAY))
            }
    }

    val style = remember(uiState.style) {
        with(ScheduleGridStyleComposed) { uiState.style.toComposedStyle() }
    }
    val displayTitle = when {
        !uiState.isSemesterSet || uiState.semesterStartDate == null -> "学期未开始"
        uiState.daysUntilStart > 0 -> "还有 ${uiState.daysUntilStart} 天开学"
        uiState.weekIndexInPager != null && uiState.weekIndexInPager!! in 1..uiState.totalWeeks -> "第${uiState.weekIndexInPager}周"
        else -> "假期中"
    }
    val isViewingCurrentWeek = uiState.weekIndexInPager == uiState.currentWeekNumber
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(Modifier.fillMaxSize()) {
    AdaptiveNavigationScaffold(
        currentDestination = Destination.CourseSchedule,
        onTabSelected = onNavigate,
        showNavigation = true,
        contentColor = MiuixTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxSize()
    ) { navigationPadding ->
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            // Match the surface layer used by the other Miuix first-level
            // pages instead of mixing in the separate background tone.
            containerColor = MiuixTheme.colorScheme.surface,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                MiuixScheduleTopBar(
                    title = displayTitle,
                    weekDates = uiState.pagerMondayDate,
                    firstDayOfWeek = uiState.firstDayOfWeek,
                    isCurrentWeek = isViewingCurrentWeek,
                    showMoreMenu = showMoreMenu,
                    collapsedFraction = (activeScrollOffset / 220f).coerceIn(0f, 1f),
                    onTitleClick = {
                        if (uiState.isSemesterSet) showWeekSelector = true else onNavigate(Destination.Settings)
                    },
                    onBackToCurrentWeek = {
                        scope.launch { pagerState.animateScrollToPage(MIUIX_PAGER_CENTER) }
                    },
                    onSwitchTable = { showTableSwitcher = true },
                    onMore = { showMoreMenu = true }
                )
            }
        ) { scaffoldPadding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = navigationPadding.calculateStartPadding(LayoutDirection.Ltr),
                        top = scaffoldPadding.calculateTopPadding(),
                        end = navigationPadding.calculateEndPadding(LayoutDirection.Ltr),
                        // Keep the pager full-height. The floating navigation
                        // bar is intentionally allowed to overlay the scroll
                        // content; only the trailing content padding reserves
                        // its height below.
                        bottom = scaffoldPadding.calculateBottomPadding()
                    ),
                beyondViewportPageCount = 1
            ) { page ->
                val pageDate = remember(page, uiState.firstDayOfWeek) {
                    weekStart(today, uiState.firstDayOfWeek).plus((page - MIUIX_PAGER_CENTER).toLong() * 7, DateTimeUnit.DAY)
                }
                val courses = uiState.courseCache[pageDate.toString()].orEmpty()
                val scrollState = remember(page) { androidx.compose.foundation.ScrollState(0) }
                LaunchedEffect(scrollState, pagerState.currentPage) {
                    snapshotFlow { scrollState.value }.collect { offset ->
                        if (page == pagerState.currentPage) activeScrollOffset = offset
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        // Reserve exactly the floating navigation bar height at
                        // the end of the schedule. This lets the bar overlay
                        // the tail while avoiding an extra artificial gap.
                        .padding(
                            top = 8.dp,
                            bottom = navigationPadding.calculateBottomPadding()
                        ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MiuixScheduleGrid(
                        style = style,
                        viewState = uiState,
                        date = pageDate,
                        courses = courses,
                        onCourseClick = { selectedBlock = it },
                        onTimeSlotClick = { onNavigate(Destination.TimeSlotSettings) }
                    )
                }
            }
        }
    }

    if (uiState.floatingCourse != null) {
        FloatingCourseBar(
            floatingCourse = uiState.floatingCourse,
            onCancelClick = viewModel::exitFloatingMode,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp + bottomInset)
        )
    }
    }

    if (showTableSwitcher) {
        CourseTablePickerDialog(
            title = stringResource(Res.string.action_select_table),
            onDismissRequest = { showTableSwitcher = false },
            onTableSelected = { table ->
                viewModel.switchCourseTable(table.id)
                showTableSwitcher = false
            }
        )
    }
    if (showWeekSelector) {
        com.xingheyuzhuan.shiguangschedule.ui.schedule.components.WeekSelectorBottomSheet(
            totalWeeks = uiState.totalWeeks,
            currentWeek = uiState.currentWeekNumber ?: 1,
            selectedWeek = uiState.weekIndexInPager ?: (uiState.currentWeekNumber ?: 1),
            onWeekSelected = { week ->
                val selected = uiState.weekIndexInPager ?: 1
                val target = (pagerState.currentPage + week - selected).coerceIn(0, MIUIX_PAGER_PAGE_COUNT - 1)
                scope.launch { pagerState.animateScrollToPage(target) }
                showWeekSelector = false
            },
            onDismissRequest = { showWeekSelector = false }
        )
    }
    if (showMoreMenu) {
        // 参考 NexioSchedule 的右上角下拉菜单。放在同一 Compose 层，
        // 避免 Android Popup 在透明顶栏下被系统当作外部点击关闭。
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(20f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showMoreMenu = false }
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp, end = 12.dp)
                    .width(240.dp)
                    .shadow(12.dp, androidx.compose.foundation.shape.RoundedCornerShape(22.dp))
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(22.dp))
                    .background(MiuixTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                MiuixMenuItem(Res.drawable.double_arrow_24px, "跳转周数") { showMoreMenu = false; showWeekSelector = true }
                MiuixMenuItem(Res.drawable.archive_24px, "课程管理") { showMoreMenu = false; onNavigate(Destination.CourseManagementList) }
                MiuixMenuItem(Res.drawable.palette_24px, "课表外观") { showMoreMenu = false; onNavigate(Destination.StyleSettings) }
            }
        }
    }
    selectedBlock?.let { block ->
        MiuixCourseDetailDialog(
            block = block,
            onDismiss = { selectedBlock = null },
            onEdit = { id ->
                selectedBlock = null
                onNavigate(Destination.AddEditCourse(courseId = id))
            }
        )
    }
}

@Composable
private fun MiuixScheduleTopBar(
    title: String,
    weekDates: LocalDate,
    firstDayOfWeek: Int,
    isCurrentWeek: Boolean,
    showMoreMenu: Boolean,
    collapsedFraction: Float,
    onTitleClick: () -> Unit,
    onBackToCurrentWeek: () -> Unit,
    onSwitchTable: () -> Unit,
    onMore: () -> Unit
) {
    val fraction = collapsedFraction.coerceIn(0f, 1f)
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val days = stringArrayResource(Res.array.week_days_short_names).toList()
    val reorderedDays = rearrangeDays(days, firstDayOfWeek)
    val dates = (0 until reorderedDays.size).map { weekDates.plus(it.toLong(), DateTimeUnit.DAY) }
    AndroidLiquidGlassTopBar(fraction = fraction, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.title4,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.Center).clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp)).combinedClickable(onClick = onTitleClick, onLongClick = onTitleClick).padding(horizontal = 24.dp, vertical = 10.dp)
                )
                if (!isCurrentWeek) {
                    ScheduleTopButton(
                        fraction = fraction,
                        onClick = onBackToCurrentWeek,
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp)
                    ) {
                        Icon(vectorResource(Res.drawable.refresh_24px), "返回本周", tint = MiuixTheme.colorScheme.onSurface)
                    }
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScheduleTopButton(fraction, onSwitchTable) {
                        Icon(vectorResource(Res.drawable.swap_horiz_24px), stringResource(Res.string.action_select_table), tint = MiuixTheme.colorScheme.onSurface)
                    }
                    ScheduleTopButton(fraction, onMore) {
                        Icon(vectorResource(Res.drawable.more_vert_24px), "更多", tint = MiuixTheme.colorScheme.onSurface)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(44.dp).padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(36.dp))
                reorderedDays.forEachIndexed { index, day ->
                    val date = dates[index]
                    val isToday = date == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(day, style = MiuixTheme.textStyles.footnote1, color = if (isToday) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface)
                        Text("%02d/%02d".format(date.month.number, date.day), style = MiuixTheme.textStyles.footnote2, color = if (isToday) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurfaceVariantActions)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleTopButton(
    fraction: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AndroidLiquidGlassButton(fraction = fraction, onClick = onClick, modifier = modifier, content = content)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MiuixScheduleGrid(
    style: ScheduleGridStyleComposed,
    viewState: WeeklyScheduleUiState,
    date: LocalDate,
    courses: List<MergedCourseBlock>,
    onCourseClick: (MergedCourseBlock) -> Unit,
    onTimeSlotClick: () -> Unit
) {
    val weekDays = stringArrayResource(Res.array.week_days_short_names).toList()
    val displayDays = if (viewState.showWeekends) rearrangeDays(weekDays, viewState.firstDayOfWeek) else rearrangeDays(weekDays, viewState.firstDayOfWeek).take(5)
    val is24Hour = style.scheduleMode == ScheduleModeProto.TIME_24H_MODE
    val maxSections = if (is24Hour) 24 else viewState.timeSlots.size.coerceAtLeast(1)
    // Match NexioSchedule's compact Android rhythm while preserving explicit
    // user style overrides.
    val effectiveSectionHeight = if (style.sectionHeight == 70.dp) 54.dp else style.sectionHeight
    val effectiveTimeColumnWidth = if (style.timeColumnWidth == 40.dp) 36.dp else style.timeColumnWidth
    val gridHeight = effectiveSectionHeight * maxSections
    val gridStyle = style.copy(
        hideGridLines = true,
        sectionHeight = effectiveSectionHeight,
        timeColumnWidth = effectiveTimeColumnWidth,
    )
    val density = LocalDensity.current
    val items = remember(courses, viewState.firstDayOfWeek, viewState.showWeekends) {
        calculateSingleSchedulables(courses, viewState.firstDayOfWeek, viewState.showWeekends)
    }

    Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth().height(gridHeight)) {
            Row(Modifier.fillMaxSize()) {
                TimeColumn(
                    style = gridStyle,
                    timeSlots = viewState.timeSlots,
                    maxGridSections = maxSections,
                    is24HourMode = is24Hour,
                    onTimeSlotClicked = onTimeSlotClick,
                    modifier = Modifier.fillMaxHeight(),
                    lineColor = Color.Transparent,
                    currentSectionIndex = viewState.currentSectionIndex,
                    textColor = MiuixTheme.colorScheme.onSurface,
                    subTextColor = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    strokeWidthPx = 0f,
                    useMiuix = true
                )
                Layout(
                    content = {
                        items.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .padding(
                                        if (style.courseBlockOuterPadding < 2.dp) 2.dp
                                        else style.courseBlockOuterPadding
                                    )
                                    .combinedClickable(
                                        onClick = { onCourseClick(item.parentBlock) },
                                        onLongClick = { onCourseClick(item.parentBlock) }
                                    )
                            ) {
                                CourseBlock(
                                    courseWrapper = item.courseWrapper,
                                    isVisualDemoted = item.parentBlock.isVisualDemoted,
                                    style = style,
                                    timeSlots = viewState.timeSlots,
                                    onClick = { onCourseClick(item.parentBlock) }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) { measurables, constraints ->
                    val dayCount = displayDays.size.coerceAtLeast(1)
                    val cellWidth = constraints.maxWidth / dayCount
                    val sectionHeightPx = with(density) { style.sectionHeight.toPx() }
                    val placeables = measurables.mapIndexed { index, measurable ->
                        val item = items[index]
                        val width = (cellWidth / item.subColumnCount.coerceAtLeast(1)).coerceAtLeast(1)
                        val height = (((item.endSection - item.startSection) * sectionHeightPx).roundToInt()).coerceAtLeast(1)
                        measurable.measure(Constraints.fixed(width, height))
                    }
                    layout(constraints.maxWidth, constraints.maxHeight) {
                        placeables.forEachIndexed { index, placeable ->
                            val item = items[index]
                            val x = item.columnIndex * cellWidth + item.subColumnIndex * (cellWidth / item.subColumnCount.coerceAtLeast(1))
                            val y = (item.startSection * sectionHeightPx).roundToInt()
                            placeable.placeRelative(x, y)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiuixMenuItem(icon: org.jetbrains.compose.resources.DrawableResource, title: String, onClick: () -> Unit) {
    BasicComponent(
        title = title,
        onClick = onClick,
        startAction = { Icon(vectorResource(icon), null, tint = MiuixTheme.colorScheme.primary) }
    )
}

@Composable
private fun MiuixCourseDetailDialog(
    block: MergedCourseBlock,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit
) {
    val courses = remember(block) { block.clusterCourses.ifEmpty { block.courses } }
    if (courses.isEmpty()) return

    // Keep the reference project's vertical list structure inside the
    // Miuix dialog available in this KMP version. Each conflicting course is
    // a separate card with an inline edit action.
    WindowDialog(
        show = true,
        title = "课程详情",
        onDismissRequest = onDismiss,
        insideMargin = DpSize(16.dp, 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 56.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            courses.forEach { wrapper ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    cornerRadius = 20.dp,
                    insideMargin = PaddingValues(0.dp),
                    pressFeedbackType = PressFeedbackType.None,
                    showIndication = true,
                    colors = CardDefaults.defaultColors(
                        color = MiuixTheme.colorScheme.surfaceContainer,
                        contentColor = MiuixTheme.colorScheme.onSurface,
                    ),
                    onClick = { onEdit(wrapper.course.id) }
                ) {
                    MiuixCourseDetailItem(wrapper, onEdit)
                }
            }
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun MiuixCourseDetailItem(wrapper: CourseWithWeeks, onEdit: (String) -> Unit) {
    val course = wrapper.course
    val weekNames = stringArrayResource(Res.array.week_days_short_names)
    val dayName = weekNames.getOrNull(course.day - 1) ?: ""
    val time = if (course.isCustomTime) {
        "${course.customStartTime.orEmpty()} - ${course.customEndTime.orEmpty()}"
    } else {
        "第${course.startSection ?: 0}-${course.endSection ?: 0}节"
    }
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(vectorResource(Res.drawable.class_24px), null, tint = MiuixTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                course.name,
                style = MiuixTheme.textStyles.title3,
                color = MiuixTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            IconButton({ onEdit(course.id) }) { Icon(vectorResource(Res.drawable.edit_24px), "编辑", tint = MiuixTheme.colorScheme.primary) }
        }
        if (course.teacher.isNotBlank()) DetailLine(Res.drawable.person_24px, course.teacher)
        if (course.position.isNotBlank()) DetailLine(Res.drawable.location_on_24px, course.position)
        DetailLine(Res.drawable.calendar_today_24px, "${dayName} · ${formatWeekSummary(wrapper.weeks.map { it.weekNumber })}")
        DetailLine(Res.drawable.schedule_24px, time)
        if (!course.remark.isNullOrBlank()) DetailLine(Res.drawable.sticky_note_2_24px, course.remark.orEmpty())
    }
}

private fun formatWeekSummary(weeks: List<Int>): String {
    val sorted = weeks.distinct().sorted()
    if (sorted.isEmpty()) return "未设置周次"
    val ranges = buildList {
        var start = sorted.first()
        var previous = start
        for (week in sorted.drop(1)) {
            if (week == previous + 1) {
                previous = week
            } else {
                add(if (start == previous) "$start" else "$start-$previous")
                start = week
                previous = week
            }
        }
        add(if (start == previous) "$start" else "$start-$previous")
    }
    return ranges.joinToString(", ") + "周"
}

@Composable
private fun DetailLine(icon: org.jetbrains.compose.resources.DrawableResource, text: String) {
    Row(Modifier.fillMaxWidth().padding(top = 9.dp), verticalAlignment = Alignment.Top) {
        Icon(vectorResource(icon), null, tint = MiuixTheme.colorScheme.onSurfaceVariantActions, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, style = MiuixTheme.textStyles.body1, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
    }
}
