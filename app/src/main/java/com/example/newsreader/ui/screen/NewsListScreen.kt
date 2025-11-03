package com.example.newsreader.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsreader.data.model.Article
import com.example.newsreader.data.model.Source
import com.example.newsreader.ui.component.*
import com.example.newsreader.ui.theme.NewsReaderTheme
import com.example.newsreader.viewmodel.NewsViewModel
import com.example.newsreader.viewmodel.NewsUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NewsListScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refresh() }
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("News Reader")
                        if (uiState.totalResults > 0) {
                            Text(
                                text = "${uiState.totalResults} articles found",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.searchNews(it) },
                onClear = { viewModel.clearSearch() }
            )

            if (uiState.articles.isNotEmpty()) {
                Text(
                    text = "Showing ${uiState.articles.size} of ${uiState.totalResults}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                when {
                    (uiState.isLoading || uiState.isSearching) && uiState.articles.isEmpty() -> {
                        LoadingIndicator()
                    }

                    uiState.error != null && uiState.articles.isEmpty() -> {
                        ErrorView(
                            message = uiState.error ?: "Unknown error",
                            onRetry = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    viewModel.searchNews(uiState.searchQuery)
                                } else {
                                    viewModel.selectCategory(uiState.selectedCategory)
                                }
                            }
                        )
                    }

                    uiState.articles.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No articles found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = uiState.articles,
                                key = { it.url }
                            ) { article ->
                                NewsCard(
                                    article = article,
                                    onClick = { onArticleClick(article) }
                                )
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = uiState.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 可預覽的 NewsListScreen 內容組件
 * 分離 ViewModel 依賴，方便 Preview
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun NewsListScreenContent(
    uiState: NewsUiState,
    onArticleClick: (Article) -> Unit,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = onRefresh
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("News Reader")
                        if (uiState.totalResults > 0) {
                            Text(
                                text = "${uiState.totalResults} articles found",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = onQueryChange,
                onClear = onClearSearch
            )

            if (uiState.articles.isNotEmpty()) {
                Text(
                    text = "Showing ${uiState.articles.size} of ${uiState.totalResults}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                when {
                    (uiState.isLoading || uiState.isSearching) && uiState.articles.isEmpty() -> {
                        LoadingIndicator()
                    }

                    uiState.error != null && uiState.articles.isEmpty() -> {
                        ErrorView(
                            message = uiState.error,
                            onRetry = onRetry
                        )
                    }

                    uiState.articles.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No articles found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = uiState.articles,
                                key = { it.url }
                            ) { article ->
                                NewsCard(
                                    article = article,
                                    onClick = { onArticleClick(article) }
                                )
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = uiState.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ==================== Preview Data ====================

/**
 * Preview 用的測試資料
 */
private fun createSampleArticle(index: Int) = Article(
    source = Source(id = "source-$index", name = "Tech News Daily"),
    author = "John Doe",
    title = "Breaking News: Important Technology Update #$index",
    description = "This is a sample description for the news article. It contains important information about recent developments in technology.",
    url = "https://example.com/article-$index",
    urlToImage = "https://via.placeholder.com/400x200",
    publishedAt = "2025-11-03T10:00:00Z",
    content = "Full article content goes here..."
)

private val sampleArticles = List(5) { createSampleArticle(it) }

// ==================== Previews ====================

/**
 * Preview: 載入中狀態
 */
@Preview(
    name = "載入中",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun NewsListScreenLoadingPreview() {
    NewsReaderTheme {
        NewsListScreenContent(
            uiState = NewsUiState(isLoading = true),
            onArticleClick = {},
            onQueryChange = {},
            onClearSearch = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

/**
 * Preview: 文章列表
 */
@Preview(
    name = "文章列表",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun NewsListScreenWithArticlesPreview() {
    NewsReaderTheme {
        NewsListScreenContent(
            uiState = NewsUiState(
                articles = sampleArticles,
                totalResults = 100
            ),
            onArticleClick = {},
            onQueryChange = {},
            onClearSearch = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

/**
 * Preview: 搜尋狀態
 */
@Preview(
    name = "搜尋中",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun NewsListScreenSearchingPreview() {
    NewsReaderTheme {
        NewsListScreenContent(
            uiState = NewsUiState(
                articles = sampleArticles,
                totalResults = 50,
                searchQuery = "technology",
                isSearching = false
            ),
            onArticleClick = {},
            onQueryChange = {},
            onClearSearch = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

/**
 * Preview: 錯誤狀態
 */
@Preview(
    name = "錯誤狀態",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun NewsListScreenErrorPreview() {
    NewsReaderTheme {
        NewsListScreenContent(
            uiState = NewsUiState(
                error = "Network connection failed. Please check your internet connection."
            ),
            onArticleClick = {},
            onQueryChange = {},
            onClearSearch = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

/**
 * Preview: 無文章狀態
 */
@Preview(
    name = "無文章",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun NewsListScreenEmptyPreview() {
    NewsReaderTheme {
        NewsListScreenContent(
            uiState = NewsUiState(
                articles = emptyList(),
                totalResults = 0,
                searchQuery = "nonexistent"
            ),
            onArticleClick = {},
            onQueryChange = {},
            onClearSearch = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

/**
 * Preview: 下拉刷新狀態
 */
@Preview(
    name = "下拉刷新",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun NewsListScreenRefreshingPreview() {
    NewsReaderTheme {
        NewsListScreenContent(
            uiState = NewsUiState(
                articles = sampleArticles,
                totalResults = 100,
                isRefreshing = true
            ),
            onArticleClick = {},
            onQueryChange = {},
            onClearSearch = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

/**
 * Preview: 深色模式
 */
@Preview(
    name = "深色模式",
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NewsListScreenDarkPreview() {
    NewsReaderTheme {
        NewsListScreenContent(
            uiState = NewsUiState(
                articles = sampleArticles,
                totalResults = 100
            ),
            onArticleClick = {},
            onQueryChange = {},
            onClearSearch = {},
            onRefresh = {},
            onRetry = {}
        )
    }
}

