package com.app.propertyfinder.api

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NormalApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UploadApi