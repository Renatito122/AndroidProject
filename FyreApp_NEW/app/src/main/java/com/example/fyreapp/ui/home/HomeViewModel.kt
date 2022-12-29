package com.example.fyreapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Palco 1"
    }
    private val _text2 = MutableLiveData<String>().apply {
        value = "Nov 11 - 22:30 - Cuca Roseta"
    }
    val text: LiveData<String> = _text
    val text2: LiveData<String> = _text2
}