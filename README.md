# News Reader - Android Application

A modern Android news reader application built with **Jetpack Compose**, following **Clean Architecture** and **MVVM** design patterns. This project demonstrates best practices in Android development including dependency injection, reactive programming with Kotlin Flow, and modern declarative UI design.

## 📱 Features

- **Top Headlines**: Browse the latest news from various categories
- **Real-time Search**: Search for news articles with debounced input (500ms)
- **Category Filtering**: Filter news by categories (Technology, Business, Sports, etc.)
- **Article Details**: View full article details with images
- **Pull-to-Refresh**: Refresh news content with swipe gesture
- **Modern UI**: Beautiful Material Design 3 interface with responsive layouts
- **Error Handling**: Comprehensive error handling with retry mechanism
- **Loading States**: Smooth loading indicators and skeleton screens

## 🏗️ MVVM Architecture

This application implements the **MVVM (Model-View-ViewModel)** pattern with **Clean Architecture** principles, ensuring separation of concerns and testability.

### Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                   VIEW (Jetpack Compose)                    │
│  ┌────────────────┐              ┌────────────────┐         │
│  │ NewsListScreen │              │ NewsDetailScreen│         │
│  └────────────────┘              └────────────────┘         │
│           │                               │                 │
│           │  collectAsStateWithLifecycle()│                 │
│           ▼                               ▼                 │
└───────────────────────────────────────────────────────────┬─┘
                                                            │
            ┌───────────────────────────────────────────────┘
            │
┌───────────▼─────────────────────────────────────────────────┐
│                    VIEWMODEL LAYER                          │
│  ┌──────────────────────────────────────────────────┐       │
│  │           NewsViewModel (HiltViewModel)          │       │
│  │  - MutableStateFlow<NewsUiState>                 │       │
│  │  - StateFlow<NewsUiState> (exposed)              │       │
│  │  - Business Logic (search, refresh, category)    │       │
│  │  - Debounce handling (searchJob with delay)      │       │
│  └──────────────────────────────────────────────────┘       │
│           │                                                  │
│           │  Flow.collect() / suspend functions             │
│           ▼                                                  │
└──────────────────────────────────────────────────────────┬──┘
                                                           │
            ┌──────────────────────────────────────────────┘
            │
┌───────────▼──────────────────────────────────────────────────┐
│              REPOSITORY LAYER (Domain)                       │
│  ┌────────────────────────────────────────────────┐          │
│  │         NewsRepository (Interface)             │          │
│  │  - getTopHeadlinesFlow(): Flow<ApiResult>      │          │
│  │  - refreshTopHeadlines(): ApiResult (suspend)  │          │
│  │  - searchNews(query): ApiResult (suspend)      │          │
│  └────────────────────────────────────────────────┘          │
│                        │                                     │
│  ┌────────────────────▼────────────────────────┐            │
│  │      NewsRepositoryImpl                     │            │
│  │  - Implements data fetching logic           │            │
│  │  - Error handling & mapping                 │            │
│  └─────────────────────────────────────────────┘            │
│           │                                                  │
│           │  API calls                                       │
│           ▼                                                  │
└──────────────────────────────────────────────────────────┬──┘
                                                           │
            ┌──────────────────────────────────────────────┘
            │
┌───────────▼──────────────────────────────────────────────────┐
│                    DATA LAYER                                │
│  ┌────────────────────────────────────────────────┐          │
│  │         NewsApiService (Retrofit)              │          │
│  │  - getTopHeadlines(category)                   │          │
│  │  - searchEverything(query)                     │          │
│  └────────────────────────────────────────────────┘          │
│                                                               │
│  ┌────────────────────────────────────────────────┐          │
│  │              Models                            │          │
│  │  - Article (data class)                        │          │
│  │  - NewsResponse                                │          │
│  │  - ApiResult (sealed class)                    │          │
│  └────────────────────────────────────────────────┘          │
│                                                               │
│  ┌────────────────────────────────────────────────┐          │
│  │         ApiKeyInterceptor                      │          │
│  │  - Automatically adds API key to requests      │          │
│  └────────────────────────────────────────────────┘          │
└───────────────────────────────────────────────────────────────┘
```

### Data Flow Pattern

```
User Interaction → View (Composable)
                     ↓
                 ViewModel.function()
                     ↓
         ViewModel updates MutableStateFlow
                     ↓
              Repository call (Flow/suspend)
                     ↓
              API Service (Retrofit)
                     ↓
         Repository emits ApiResult
                     ↓
    ViewModel collects & updates UiState
                     ↓
    View observes StateFlow with collectAsStateWithLifecycle()
                     ↓
              Recomposition triggered
