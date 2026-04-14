package com.kotonosora.numrise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotonosora.numrise.data.LevelDao

class GameViewModelFactory(private val levelDao: LevelDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(levelDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
