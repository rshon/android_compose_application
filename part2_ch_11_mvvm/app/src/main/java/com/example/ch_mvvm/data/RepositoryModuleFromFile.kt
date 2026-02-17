package com.example.ch_mvvm.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModuleFromFile { // abstract class 대신 object 사용
    // 설정 파일에서 Repository 타입을 읽어오는 함수 (예시)
    @Provides
    fun provideRepositoryType(): String {
        // 실제로는 YAML 파일 등을 읽어와서 값을 반환하는 로직을 구현해야 합니다.
        // 예시로 BuildConfig 또는 다른 설정 관리자를 사용할 수 있습니다.
        return "real" // 또는 "mock"
    }

    @Singleton
    @Provides
    fun provideStudentRepository(repositoryType: String): StudentRepositoryInterface {
        return when (repositoryType) {
            "real" -> StudentRepositoryImplHilt()
            "mock" -> StudentRepositoryImplHiltMock()
            else -> throw IllegalArgumentException("Unknown repository type: $repositoryType")
        }
    }
}
*/