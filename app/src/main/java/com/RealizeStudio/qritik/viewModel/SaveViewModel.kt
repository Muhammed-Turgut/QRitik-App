package com.RealizeStudio.qritik.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.RealizeStudio.qritik.data.entity.QRsavesItem
import com.RealizeStudio.qritik.data.repo.SaveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SaveViewModel @Inject  constructor( val saveRepository: SaveRepository): ViewModel(){


    private val _saveList = MutableStateFlow<List<QRsavesItem>>(emptyList())
    val saveList: StateFlow<List<QRsavesItem>> = _saveList

    init {
        viewModelScope.launch {
            saveRepository.getAllSaves().collect { saves ->
                _saveList.value = saves
            }
        }
    }



    fun save (qrType: String, qrContents: String, date: String){
        viewModelScope.launch {
            try {

                saveRepository.save(qrType,qrContents,date)

            }catch (e: Exception){

            }
        }

    }

    fun delete(id:Int){
        viewModelScope.launch {
            saveRepository.delete(id)
        }
    }

    fun update(id: Int,qrType: String, qrContents: String, date: String){
        viewModelScope.launch {
            saveRepository.update(id,qrType,qrContents,date)
        }
    }
}