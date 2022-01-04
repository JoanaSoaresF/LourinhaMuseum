package com.example.lourinhamuseum.screens.information

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class InformationViewModelFactory(private val app:Application, private val point : Int)
    :ViewModelProvider
.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(InformationViewModel::class.java)){
            return InformationViewModel(point, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}