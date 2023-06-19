package com.devjeem.clasificadordeimagenes.util

import android.content.Context
import com.devjeem.clasificadordeimagenes.camera.PhotoUtilities
import com.devjeem.clasificadordeimagenes.ml.MobileNetClassifier2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ModulePhotoUtilities {

    @Provides
    @Singleton
    fun providePhoto(@ApplicationContext context: Context) = PhotoUtilities(context)


    @Provides
    @Singleton
    fun provideMobileNet2(@ApplicationContext context: Context) =
        MobileNetClassifier2(context)

}