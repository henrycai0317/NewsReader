package com.example.newsreader.data.api

import com.example.newsreader.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService {

    /**
     * Get top headlines
     * https://newsapi.org/docs/endpoints/top-headlines
     */
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String? = null,
        @Query("pageSize") pageSize: Int = 20
    ): NewsResponse

    /**
     * Search everything
     * https://newsapi.org/docs/endpoints/everything
     */
    @GET("v2/everything")
    suspend fun searchEverything(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 20,
        @Query("language") language: String = "en"
    ): NewsResponse
}