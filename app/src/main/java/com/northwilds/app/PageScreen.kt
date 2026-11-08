package com.northwilds.app

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bifrostorbit.cms.CmsPage
import com.northwilds.app.blocks.CmsBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageScreen(path: String, onBack: (() -> Unit)?) {
    val model = viewModel { PageViewModel(path) }
    val state = model.state
    val openLink = LocalOpenLink.current

    LifecycleStartEffect(model) {
        model.load()
        onStopOrDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text((state as? PageState.Ready)?.page?.let { if (it.draft) "${it.name} (draft)" else it.name } ?: "Northwilds") },
                navigationIcon = { if (onBack != null) TextButton(onClick = onBack) { Text("Back") } },
                actions = { if (state is PageState.Ready) ShareButton(state.page) },
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (state) {
                PageState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is PageState.Ready -> PullToRefreshBox(isRefreshing = model.refreshing, onRefresh = { model.load(fresh = true) }) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(state.page.components, key = { it.id }) { CmsBlock(it) }
                    }
                }
                PageState.NotFound -> Message("We couldn't find this page.", "Go to the home page") { openLink("/") }
                is PageState.Elsewhere -> LaunchedEffect(state.url) {
                    openLink(state.url)
                    onBack?.invoke()
                }
                is PageState.Failed -> Message(state.message, "Try again") { model.load(fresh = true) }
            }
        }
    }
}

@Composable
private fun ShareButton(page: CmsPage) {
    val context = LocalContext.current
    TextButton(onClick = {
        val share = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TITLE, page.seo.title.ifEmpty { page.name })
            .putExtra(Intent.EXTRA_TEXT, listOf(page.seo.description, page.seo.canonical).filter { it.isNotEmpty() }.joinToString("\n"))
        context.startActivity(Intent.createChooser(share, null))
    }) { Text("Share") }
}

@Composable
private fun Message(text: String, action: String, onAction: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text)
        Button(onClick = onAction) { Text(action) }
    }
}
