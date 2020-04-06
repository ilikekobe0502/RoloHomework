package com.rolo.homework.pages.chat

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.rolo.homework.repository.data.ContactContent
import com.rolo.homework.repository.data.Contacts
import com.rolo.homework.repository.viewModel.BaseViewModel
import kotlinx.coroutines.*
import java.lang.Exception

class ChatViewModel(application: Application) : BaseViewModel(application) {

    private var fakingContacts = Gson().fromJson("{\"contacts\":[{\"id\":\"0\",\"name\":\"Cindy Tsai\",\"email\":\"cindytsai@gmail.com\"},{\"id\":\"1\",\"name\":\"Andy Hu\",\"email\":\"abdyhu@gmail.com\"},{\"id\":\"2\",\"name\":\"Tom Cruise\",\"email\":\"tomcuise@gmail.com\"},{\"id\":\"3\",\"name\":\"Gal Gadot\",\"email\":\"galgadot@gmail.com\"},{\"id\":\"4\",\"name\":\"Chris Pine\",\"email\":\"chrispine@gmail.com\"},{\"id\":\"5\",\"name\":\"Kelly Capwell\",\"email\":\"kellycapwell@gmail.com\"},{\"id\":\"6\",\"name\":\"Connie Nielsen\",\"email\":\"connienielsen@gmail.com\"},{\"id\":\"7\",\"name\":\"Scarlett Johansson\",\"email\":\"scarlettjohansson@gmail.com\"},{\"id\":\"8\",\"name\":\"Linda Carter\",\"email\":\"lindacarter@gmail.com\"},{\"id\":\"9\",\"name\":\"Isla Fisher\",\"email\":\"islafisher@gmail.com\"},{\"id\":\"10\",\"name\":\"Ben Affleck\",\"email\":\"benaffleck@gmail.com\"},{\"id\":\"11\",\"name\":\"Natalie Portman\",\"email\":\"natalieportman@gmail.com\"},{\"id\":\"12\",\"name\":\"Vin Diesel\",\"email\":\"vindiesel@gmail.com\"},{\"id\":\"13\",\"name\":\"Jason Momoa\",\"email\":\"jasonmomoa@gmail.com\"},{\"id\":\"14\",\"name\":\"Amber Heard\",\"email\":\"amberheard@gmail.com\"},{\"id\":\"15\",\"name\":\"Paul Walker\",\"email\":\"paulwalker@gmail.com\"}]}", Contacts::class.java)
    private var job: Job? = null

    var contactsResult: MutableLiveData<ArrayList<ContactContent>> = MutableLiveData()

    interface ViewModelResult {
        fun onSucceed(T: Any)
        fun onFailed(e: Exception)
        fun onProgress(b: Boolean)
    }

    fun getContacts() {
        job = GlobalScope.launch(Dispatchers.IO) {
            contactsResult.postValue(fakingContacts.contacts)
        }
    }

    override fun destroy() {
        job?.cancel()
    }
}