package com.xingheyuzhuan.shiguangschedule.ui.service

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.ui.components.AdaptiveNavigationScaffold
import org.jetbrains.compose.resources.stringResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.nav_service

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceScreen(
    onNavigate: (Destination) -> Unit,
    onBack: () -> Unit
) {
    AdaptiveNavigationScaffold(
        currentDestination = Destination.Service,
        onTabSelected = onNavigate
    ) { navigationPadding ->
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(Res.string.nav_service)) }
                )
            }
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(navigationPadding)
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(Res.string.nav_service))
            }
        }
    }
}
