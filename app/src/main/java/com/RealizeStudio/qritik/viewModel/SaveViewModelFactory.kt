package com.RealizeStudio.qritik.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.RealizeStudio.qritik.data.repo.SaveRepository

class SaveViewModelFactory(
    private val saveRepository: SaveRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SaveViewModel(saveRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}