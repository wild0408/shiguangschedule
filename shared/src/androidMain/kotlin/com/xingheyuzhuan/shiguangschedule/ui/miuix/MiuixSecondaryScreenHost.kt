package com.xingheyuzhuan.shiguangschedule.ui.miuix

import androidx.compose.runtime.Composable
import com.xingheyuzhuan.shiguangschedule.Destination
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
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixContributionScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixOpenSourceLicensesScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixUpdateRepoScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixBackupScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixManageCourseTablesScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixStyleSettingsScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixCourseNameListScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixCourseInstanceListScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixAddEditCourseScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixTimeScheduleManagementScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixSchoolSelectionListScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixAdapterSelectionScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixComboScheduleEditScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixSingleScheduleEditScreen
import com.xingheyuzhuan.shiguangschedule.ui.miuix.screens.MiuixWebViewScreen

@Composable
internal fun MiuixSecondaryScreenHost(
    destination: Destination,
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit,
) {
    when (destination) {
        Destination.SchoolSelectionListScreen -> MiuixSchoolSelectionListScreen(
            onNavigate = onNavigate,
            onBack = onBack,
        )
        is Destination.AdapterSelection -> MiuixAdapterSelectionScreen(
            onNavigate = onNavigate,
            onBack = onBack,
            schoolId = destination.schoolId,
            schoolName = destination.schoolName,
            categoryNumber = destination.categoryNumber,
            resourceFolder = destination.resourceFolder,
        )
        Destination.TimeScheduleManagement -> MiuixTimeScheduleManagementScreen(
            onBack = onBack,
            onEditSingleSchedule = { tableId, isPublic, copyFromId ->
                onNavigate(Destination.SingleScheduleEdit(tableId, isPublic, copyFromId))
            },
            onEditComboSchedule = { comboId, copyFromId ->
                onNavigate(Destination.ComboScheduleEdit(comboId, copyFromId))
            },
        )
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
        Destination.StyleSettings -> MiuixStyleSettingsScreen(onBack)
        Destination.CourseManagementList -> MiuixCourseNameListScreen(onNavigate, onBack)
        is Destination.CourseManagementDetail -> MiuixCourseInstanceListScreen(destination.courseName, onBack, onNavigate)
        is Destination.AddEditCourse -> MiuixAddEditCourseScreen(onBack, destination.courseId)
        is Destination.WebView -> MiuixWebViewScreen(
            onNavigate = onNavigate,
            onBack = onBack,
            initialUrl = destination.initialUrl,
            assetJsPath = destination.assetJsPath,
        )
        is Destination.SingleScheduleEdit -> MiuixSingleScheduleEditScreen(
            tableId = destination.tableId,
            isPublic = destination.isPublic,
            copyFromId = destination.copyFromId,
            onBack = onBack,
        )
        is Destination.ComboScheduleEdit -> MiuixComboScheduleEditScreen(
            comboId = destination.comboId,
            copyFromId = destination.copyFromId,
            onBack = onBack,
        )
        // Main destinations are consumed by MiuixMainShell and never reach this host.
        else -> Unit
    }
}
