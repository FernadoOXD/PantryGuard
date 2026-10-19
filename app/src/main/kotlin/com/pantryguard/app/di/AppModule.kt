package com.pantryguard.app.di

import android.content.Context
import androidx.room.Room
import com.pantryguard.app.data.local.AppDatabase
import com.pantryguard.app.data.local.dao.PantryDao
import com.pantryguard.app.data.remote.api.OpenFoodFactsApi
import com.pantryguard.app.data.remote.interceptor.UserAgentInterceptor
import com.pantryguard.app.data.repository.PantryRepositoryImpl
import com.pantryguard.app.domain.repository.PantryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPantryRepository(
        pantryRepositoryImpl: PantryRepositoryImpl
    ): PantryRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pantry_guard.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePantryDao(db: AppDatabase): PantryDao {
        return db.pantryDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenFoodFactsApi(okHttpClient: OkHttpClient): OpenFoodFactsApi {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OpenFoodFactsApi::class.java)
    }
}