```


## 🛠️ Tech Stack & Key Features

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Architecture**: MVVM + Clean Architecture
- **State Management**: Kotlin Flow & StateFlow
- **Dependency Injection**: Dagger Hilt
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Minimum SDK**: 24 (Android 7.0) | **Target SDK**: 36

### Jetpack Compose UI Implementation

#### 1. Lifecycle-Aware State Collection
```kotlin
@Composable
fun NewsListScreen(viewModel: NewsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // UI automatically recomposes when state changes
}
```
- **collectAsStateWithLifecycle()**: Stops collecting when app is in background, optimizing performance
- **hiltViewModel()**: Automatic ViewModel injection via Hilt

#### 2. Stateless Composables Pattern
```kotlin
@Composable
fun NewsCard(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.clickable(onClick = onClick)) {
        // UI implementation
    }
}
```
- All data and events passed as parameters
- Highly reusable across different screens
- Easier to test and preview

#### 3. Declarative State-Driven UI
```kotlin
when {
    uiState.isLoading -> LoadingIndicator()
    uiState.error != null -> ErrorView(
        message = uiState.error,
        onRetry = { viewModel.refresh() }
    )
    else -> LazyColumn {
        items(uiState.articles) { article ->
            NewsCard(article = article, onClick = { onArticleClick(article) })
        }
    }
}
```
- UI automatically reflects application state
- Single source of truth from ViewModel

#### 4. Pull-to-Refresh Interaction
```kotlin
@OptIn(ExperimentalMaterialApi::class)
val pullRefreshState = rememberPullRefreshState(
    refreshing = uiState.isRefreshing,
    onRefresh = { viewModel.refresh() }
)

Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
    // Content
    PullRefreshIndicator(refreshing = uiState.isRefreshing, state = pullRefreshState)
}
```
- Native Material Design swipe-to-refresh gesture
- State-driven refresh indicator

#### 5. Efficient List Rendering
```kotlin
LazyColumn {
    items(uiState.articles, key = { it.url }) { article ->
        NewsCard(article = article, onClick = { onArticleClick(article) })
    }
}
```
- **LazyColumn**: Only renders visible items, optimizing performance
- **key parameter**: Ensures stable item identity for smooth animations

#### 6. Image Loading with Coil
```kotlin
AsyncImage(
    model = article.urlToImage,
    contentDescription = article.title,
    modifier = Modifier.fillMaxWidth().height(200.dp),
    contentScale = ContentScale.Crop
)
```
- Async image loading with automatic caching
- Compose-native image component

## 📂 Project Structure

```
com.example.newsreader              # Root Package
│
├── data                            # Data Layer
│   ├── api                         # Remote API Service
│   │   └── NewsApiService.kt       # Retrofit API definitions
│   │
│   ├── model                       # Data Models
│   │   ├── Article.kt              # Article data class
│   │   ├── NewsResponse.kt         # API response model
│   │   ├── ApiResult.kt            # Sealed class for API results (Loading/Success/Error)
│   │   └── ApiKeyInterceptor.kt    # OkHttp interceptor for API key injection
│   │
│   └── repository                  # Repository Pattern Implementation
│       ├── NewsRepository.kt       # Repository interface (abstraction)
│       └── NewsRepositoryImpl.kt   # Repository implementation with Flow/Suspend
│
├── di                              # Dependency Injection
│   └── AppModule.kt                # Dagger Hilt modules (provides Retrofit, Repository, etc.)
│
├── navigation                      # Navigation Component
│   └── NavigationRoute.kt          # Navigation routes and NavHost setup
│
├── ui                              # UI Layer (Jetpack Compose)
│   ├── component                   # Reusable UI Components
│   │   ├── ErrorView.kt            # Error state display with retry button
│   │   ├── LoadingIndicator.kt     # Circular loading indicator
│   │   ├── NewsCard.kt             # News article card (stateless composable)
│   │   └── SearchBar.kt            # Search input with clear button
│   │
│   ├── screen                      # Full Screen Composables
│   │   ├── NewsListScreen.kt       # Main news list with pull-to-refresh
│   │   └── NewsDetailScreen.kt     # Article detail view
│   │
│   └── theme                       # Material Design 3 Theme
│       ├── Color.kt                # Color palette definitions
│       ├── Theme.kt                # Theme configuration (light/dark mode)
│       └── Type.kt                 # Typography system
│
├── viewmodel                       # ViewModel Layer
│   └── NewsViewModel.kt            # Main ViewModel with StateFlow and business logic
│
├── MainActivity.kt                 # Entry point activity (sets up Compose)
└── NewsApp.kt                      # Application class with @HiltAndroidApp
```

---

**Note for Reviewers**: This project demonstrates modern Android development practices with emphasis on **reactive programming** (Kotlin Flow), **declarative UI** (Jetpack Compose), **clean architecture** (MVVM pattern), and **dependency injection** (Hilt). The codebase showcases practical implementations of state management, error handling, and user experience optimization (debounced search, pull-to-refresh).

