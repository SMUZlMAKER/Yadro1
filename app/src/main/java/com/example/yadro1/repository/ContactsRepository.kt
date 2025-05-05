package com.example.yadro1.repository

import android.content.Context
import android.provider.ContactsContract
import com.example.yadro1.data.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ContactsRepository private constructor() {


    companion object {
        @Volatile
        private var INSTANCE: ContactsRepository? = null

        fun getInstance(): ContactsRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ContactsRepository()
                INSTANCE = instance
                instance
            }
        }
    }


    suspend fun getContacts(context: Context): List<Contact> = coroutineScope {
        async(Dispatchers.IO) { getContactsList(context) }.await()
    }


    private fun getContactsList(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )
        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoUriIndex = it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            val accountTypeIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET)
            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                val photoUri = it.getString(photoUriIndex)
                val accountType = it.getString(accountTypeIndex)

                //Сохранение только контактов хранящихся на устройстве
                if(accountType == null||accountType == "com.android.contacts")
                    contacts.add(Contact(id, name, number, photoUri))
            }
        }
        return contacts
    }
}