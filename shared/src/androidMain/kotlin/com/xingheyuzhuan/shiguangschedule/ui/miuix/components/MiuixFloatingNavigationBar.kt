package com.xingheyuzhuan.shiguangschedule.ui.miuix.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.xingheyuzhuan.shiguangschedule.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.account_circle_24px
import shiguangschedule.shared.generated.resources.account_circle_filled_24px
import shiguangschedule.shared.generated.resources.nav_course_schedule
import shiguangschedule.shared.generated.resources.nav_service
import shiguangschedule.shared.generated.resources.nav_settings
import shiguangschedule.shared.generated.resources.nav_today_schedule
import shiguangschedule.shared.generated.resources.service_24px
import shiguangschedule.shared.generated.resources.service_filled_24px
import shiguangschedule.shared.generated.resources.view_agenda_24px
import shiguangschedule.shared.generated.resources.view_agenda_filled_24px
import shiguangschedule.shared.generated.resources.view_week_24px
import shiguangschedule.shared.generated.resources.view_week_filled_24px
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
internal fun MiuixFloatingNavigationBar(
    selectedDestination: Destination,
    onDestinationSelected: (Destination) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        NavigationBar(
            color = MiuixTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height(64.dp)
                .shadow(12.dp, RoundedCornerShape(28.dp), clip = false)
                .clip(RoundedCornerShape(28.dp)),
            showDivider = false,
            defaultWindowInsetsPadding = false,
        ) {
            NavigationBarItem(
                selected = selectedDestination == Destination.TodaySchedule,
                onClick = { onDestinationSelected(Destination.TodaySchedule) },
                icon = vectorResource(
                    if (selectedDestination == Destination.TodaySchedule) Res.drawable.view_agenda_filled_24px
                    else Res.drawable.view_agenda_24px
                ),
                label = stringResource(Res.string.nav_today_schedule),
            )
            NavigationBarItem(
                selected = selectedDestination == Destination.CourseSchedule,
                onClick = { onDestinationSelected(Destination.CourseSchedule) },
                icon = vectorResource(
                    if (selectedDestination == Destination.CourseSchedule) Res.drawable.view_week_filled_24px
                    else Res.drawable.view_week_24px
                ),
                label = stringResource(Res.string.nav_course_schedule),
            )
            NavigationBarItem(
                selected = selectedDestination == Destination.Service,
                onClick = { onDestinationSelected(Destination.Service) },
                icon = vectorResource(
                    if (selectedDestination == Destination.Service) Res.drawable.service_filled_24px
                    else Res.drawable.service_24px
                ),
                label = stringResource(Res.string.nav_service),
            )
            NavigationBarItem(
                selected = selectedDestination == Destination.Settings,
                onClick = { onDestinationSelected(Destination.Settings) },
                icon = vectorResource(
                    if (selectedDestination == Destination.Settings) Res.drawable.account_circle_filled_24px
                    else Res.drawable.account_circle_24px
                ),
                label = stringResource(Res.string.nav_settings),
            )
        }
    }
}
