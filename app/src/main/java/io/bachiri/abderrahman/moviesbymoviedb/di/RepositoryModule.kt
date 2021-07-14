package io.bachiri.abderrahman.moviesbymoviedb.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.bachiri.abderrahman.moviesbymoviedb.repository.MovieMapper
import io.bachiri.abderrahman.moviesbymoviedb.repository.MovieMapperImpl
import io.bachiri.abderrahman.moviesbymoviedb.repository.MovieRepository
import io.bachiri.abderrahman.moviesbymoviedb.repository.MovieRepositoryImpl
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun provideMovieMapper(movieMapperImpl: MovieMapperImpl): MovieMapper

    @Binds
    @Singleton
    fun provideBookRepository(movieRepositoryImpl: MovieRepositoryImpl): MovieRepository

}