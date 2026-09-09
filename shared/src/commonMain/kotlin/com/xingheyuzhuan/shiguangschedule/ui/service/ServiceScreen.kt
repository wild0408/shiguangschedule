package com.xingheyuzhuan.shiguangschedule.ui.service

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.ui.components.AdaptiveNavigationScaffold
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.chevron_right_24px
import shiguangschedule.shared.generated.resources.desc_grade_center
import shiguangschedule.shared.generated.resources.item_grade_center
import shiguangschedule.shared.generated.resources.list_alt_24px
import shiguangschedule.shared.generated.resources.nav_service
import shiguangschedule.shared.generated.resources.item_electricity
import shiguangschedule.shared.generated.resources.desc_electricity
import shiguangschedule.shared.generated.resources.electricity_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceScreen(
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit
) {
    val miuix = LocalUiStyle.current == AppUiStyle.MIUIX
    AdaptiveNavigationScaffold(
        currentDestination = Destination.Service,
        onTabSelected = onNavigate
    ) { navigationPadding ->
        if (miuix) {
            top.yukonga.miuix.kmp.basic.Scaffold(
                containerColor = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface,
                topBar = {
                    top.yukonga.miuix.kmp.basic.TopAppBar(
                        title = stringResource(Res.string.nav_service),
                        largeTitle = stringResource(Res.string.nav_service),
                        color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.surface,
                        defaultWindowInsetsPadding = true,
                    )
                },
            ) { contentPadding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(contentPadding).padding(horizontal = 16.dp),
                ) {
                    item { MiuixServiceCard(onClick = { onNavigate(Destination.GradeCenter) }, icon = Res.drawable.list_alt_24px, title = stringResource(Res.string.item_grade_center), description = stringResource(Res.string.desc_grade_center)) }
                    item { MiuixServiceCard(onClick = { onNavigate(Destination.ElectricityCenter) }, icon = Res.drawable.electricity_24px, title = stringResource(Res.string.item_electricity), description = stringResource(Res.string.desc_electricity)) }
                }
            }
        } else Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(Res.string.nav_service)) }
                )
            }
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(navigationPadding)
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Card(
                        onClick = { onNavigate(Destination.GradeCenter) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        RowContent(
                            icon = Res.drawable.list_alt_24px,
                            title = stringResource(Res.string.item_grade_center),
                            description = stringResource(Res.string.desc_grade_center)
                        )
                    }
                }
                item {
                    Card(onClick = { onNavigate(Destination.ElectricityCenter) }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        RowContent(Res.drawable.electricity_24px, stringResource(Res.string.item_electricity), stringResource(Res.string.desc_electricity))
                    }
                }
            }
        }
    }
}

@Composable
private fun MiuixServiceCard(onClick: () -> Unit, icon: org.jetbrains.compose.resources.DrawableResource, title: String, description: String) {
    top.yukonga.miuix.kmp.basic.Card(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        onClick = onClick,
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            top.yukonga.miuix.kmp.basic.Icon(vectorResource(icon), contentDescription = null, modifier = Modifier.size(30.dp), tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                top.yukonga.miuix.kmp.basic.Text(title, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.title3)
                top.yukonga.miuix.kmp.basic.Text(description, style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2, color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary)
            }
            top.yukonga.miuix.kmp.basic.Icon(vectorResource(Res.drawable.chevron_right_24px), contentDescription = null, tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantActions)
        }
    }
}

@Composable
private fun RowContent(
    icon: org.jetbrains.compose.resources.DrawableResource,
    title: String,
    description: String
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = vectorResource(icon),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = vectorResource(Res.drawable.chevron_right_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
