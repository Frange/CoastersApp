package com.frange.coasters.di

import com.google.gson.Gson
import com.frange.coasters.data.api.service.InfoCaptainApiService
import com.frange.coasters.data.api.service.QueueApiService
import com.frange.coasters.data.api.Url
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideCaptainApiService(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): InfoCaptainApiService {
        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(Interceptor {
                val token = "17272049-aba4-43f9-be9c-c086347e1ec6"
                val newRequest = it.request().newBuilder()
                    .addHeader("Authorization", token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                it.proceed(newRequest)
            }).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Url.BASE_GENERAL_INFO_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(InfoCaptainApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideQueueApiService(
        okHttpClient: OkHttpClient
    ): QueueApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(Url.BASE_QUEUE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(QueueApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(Interceptor {
                val token = "17272049-aba4-43f9-be9c-c086347e1ec6"
                val newRequest = it.request().newBuilder()
                    .addHeader("Authorization", token)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                it.proceed(newRequest)
            }).build()

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
//            level = if (BuildConfig.BUILD_TYPE.contentEquals("debug"))
//                HttpLoggingInterceptor.Level.BODY
//            else
//                HttpLoggingInterceptor.Level.NONE
        }

    @Singleton
    @Provides
    fun providesGson() = Gson()

}
