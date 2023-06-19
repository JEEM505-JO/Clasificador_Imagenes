package com.devjeem.clasificadordeimagenes.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.devjeem.clasificadordeimagenes.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

class PhotoUtilities @Inject constructor(
    private val context: Context
) {
    lateinit var file: File
    val authority = BuildConfig.APPLICATION_ID + ".fileprovider"
    private var namePhoto = ""

    fun createPhotoFile(name: String) {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file = File.createTempFile("IMG_${name}_", ".jpg", dir)
        namePhoto = file.name
    }
    fun getNamePhoto(): String {
        return namePhoto
    }
    fun getByteArrayOfPhoto(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        return stream.toByteArray()
    }

}
