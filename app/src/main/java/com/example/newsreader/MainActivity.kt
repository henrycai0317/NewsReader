package com.example.newsreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.newsreader.navigation.NavigationRoute
import com.example.newsreader.ui.screen.NewsDetailScreen
import com.example.newsreader.ui.screen.NewsListScreen
import com.example.newsreader.ui.theme.NewsReaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsReaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NewsReaderNavigation()
                }
            }
        }
    }
}

@Composable
fun NewsReaderNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.NewsList.route
    ) {
        // 新聞列表頁面
        composable(NavigationRoute.NewsList.route) {
            NewsListScreen(
                onArticleClick = { article ->
                    val route = NavigationRoute.NewsDetail.fromArticle(article)
                    navController.navigate(route.route)
                }
            )
        }

        // 新聞詳情頁面
        composable(
            route = NavigationRoute.NewsDetail.ROUTE_PATTERN,
            arguments = listOf(
                navArgument(NavigationRoute.NewsDetail.ARG_ARTICLE_JSON) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val articleJson = backStackEntry.arguments?.getString(
                NavigationRoute.NewsDetail.ARG_ARTICLE_JSON
            )
            val article = NavigationRoute.NewsDetail.parseArticle(articleJson)

            article?.let {
                NewsDetailScreen(
                    article = it,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}