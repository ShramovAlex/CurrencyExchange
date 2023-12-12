package com.shramov.currencyexchanger.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.shramov.currencyexchanger.BuildConfig
import com.shramov.currencyexchanger.domain.useCase.ReceiveCurrencyUseCase
import com.shramov.currencyexchanger.domain.useCase.GetBalancesUseCase
import com.shramov.currencyexchanger.domain.useCase.SellCurrencyUseCase
import com.shramov.currencyexchanger.domain.useCase.SynchronizeUseCase
import com.shramov.currencyexchanger.network.ApiService
import com.shramov.currencyexchanger.repository.BalancePreferencesRepositoryImpl
import com.shramov.currencyexchanger.repository.CommissionRepositoryImpl
import com.shramov.currencyexchanger.repository.ExchangeRatesRepositoryImpl
import com.shramov.currencyexchanger.utils.Const
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BuildConfig.API_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun providePreferences(context: Application): SharedPreferences {
        return context.getSharedPreferences(Const.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideGetBalancesUseCase(
        preferences: SharedPreferences,
        exchangeRatesRepositoryImpl: ExchangeRatesRepositoryImpl
    ): GetBalancesUseCase {
        return GetBalancesUseCase(
            exchangeRatesRepositoryImpl,
            BalancePreferencesRepositoryImpl(preferences)
        )
    }

    @Singleton
    @Provides
    fun provideBuyUseCase(
        exchangeRatesRepositoryImpl: ExchangeRatesRepositoryImpl,
        balancePreferencesRepositoryImpl: BalancePreferencesRepositoryImpl,
        commissionRepositoryImpl: CommissionRepositoryImpl
    ): ReceiveCurrencyUseCase {
        return ReceiveCurrencyUseCase(exchangeRatesRepositoryImpl, balancePreferencesRepositoryImpl, commissionRepositoryImpl)
    }

    @Singleton
    @Provides
    fun provideSellUseCase(
        exchangeRatesRepositoryImpl: ExchangeRatesRepositoryImpl,
        balancePreferencesRepositoryImpl: BalancePreferencesRepositoryImpl,
        commissionRepositoryImpl: CommissionRepositoryImpl
    ): SellCurrencyUseCase {
        return SellCurrencyUseCase(exchangeRatesRepositoryImpl, balancePreferencesRepositoryImpl, commissionRepositoryImpl)
    }

    @Singleton
    @Provides
    fun provideSynchronizeUseCase(
        sharedPreferences: SharedPreferences,
        exchangeRatesRepositoryImpl: ExchangeRatesRepositoryImpl
    ): SynchronizeUseCase {
        return SynchronizeUseCase(
            exchangeRatesRepositoryImpl,
            BalancePreferencesRepositoryImpl(sharedPreferences)
        )
    }

    @Singleton
    @Provides
    fun provideDefaultBgDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

}