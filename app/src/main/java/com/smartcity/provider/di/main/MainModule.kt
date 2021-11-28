package com.smartcity.provider.di.main

import android.content.SharedPreferences
import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.persistence.AccountPropertiesDao
import com.smartcity.provider.persistence.AppDatabase
import com.smartcity.provider.persistence.BlogPostDao
import com.smartcity.provider.repository.main.AccountRepositoryImpl
import com.smartcity.provider.repository.main.StoreRepositoryImpl
import com.smartcity.provider.repository.main.OrderRepositoryImpl
import com.smartcity.provider.repository.main.CustomCategoryRepositoryImpl


import com.smartcity.provider.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
@Module
object MainModule {

    @JvmStatic
    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideStoreRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): StoreRepositoryImpl {
        return StoreRepositoryImpl(openApiMainService, accountPropertiesDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        sessionManager: SessionManager
    ): OrderRepositoryImpl {
        return OrderRepositoryImpl(openApiMainService, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideCreateBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): CustomCategoryRepositoryImpl {
        return CustomCategoryRepositoryImpl(openApiMainService, blogPostDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        sessionManager: SessionManager,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ): AccountRepositoryImpl {
        return AccountRepositoryImpl(openApiMainService, sessionManager,preferences,editor)
    }
}

















