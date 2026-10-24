package com.example.ch_mvvm.data

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RealRepository

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MockRepository
