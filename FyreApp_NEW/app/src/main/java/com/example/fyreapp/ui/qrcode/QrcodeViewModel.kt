package com.example.fyreapp.ui.qrcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QrcodeViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    }
    val text: LiveData<String> = _text
}