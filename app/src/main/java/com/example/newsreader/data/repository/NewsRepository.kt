package com.example.newsreader.data.repository

import com.example.newsreader.data.model.ApiResult
import com.example.newsreader.data.model.NewsResponse
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    // Flow 版本 - 用於初始載入和自動更新
    fun getTopHeadlinesFlow(category: String? = null): Flow<ApiResult<NewsResponse>>

    // Suspend 版本 - 用於手動刷新
    suspend fun refreshTopHeadlines(category: String? = null): ApiResult<NewsResponse>

    // 搜尋功能 - 使用 suspend (一次性請求)
    suspend fun searchNews(query: String): ApiResult<NewsResponse>
}