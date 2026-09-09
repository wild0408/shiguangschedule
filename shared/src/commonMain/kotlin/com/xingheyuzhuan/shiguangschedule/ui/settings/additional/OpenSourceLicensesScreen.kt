package com.xingheyuzhuan.shiguangschedule.ui.settings.additional

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.xingheyuzhuan.shiguangschedule.data.model.AppUiStyle
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalUiStyle
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.a11y_back
import shiguangschedule.shared.generated.resources.arrow_back_24px
import shiguangschedule.shared.generated.resources.text_loading_failed
import shiguangschedule.shared.generated.resources.title_open_source_licenses
import top.yukonga.miuix.kmp.theme.MiuixTheme

// 定义加载状态密封接口
private sealed interface ResourceState<out T> {
    data object Loading : ResourceState<Nothing>
    data class Success<T>(val data: T) : ResourceState<T>
    data class Error(val message: String) : ResourceState<Nothing>
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun OpenSourceLicensesScreen(onBack: () -> Unit) {

    val librariesState by produceState<ResourceState<Libs?>>(initialValue = ResourceState.Loading) {
        value = try {
            val jsonString = Res.readBytes("files/aboutlibraries.json").decodeToString()
            val libs = Libs.Builder().withJson(jsonString).build()
            ResourceState.Success(libs)
        } catch (e: Exception) {
            ResourceState.Error(e.message ?: e.toString())
        }
    }
    val useMiuix = LocalUiStyle.current == AppUiStyle.MIUIX

    if (useMiuix) {
        MiuixOpenSourceLicensesScreen(onBack, librariesState)
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.title_open_source_licenses))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = stringResource(Res.string.a11y_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = librariesState) {
                is ResourceState.Loading -> {
                    CircularProgressIndicator()
                }
                is ResourceState.Success -> {
                    LibrariesContainer(
                        libraries = state.data,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is ResourceState.Error -> {
                    Text(
                        text = stringResource(Res.string.text_loading_failed, state.message),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiuixOpenSourceLicensesScreen(
    onBack: () -> Unit,
    librariesState: ResourceState<Libs?>,
) {
    val title = stringResource(Res.string.title_open_source_licenses)
    val scrollBehavior = top.yukonga.miuix.kmp.basic.MiuixScrollBehavior()
    var selectedLibrary by remember { mutableStateOf<Library?>(null) }

    top.yukonga.miuix.kmp.basic.Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MiuixTheme.colorScheme.surface,
        topBar = {
            top.yukonga.miuix.kmp.basic.TopAppBar(
                title = title,
                largeTitle = title,
                color = MiuixTheme.colorScheme.surface,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    top.yukonga.miuix.kmp.basic.IconButton(onBack) {
                        top.yukonga.miuix.kmp.basic.Icon(
                            vectorResource(Res.drawable.arrow_back_24px),
                            stringResource(Res.string.a11y_back),
                            tint = MiuixTheme.colorScheme.onSurface
                        )
                    }
                },
                defaultWindowInsetsPadding = true,
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
            when (librariesState) {
                ResourceState.Loading -> top.yukonga.miuix.kmp.basic.CircularProgressIndicator()
                is ResourceState.Error -> top.yukonga.miuix.kmp.basic.Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    insideMargin = PaddingValues(16.dp),
                ) {
                    top.yukonga.miuix.kmp.basic.Text(
                        stringResource(Res.string.text_loading_failed, librariesState.message),
                        color = MiuixTheme.colorScheme.error,
                        style = MiuixTheme.textStyles.body1,
                    )
                }
                is ResourceState.Success -> {
                    val libraries = librariesState.data?.libraries.orEmpty()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    ) {
                        items(libraries, key = { it.uniqueId }) { library ->
                            MiuixLibraryCard(library) { selectedLibrary = library }
                        }
                    }
                }
            }
        }

        selectedLibrary?.let { library ->
            MiuixLibraryDetailsDialog(library) { selectedLibrary = null }
        }
    }
}

@Composable
private fun MiuixLibraryCard(library: Library, onClick: () -> Unit) {
    val licenseText = library.licenses.joinToString { it.name }.ifBlank { "未声明许可证" }
    top.yukonga.miuix.kmp.basic.Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        onClick = onClick,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                top.yukonga.miuix.kmp.basic.Text(library.name, style = MiuixTheme.textStyles.body1, fontWeight = FontWeight.Medium)
                top.yukonga.miuix.kmp.basic.Text(
                    listOfNotNull(library.artifactVersion, licenseText).joinToString(" · "),
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
            top.yukonga.miuix.kmp.basic.Text("›", style = MiuixTheme.textStyles.title3, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
        }
    }
}

@Composable
private fun MiuixLibraryDetailsDialog(library: Library, onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val licenseText = library.licenses.joinToString { it.name }.ifBlank { "未声明许可证" }
    top.yukonga.miuix.kmp.overlay.OverlayDialog(
        title = library.name,
        summary = library.artifactVersion?.let { "版本 $it" }.orEmpty(),
        show = true,
        onDismissRequest = onDismiss,
    ) {
        Column(Modifier.fillMaxWidth().heightIn(max = 430.dp).verticalScroll(rememberScrollState())) {
            library.description?.takeIf { it.isNotBlank() }?.let {
                top.yukonga.miuix.kmp.basic.Text(it, style = MiuixTheme.textStyles.body1)
                Spacer(Modifier.height(14.dp))
            }
            top.yukonga.miuix.kmp.basic.SmallTitle("许可证")
            top.yukonga.miuix.kmp.basic.Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = PaddingValues(14.dp),
            ) {
                top.yukonga.miuix.kmp.basic.Text(licenseText, style = MiuixTheme.textStyles.body1)
            }
            library.website?.takeIf { it.isNotBlank() }?.let { website ->
                Spacer(Modifier.height(14.dp))
                top.yukonga.miuix.kmp.basic.TextButton(
                    text = "打开项目主页",
                    onClick = { uriHandler.openUri(website) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        }
    }
}
