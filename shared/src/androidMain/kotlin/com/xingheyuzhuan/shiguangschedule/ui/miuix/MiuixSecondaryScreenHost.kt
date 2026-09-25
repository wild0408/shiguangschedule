package com.xingheyuzhuan.shiguangschedule.ui.miuix

import androidx.compose.runtime.Composable
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.ScreenContent
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixGradeCenterScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixElectricityCenterScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixQuickActionsScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixTweakScheduleScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixQuickDeleteScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixNotificationSettingsScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixCourseTableConversionScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixLanguageSettingsScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixThemeSettingsScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixMoreOptionsScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixTimeSlotManagementScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixContributionScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixOpenSourceLicensesScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixUpdateRepoScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixBackupScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixManageCourseTablesScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixCourseNameListScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixCourseInstanceListScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixAddEditCourseScreen

@Composable
internal fun MiuixSecondaryScreenHost(
    destination: Destination,
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit,
) {
    when (destination) {
        Destination.TimeSlotSettings -> MiuixTimeSlotManagementScreen(onBack)
        Destination.GradeCenter -> MiuixGradeCenterScreen(onNavigate, onBack)
        Destination.ElectricityCenter -> MiuixElectricityCenterScreen(onBack)
        Destination.QuickActions -> MiuixQuickActionsScreen(onNavigate, onBack)
        Destination.TweakSchedule -> MiuixTweakScheduleScreen(onBack)
        Destination.QuickDelete -> MiuixQuickDeleteScreen(onBack)
        Destination.NotificationSettings -> MiuixNotificationSettingsScreen(onBack)
        Destination.CourseTableConversion -> MiuixCourseTableConversionScreen(onNavigate, onBack)
        Destination.LanguageSettings -> MiuixLanguageSettingsScreen(onBack)
        Destination.ThemeSettings -> MiuixThemeSettingsScreen(onBack)
        Destination.MoreOptions -> MiuixMoreOptionsScreen(onNavigate, onBack)
        Destination.ContributionList -> MiuixContributionScreen(onBack)
        Destination.OpenSourceLicenses -> MiuixOpenSourceLicensesScreen(onBack)
        Destination.UpdateRepo -> MiuixUpdateRepoScreen(onBack)
        Destination.BackupAndRestore -> MiuixBackupScreen(onBack)
        Destination.ManageCourseTables -> MiuixManageCourseTablesScreen(onBack)
        Destination.CourseManagementList -> MiuixCourseNameListScreen(onNavigate, onBack)
        is Destination.CourseManagementDetail -> MiuixCourseInstanceListScreen(destination.courseName, onBack, onNavigate)
        is Destination.AddEditCourse -> MiuixAddEditCourseScreen(onBack, destination.courseId)
        else -> ScreenContent(destination, onNavigate, onBack)
    }
}
