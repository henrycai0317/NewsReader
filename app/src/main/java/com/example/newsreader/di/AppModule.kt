package com.example.newsreader.di

import com.example.newsreader.BuildConfig
import com.example.newsreader.data.api.NewsApiService
import com.example.newsreader.data.model.ApiKeyInterceptor
import com.example.newsreader.data.repository.NewsRepository
import com.example.newsreader.data.repository.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://newsapi.org/"

    /**
     * 提供 HttpLoggingInterceptor
     * 用於記錄網路請求和回應的詳細資訊
     * 只在 Debug 模式下啟用 BODY 級別的日誌
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * 提供 ApiKeyInterceptor
     * 自動在所有請求的 URL 加入 apiKey 參數
     */
    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): ApiKeyInterceptor {
        return ApiKeyInterceptor()
    }

    /**
     * 提供 OkHttpClient
     * 設定：
     * 1. ApiKeyInterceptor - 自動加入 API Key
     * 2. HttpLoggingInterceptor - 記錄請求日誌
     * 3. Timeout 設定 - 連線、讀取、寫入都設為 30 秒
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)      // 先加入 API Key
            .addInterceptor(loggingInterceptor)     // 再加入日誌記錄
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(apiService: NewsApiService): NewsRepository {
        return NewsRepositoryImpl(apiService)
    }
}