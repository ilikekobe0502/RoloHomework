package com.rolo.homework.repository.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    override fun onCleared() {
        super.onCleared()
    }

    abstract fun destroy()
}