package com.rolo.homework.repository.data

import java.io.Serializable

data class Contacts(
        var contacts: ArrayList<ContactContent>?
) : Serializable

data class ContactContent(
        var id: Int?,
        var name: String?,
        var email: String?
) : Serializable