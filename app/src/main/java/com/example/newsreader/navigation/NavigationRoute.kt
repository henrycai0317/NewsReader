package com.example.newsreader.navigation

import com.example.newsreader.data.model.Article
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Navigation 路由定義
 * 使用 sealed class 管理所有導航路由，提供類型安全的導航
 */
sealed class NavigationRoute(val route: String) {

    /**
     * 新聞列表頁面
     */
    data object NewsList : NavigationRoute("news_list")

    /**
     * 新聞詳情頁面
     * @param articleJson Article 對象的 JSON 字串（已編碼）
     */
    data class NewsDetail(val articleJson: String) : NavigationRoute("news_detail/$articleJson") {
        companion object {
            // 路由模式，用於 NavHost 定義
            const val ROUTE_PATTERN = "news_detail/{articleJson}"
            // 參數名稱
            const val ARG_ARTICLE_JSON = "articleJson"

            private val gson = Gson()

            /**
             * 從 Article 對象創建 NewsDetail 路由
             */
            fun fromArticle(article: Article): NewsDetail {
                val articleJson = gson.toJson(article)
                val encodedJson = URLEncoder.encode(
                    articleJson,
                    StandardCharsets.UTF_8.toString()
                )
                return NewsDetail(encodedJson)
            }

            /**
             * 從編碼的 JSON 字串解析 Article 對象
             */
            fun parseArticle(encodedJson: String?): Article? {
                return try {
                    encodedJson?.let {
                        val decodedJson = URLDecoder.decode(
                            it,
                            StandardCharsets.UTF_8.toString()
                        )
                        gson.fromJson(decodedJson, Article::class.java)
                    }
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
}

