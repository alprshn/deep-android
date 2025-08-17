package com.kamiapps.deep.deep.data

sealed class UiState{
    object Loading: UiState()
    data class Success(val data: Any): UiState()
    data class Error(val message: String): UiState()
}


