package com.smartcity.provider.di.auth

import android.content.SharedPreferences
import com.smartcity.provider.api.auth.OpenApiAuthService
import com.smartcity.provider.persistence.AccountPropertiesDao
import com.smartcity.provider.persistence.AuthTokenDao
import com.smartcity.provider.repository.auth.AuthRepositoryImpl
import com.smartcity.provider.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object AuthModule{

    @JvmStatic
    @AuthScope
    @Provides
    fun provideApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @JvmStatic
    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor
        ): AuthRepositoryImpl {
        return AuthRepositoryImpl(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager,
            preferences,
            editor
        )
    }
}









