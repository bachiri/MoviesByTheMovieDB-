package io.bachiri.abderrahman.moviesbymoviedb.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.bachiri.abderrahman.moviesbymoviedb.BuildConfig
import io.bachiri.abderrahman.moviesbymoviedb.data.remote.AuthenticationInterceptor
import io.bachiri.abderrahman.moviesbymoviedb.data.remote.FakeNetworkMovieService
import io.bachiri.abderrahman.moviesbymoviedb.data.remote.MovieService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FakeMoviesNetworkService


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MoviesNetworkService

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        const val MOVIE_BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "25a8f80ba018b52efb64f05140f6b43c"
        const val DEFAULT_LANGUAGE = "en-US"
    }

    @FakeMoviesNetworkService
    @Singleton
    @Provides
    fun provideFakeNetworkService(): MovieService {
        return FakeNetworkMovieService()

    }

    @MoviesNetworkService
    @Singleton
    @Provides
    fun provideMoviesNetworkService(retrofit: Retrofit): MovieService {
        return retrofit.create(MovieService::class.java)

    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): OkHttpClient {
        val httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else
            HttpLoggingInterceptor.Level.NONE

        return OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(AuthenticationInterceptor(API_KEY, DEFAULT_LANGUAGE))
            .build()
    }


    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(MOVIE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

}