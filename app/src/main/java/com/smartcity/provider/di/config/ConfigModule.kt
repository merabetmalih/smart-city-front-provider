package com.smartcity.provider.di.config

import android.content.SharedPreferences
import com.smartcity.provider.api.config.OpenApiConfigService
import com.smartcity.provider.repository.config.ConfigRepositoryImpl
import com.smartcity.provider.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
object ConfigModule {

    @JvmStatic
    @ConfigScope
    @Provides
    fun provideApiConfigService(retrofitBuilder: Retrofit.Builder): OpenApiConfigService {
        return retrofitBuilder
            .build()
            .create(OpenApiConfigService::class.java)
    }

    @JvmStatic
    @ConfigScope
    @Provides
    fun provideConfigRepository(
        sessionManager: SessionManager,
        OpenApiConfigService: OpenApiConfigService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ): ConfigRepositoryImpl {
        return ConfigRepositoryImpl(
            OpenApiConfigService,
            sessionManager,
            preferences,
            editor
        )
    }

    @JvmStatic
    @ConfigScope
    @Provides
    @Named("GetSharedPreferences")
    fun provideSharedPreferences(
        sharedPreferences: SharedPreferences
    ): SharedPreferences {
        return sharedPreferences
    }
}