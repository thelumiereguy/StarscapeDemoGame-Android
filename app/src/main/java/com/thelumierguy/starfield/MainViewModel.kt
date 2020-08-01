package com.thelumierguy.starfield

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thelumierguy.starfield.utils.ScreenStates

class MainViewModel : ViewModel() {

    private val screenStateLiveData = MutableLiveData<ScreenStates>()

    fun observeScreenState(): LiveData<ScreenStates> = screenStateLiveData

    fun updateUIState(screenStates: ScreenStates) {
        screenStateLiveData.postValue(screenStates)
    }

    fun getCurrentState(): ScreenStates? = observeScreenState().value
}