package com.devjeem.clasificadordeimagenes.ml

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import javax.inject.Inject

class MobileNetClassifier2 @Inject constructor(private val context: Context) {

    private lateinit var imageClassifier: ImageClassifier

    companion object {
        private const val threshold: Float = 0.5f
        private const val numThreads: Int = 2
        private const val maxResults: Int = 3
    }

    init {
        setUpImageClassifier()
    }

    private fun setUpImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
            .setNumThreads(numThreads)
            .build()

        val modelName =
            "lite-model_imagenet_mobilenet_v3_large_075_224_classification_5_metadata_1.tflite"

        try {
            imageClassifier =
                ImageClassifier.createFromFileAndOptions(context, modelName, optionsBuilder)
        } catch (e: Exception) {
            Log.e("ERROR", "${e.message}")
        }
    }

    fun classify(image: Bitmap, rotation: Int): Flow<List<CategoryClassification>> {
        return flow {
            var inferenciaTime = SystemClock.uptimeMillis()
            val imageProcess = ImageProcessor.Builder().build()
            val tensorImage = imageProcess.process(TensorImage.fromBitmap(image))
            val imageProcessorOptions = ImageProcessingOptions.builder()
                .setOrientation(getOrientationFromRotation(rotation))
                .build()
            val result = imageClassifier.classify(tensorImage, imageProcessorOptions)
            inferenciaTime = SystemClock.uptimeMillis() - inferenciaTime

            val listDataCategory =
                arrayListOf<CategoryClassification>()
            result.forEach {
                it.categories.forEach { category ->
                    listDataCategory.add(
                        CategoryClassification(
                            label = category.label,
                            score = category.score,
                            index = category.index
                        )
                    )
                }
            }
            if (listDataCategory.size > 1) {
                emit(listDataCategory)
            }
        }
    }


    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 ->
                ImageProcessingOptions.Orientation.BOTTOM_RIGHT

            Surface.ROTATION_180 ->
                ImageProcessingOptions.Orientation.RIGHT_BOTTOM

            Surface.ROTATION_90 ->
                ImageProcessingOptions.Orientation.TOP_LEFT

            else ->
                ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }


}