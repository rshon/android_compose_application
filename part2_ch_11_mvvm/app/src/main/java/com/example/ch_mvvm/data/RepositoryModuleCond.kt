package com.example.ch_mvvm.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModuleCond { // abstract class 대신 object 사용
    @Singleton
    @RealRepository
    @Provides
    fun provideRealRepository(): StudentRepositoryInterface {
        return StudentRepositoryImplHilt()
    }

    @Singleton
    @MockRepository
    @Provides
    fun provideMockRepository(): StudentRepositoryInterface {
        return StudentRepositoryImplHiltMock()
    }

    // 설정 파일에서 값을 읽어오는 함수 (예시)
    @Provides
    fun provideUseMockRepository(): Boolean {
        // 실제 설정 파일 (yaml 등)을 읽어와서 값을 반환하는 로직 구현
        return false // 기본적으로 실제 Repository 사용
    }

    @Singleton
    @Provides
    fun provideStudentRepository(
        @RealRepository realRepository: StudentRepositoryInterface,
        @MockRepository mockRepository: StudentRepositoryInterface,
        useMockRepository: Boolean
    ): StudentRepositoryInterface {
        return if (useMockRepository) {
            mockRepository
        } else {
            realRepository
        }
    }
}