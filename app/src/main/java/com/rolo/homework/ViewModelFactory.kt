package com.rolo.homework

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.rolo.homework.pages.chat.ChatViewModel
import com.rolo.homework.repository.Repository
import com.rolo.homework.repository.provider.preferences.SharedPreferencesProvider
import com.rolo.homework.repository.provider.resource.ResourceProvider


class ViewModelFactory(private val application: Application,
                       private val repository: Repository,
                       private val preferences: SharedPreferencesProvider,
                       private val resource: ResourceProvider
) : ViewModelProvider.NewInstanceFactory() {

    //ViewModel需要的model再打進去

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(ChatViewModel::class.java) -> ChatViewModel(application)
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            } as T
        }
    }
}