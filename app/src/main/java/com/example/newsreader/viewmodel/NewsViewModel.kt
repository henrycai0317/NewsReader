package com.example.newsreader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsreader.data.model.ApiResult
import com.example.newsreader.data.model.Article
import com.example.newsreader.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsUiState(
    val articles: List<Article> = emptyList(),
    val totalResults: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    // 內部狀態
    private val _uiState = MutableStateFlow(NewsUiState())

    // 外部可觀察的狀態
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    // 搜尋 Job
    private var searchJob: Job? = null

    // 初始化時自動載入
    init {
        loadTopHeadlinesWithFlow()
    }

    /**
     * 使用 Flow 載入頭條新聞
     * Flow 會自動發射 Loading -> Success/Error
     */
    private fun loadTopHeadlinesWithFlow(category: String? = null) {
        viewModelScope.launch {
            repository.getTopHeadlinesFlow(category)
                .collect { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = true,
                                    error = null,
                                    selectedCategory = category
                                )
                            }
                        }
                        is ApiResult.Success -> {
                            val response = result.data
                            _uiState.update {
                                it.copy(
                                    articles = response.articles,
                                    totalResults = response.totalResults,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is ApiResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
        }
    }

    /**
     * 下拉刷新 - 使用 suspend function
     * 不使用 Flow，因為我們想手動控制 isRefreshing 狀態
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }

            val category = _uiState.value.selectedCategory
            when (val result = repository.refreshTopHeadlines(category)) {
                is ApiResult.Success -> {
                    val response = result.data
                    _uiState.update {
                        it.copy(
                            articles = response.articles,
                            totalResults = response.totalResults,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
                is ApiResult.Loading -> {
                    // Not used in suspend version
                }
            }
        }
    }

    /**
     * 搜尋新聞 - 使用 suspend function with debounce
     */
    fun searchNews(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // 取消前一次搜尋
        searchJob?.cancel()

        if (query.isBlank()) {
            loadTopHeadlinesWithFlow()
            return
        }

        // Debounce: 等待 500ms
        searchJob = viewModelScope.launch {
            delay(500)

            _uiState.update { it.copy(isSearching = true, error = null) }

            when (val result = repository.searchNews(query)) {
                is ApiResult.Success -> {
                    val response = result.data
                    _uiState.update {
                        it.copy(
                            articles = response.articles,
                            totalResults = response.totalResults,
                            isSearching = false,
                            error = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            error = result.message
                        )
                    }
                }
                is ApiResult.Loading -> {
                    // Not used
                }
            }
        }
    }

    /**
     * 清除搜尋
     */
    fun clearSearch() {
        searchJob?.cancel()
        _uiState.update { it.copy(searchQuery = "", isSearching = false) }
        loadTopHeadlinesWithFlow(_uiState.value.selectedCategory)
    }

    /**
     * 選擇分類
     */
    fun selectCategory(category: String?) {
        loadTopHeadlinesWithFlow(category)
    }

    /**
     * 清除錯誤
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}