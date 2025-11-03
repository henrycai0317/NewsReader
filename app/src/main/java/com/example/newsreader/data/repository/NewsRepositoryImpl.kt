package com.example.newsreader.data.repository

import com.example.newsreader.data.api.NewsApiService
import com.example.newsreader.data.model.ApiResult
import com.example.newsreader.data.model.NewsResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService
) : NewsRepository {

    /**
     * Flow 版本 - 自動發射 Loading -> Success/Error
     * 適合用於初始載入
     */
    override fun getTopHeadlinesFlow(category: String?): Flow<ApiResult<NewsResponse>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = apiService.getTopHeadlines(category = category)
            emit(ApiResult.Success(response))
        } catch (e: Exception) {
            emit(ApiResult.Error(
                message = e.message ?: "Failed to fetch top headlines",
                exception = e
            ))
        }
    }

    /**
     * Suspend 版本 - 用於下拉刷新
     * 不發射 Loading，由呼叫方控制
     */
    override suspend fun refreshTopHeadlines(category: String?): ApiResult<NewsResponse> {
        return try {
            val response = apiService.getTopHeadlines(category = category)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(
                message = e.message ?: "Failed to refresh headlines",
                exception = e
            )
        }
    }

    /**
     * 搜尋功能 - Suspend 版本
     * 適合一次性請求
     */
    override suspend fun searchNews(query: String): ApiResult<NewsResponse> {
        return try {
            if (query.isBlank()) {
                return ApiResult.Error("Search query cannot be empty")
            }

            val response = apiService.searchEverything(query = query)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(
                message = e.message ?: "Failed to search news",
                exception = e
            )
        }
    }
}