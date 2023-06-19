package com.devjeem.clasificadordeimagenes.repository

import android.graphics.Bitmap
import com.devjeem.clasificadordeimagenes.ml.MobileNetClassifier2
import javax.inject.Inject

class RepositoryCamera @Inject constructor(private var mobileNetClassifier2: MobileNetClassifier2) {

    fun classify(bitmap: Bitmap, screenOrientation: Int) =
        mobileNetClassifier2.classify(bitmap, screenOrientation)

}