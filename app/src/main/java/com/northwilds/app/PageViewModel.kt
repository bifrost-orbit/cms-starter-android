package com.northwilds.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bifrostorbit.cms.CmsOrbitException
import com.bifrostorbit.cms.CmsPage
import com.bifrostorbit.cms.CmsResolution
import kotlinx.coroutines.launch

sealed interface PageState {
    data object Loading : PageState
    data class Ready(val page: CmsPage) : PageState
    data object NotFound : PageState
    data class Elsewhere(val url: String) : PageState
    data class Failed(val message: String) : PageState
}

class PageViewModel(private val path: String) : ViewModel() {
    var state by mutableStateOf<PageState>(PageState.Loading)
        private set
    var refreshing by mutableStateOf(false)
        private set

    fun load(fresh: Boolean = false) {
        viewModelScope.launch {
            refreshing = fresh
            state = try {
                show(path, fresh)
            } catch (error: CmsOrbitException) {
                state as? PageState.Ready ?: PageState.Failed("We couldn't load this page. Check your connection and try again.")
            }
            refreshing = false
        }
    }

    private suspend fun show(path: String, fresh: Boolean, hops: Int = 0): PageState =
        when (val result = PageCache.resolve(path, fresh)) {
            is CmsResolution.Page -> PageState.Ready(result.page)
            is CmsResolution.Redirect ->
                if (result.to.startsWith("/") && hops < 5) show(result.to, fresh, hops + 1) else PageState.Elsewhere(result.to)
            CmsResolution.Gone, CmsResolution.NotFound -> PageState.NotFound
        }
}
