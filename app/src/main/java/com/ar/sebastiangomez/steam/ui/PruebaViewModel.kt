package com.ar.sebastiangomez.steam.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class PruebaViewModel : ViewModel()
{
    var TextoTotalPersonas : MutableLiveData<String> = MutableLiveData<String>()
    fun Calcular() {

    }

    fun Reset(){
    }
}