package com.example.deepwork.deep_work_app.data

sealed class UiState{
    object Loading: UiState()
    data class Success(val data: Any): UiState()
    data class Error(val message: String): UiState()
}