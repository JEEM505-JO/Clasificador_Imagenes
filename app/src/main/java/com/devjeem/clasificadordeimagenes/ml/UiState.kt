package com.devjeem.clasificadordeimagenes.ml

import androidx.compose.ui.graphics.ImageBitmap

data class UiState(
    val success: List<CategoryClassification>? = null,
    val loading: Boolean = false,
    val invokeClick: () -> Unit = {},
    val image: ImageBitmap? = null
)
