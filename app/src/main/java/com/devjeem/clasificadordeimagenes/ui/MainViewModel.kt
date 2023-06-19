package com.devjeem.clasificadordeimagenes.ui

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devjeem.clasificadordeimagenes.ml.UiState
import com.devjeem.clasificadordeimagenes.repository.RepositoryCamera
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repositoryCamera: RepositoryCamera
) :
    ViewModel() {

    val dataCharacterList: MutableStateFlow<UiState> =
        MutableStateFlow(UiState(loading = false, invokeClick = ::clickPhoto))

    fun classifyFinalVersionTwo(bitmap: Bitmap, rotate: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            repositoryCamera.classify(bitmap, rotate).collect { list ->
                dataCharacterList.update {
                    it.copy(success = list, loading = false, image = bitmap.asImageBitmap())
                }
            }
        }


    private fun clickPhoto() {
        viewModelScope.launch {
            dataCharacterList.update {
                it.copy(loading = true)
            }
        }
    }
}