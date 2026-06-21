package org.wikipedia.page.linkpreview

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wikipedia.BuildConfig
import org.wikipedia.auth.AccountUtil
import org.wikipedia.database.AppDatabase
import org.wikipedia.dataclient.ServiceFactory
import org.wikipedia.dataclient.page.PageSummary
import org.wikipedia.history.HistoryEntry
import org.wikipedia.page.PageTitle
import org.wikipedia.settings.Prefs
import org.wikipedia.util.log.L
import org.wikipedia.watchlist.WatchlistExpiry
import org.wikipedia.watchlist.WatchlistViewModel

class LinkPreviewViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<LinkPreviewViewState>(LinkPreviewViewState.Loading)
    val uiState = _uiState.asStateFlow()
    val historyEntry = savedStateHandle.get<HistoryEntry>(LinkPreviewDialog.ARG_ENTRY)!!
    var pageTitle = historyEntry.title
    var location = savedStateHandle.get<Location>(LinkPreviewDialog.ARG_LOCATION)
    val fromPlaces = historyEntry.source == HistoryEntry.SOURCE_PLACES
    val lastKnownLocation = savedStateHandle.get<Location>(LinkPreviewDialog.ARG_LAST_KNOWN_LOCATION)
    var isInReadingList = false

    var isWatched = false
    var hasWatchlistExpiry = false

    init {
        loadContent()
    }

    private fun loadContent() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            _uiState.value = LinkPreviewViewState.Error(throwable)
        }) {
            val watchedCall = async { if (fromPlaces && AccountUtil.isLoggedIn) ServiceFactory.get(pageTitle.wikiSite).getWatchedStatus(pageTitle.prefixedText) else null }

            val summary: PageSummary
            if (BuildConfig.HAS_RESTBASE) {
                val summaryCall = async { ServiceFactory.getRest(pageTitle.wikiSite).getSummaryResponse(pageTitle.prefixedText) }
                val response = summaryCall.await()
                summary = response.body()!!
                val oldFragment = pageTitle.fragment
                pageTitle = PageTitle(summary.apiTitle, pageTitle.wikiSite, summary.thumbnailUrl, summary.description, summary.displayTitle)
                if (!response.raw().request.url.fragment.isNullOrEmpty()) {
                    pageTitle.fragment = response.raw().request.url.fragment
                } else if (!oldFragment.isNullOrEmpty()) {
                    pageTitle.fragment = oldFragment
                }
            } else {
                summary = ServiceFactory.getPageSummary(pageTitle.wikiSite, pageTitle.prefixedText)
                pageTitle = PageTitle(summary.apiTitle, pageTitle.wikiSite, summary.thumbnailUrl, summary.description, summary.displayTitle)
            }

            if (fromPlaces) {
                isWatched = watchedCall.await()?.query?.firstPage()?.watched ?: false
                val readingList = AppDatabase.instance.readingListPageDao().findPageInAnyList(pageTitle)
                isInReadingList = readingList != null
            }

            if (location == null) {
                location = summary.coordinates
            }

            _uiState.value = LinkPreviewViewState.Content(summary)
        }
    }

    fun loadGallery() {
        if (Prefs.isImageDownloadEnabled && !pageTitle.isFilePage) {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                L.w("Failed to fetch gallery collection.", throwable)
            }) {
                val mediaList = ServiceFactory.getRest(pageTitle.wikiSite)
                    .getMediaList(pageTitle.prefixedText)
                val maxImages = 10
                val items = mediaList.getItems("image", "video").asReversed()
                val titleList =
                    items.filter { it.showInGallery }.map { it.title }.take(maxImages)
                if (titleList.isEmpty()) {
                    _uiState.value = LinkPreviewViewState.Completed
                } else {
                    val response = ServiceFactory.get(
                        pageTitle.wikiSite
                    ).getImageInfo(
                        titleList.joinToString("|"),
                        pageTitle.wikiSite.languageCode
                    )
                    val pageList =
                        response.query?.pages?.filter { it.imageInfo() != null }.orEmpty()
                    _uiState.value = LinkPreviewViewState.Gallery(pageList)
                }
            }
        } else {
            _uiState.value = LinkPreviewViewState.Completed
        }
    }

    fun watchOrUnwatch(unwatch: Boolean) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            L.e("Failed to fetch watch status.", throwable)
        }) {
            val pair = WatchlistViewModel.watchPageTitle(this, pageTitle, unwatch, WatchlistExpiry.NEVER, isWatched, pageTitle.namespace().talk())
            isWatched = pair.first
            _uiState.value = LinkPreviewViewState.Watch(pair)
        }
    }
}
