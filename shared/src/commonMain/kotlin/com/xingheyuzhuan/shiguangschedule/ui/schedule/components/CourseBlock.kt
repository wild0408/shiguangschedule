package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.xingheyuzhuan.shiguangschedule.data.db.main.CourseWithWeeks
import com.xingheyuzhuan.shiguangschedule.data.db.main.TimeSlot
import com.xingheyuzhuan.shiguangschedule.data.model.schedule_style.BorderTypeProto
import com.xingheyuzhuan.shiguangschedule.data.model.schedule_style.ScheduleModeProto
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalIsDarkTheme
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import androidx.compose.foundation.shape.CircleShape

@Composable
fun CourseBlock(
    courseWrapper: CourseWithWeeks,
    isVisualDemoted: Boolean,
    style: ScheduleGridStyleComposed,
    timeSlots: List<TimeSlot>,
    modifier: Modifier = Modifier,
    isFloating: Boolean = false, // 标记当前块是否处于长按选中/悬浮状态
    onClick: () -> Unit = {}
) {
    val course = courseWrapper.course
    val isDarkTheme = LocalIsDarkTheme.current

    // 颜色适配
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX
    val baseCourseColor = resolveCourseColor(course.colorInt, style.courseColorMaps, isDarkTheme, useMiuix)
    // NexioSchedule uses a soft translucent surface and keeps the label in the
    // same hue family. The legacy default alpha is 1.0f, so it must not be used
    // directly for the Miuix surface.
    val currentAlpha = if (isFloating) 0.95f else if (useMiuix) {
        (0.16f + style.courseBlockAlpha.coerceIn(0f, 1f) * 0.12f).coerceIn(0.12f, 0.32f)
    } else {
        style.courseBlockAlpha
    }
    val blockColor = baseCourseColor.copy(alpha = currentAlpha)
    val miuixTextColor = miuixCourseTextColor(baseCourseColor, isDarkTheme)
    val textColor = style.courseTextColor ?: if (useMiuix) miuixTextColor else MaterialTheme.colorScheme.onSurface

    // 字体大小
    val s13 = (13 * style.fontScale).sp
    val s10 = (10 * style.fontScale).sp

    // 核心分支逻辑：判断 24小时模式 与 节次模式 的时间文本渲染
    val customStartTime = course.customStartTime
    val customEndTime = course.customEndTime
    val customTimeString = if (customStartTime != null && customEndTime != null) "$customStartTime - $customEndTime" else null
    val isCustomTimeCourse = customTimeString != null

    val timeTextToShow = if (style.scheduleMode == ScheduleModeProto.TIME_24H_MODE) {
        // 24小时绝对时间轴模式：全部课程都显示起止时间
        if (isCustomTimeCourse) {
            customTimeString
        } else {
            val startSlot = timeSlots.find { it.number == course.startSection }
            val endSlot = timeSlots.find { it.number == course.endSection }
            if (startSlot != null && endSlot != null) "${startSlot.startTime} - ${endSlot.endTime}" else null
        }
    } else {
        // 传统节次模式：只有自定义课程显示起止时间；普通节次课程只有在开启展示开始时间时才显示开始时间
        if (isCustomTimeCourse) {
            customTimeString
        } else if (style.showStartTime) {
            timeSlots.find { it.number == course.startSection }?.startTime
        } else {
            null
        }
    }

    // 边框样式配置
    val borderColor = if (isFloating) {
        if (useMiuix) MiuixTheme.colorScheme.primary else Color(0xFF2196F3)
    } else if (useMiuix) MiuixTheme.colorScheme.outline else MaterialTheme.colorScheme.outline
    val borderWidth = if (isFloating) 2.dp else 1.dp
    val borderAlpha = if (isFloating) 1.0f else style.courseBlockAlpha
    val shape = RoundedCornerShape(style.courseBlockCornerRadius)

    val borderModifier = if (useMiuix && !isFloating) {
        // NexioSchedule 的 Miuix 课程卡片依靠色块和圆角区分，不额外绘制分割边框。
        Modifier
    } else when (style.borderType) {
        BorderTypeProto.BORDER_TYPE_SOLID -> {
            Modifier.border(borderWidth, borderColor.copy(alpha = borderAlpha), shape)
        }
        BorderTypeProto.BORDER_TYPE_DASHED -> {
            Modifier.drawBehind {
                val strokeWidth = borderWidth.toPx()
                val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
                drawOutline(
                    outline = shape.createOutline(size, layoutDirection, this),
                    color = borderColor.copy(alpha = borderAlpha),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, pathEffect = dashPathEffect)
                )
            }
        }
        else -> {
            if (isFloating) Modifier.border(borderWidth, borderColor, shape) else Modifier
        }
    }

    val horizontalAlignment = if (style.textAlignCenterHorizontal) Alignment.CenterHorizontally else Alignment.Start
    val verticalArrangement = if (style.textAlignCenterVertical) Arrangement.Center else Arrangement.Top
    val textAlign = if (style.textAlignCenterHorizontal) TextAlign.Center else TextAlign.Start

    // 选中捏起时，增加三维物理阴影
    val floatingShadowModifier = if (isFloating) {
        Modifier.shadow(elevation = 8.dp, shape = shape, clip = false)
    } else {
        Modifier
    }

    val content: @Composable () -> Unit = {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            Text(
                text = course.name,
                fontSize = (12.7f * style.fontScale).sp,
                lineHeight = (14.2f * style.fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = textAlign,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            if (!style.hideLocation && course.position.isNotBlank()) {
                Text(
                    text = if (style.removeLocationAt) course.position else "@${course.position}",
                    fontSize = s10,
                    lineHeight = 12.sp,
                    color = textColor.copy(alpha = 0.82f),
                    textAlign = textAlign,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (!style.hideTeacher && course.teacher.isNotBlank()) {
                Text(
                    text = course.teacher,
                    fontSize = s10,
                    lineHeight = 12.sp,
                    color = textColor.copy(alpha = 0.82f),
                    textAlign = textAlign,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (useMiuix && !isFloating) {
        Card(
            modifier = modifier.fillMaxSize().clip(shape),
            cornerRadius = style.courseBlockCornerRadius,
            insideMargin = PaddingValues(0.dp),
            pressFeedbackType = PressFeedbackType.Sink,
            showIndication = true,
            colors = CardDefaults.defaultColors(
                color = blockColor,
                contentColor = textColor
            ),
            onClick = onClick
        ) {
            Box(Modifier.fillMaxSize()) {
                content()
                if (timeTextToShow != null && course.isCustomTime) {
                    Text(
                        text = timeTextToShow,
                        fontSize = 8.sp,
                        lineHeight = 10.sp,
                        color = textColor.copy(alpha = 0.78f),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp)
                            .background(textColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                        maxLines = 1
                    )
                }
                if (courseWrapper.weeks.size > 1) {
                    Box(
                        Modifier.align(Alignment.BottomEnd).padding(5.dp).size(7.dp)
                            .background(textColor.copy(alpha = 0.82f), CircleShape)
                    )
                }
                if (isVisualDemoted) {
                    Box(
                        Modifier.fillMaxSize().background(
                            (if (isDarkTheme) Color.Black else Color.White).copy(alpha = 0.42f)
                        )
                    )
                }
            }
        }
    } else {
        Box(
            modifier = modifier
                .then(floatingShadowModifier)
                .fillMaxSize()
                .then(borderModifier)
                .clip(shape)
                .background(color = blockColor)
        ) {
            content()
            if (isVisualDemoted && !isFloating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = (if (isDarkTheme) Color.Black else Color.White).copy(alpha = 0.618f))
                        .drawBehind {
                            val stripeWidth = 5.dp.toPx()
                            val stripeColor = (if (isDarkTheme) Color.White else Color.Black).copy(alpha = 0.06f)
                            val brush = Brush.linearGradient(
                                0.0f to stripeColor, 0.45f to stripeColor,
                                0.55f to Color.Transparent, 1.0f to Color.Transparent,
                                start = Offset(0f, 0f), end = Offset(stripeWidth, stripeWidth), tileMode = TileMode.Repeated
                            )
                            drawRect(brush = brush)
                        }
                )
            }
        }
    }
}
