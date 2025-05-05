package com.example.yadro1.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yadro1.data.Contact
import com.example.yadro1.repository.ContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.collections.groupBy
import kotlin.collections.toSortedMap

class ContactsViewModel : ViewModel() {
    private val contactsRepository: ContactsRepository = ContactsRepository.getInstance()

    private val _contacts = MutableStateFlow(emptyList<Contact>())
    val groupedContacts = _contacts.map { contacts ->
        contacts.groupBy {
            it.name.firstOrNull()?.uppercase() ?: "#"
        }.toSortedMap()
    }

    fun loadContacts(context: Context) {
        viewModelScope.launch {
            _contacts.value = contactsRepository.getContacts(context)
        }
    }

    private val _isContactsPermissionDialogShown = MutableStateFlow(true)
    val isContactsPermissionDialogShown = _isContactsPermissionDialogShown.asStateFlow()
    private val _isContactsPermissionGranted = MutableStateFlow(true)
    val isContactsPermissionGranted = _isContactsPermissionGranted.asStateFlow()

    fun changePermissionToContacts(status: Boolean) {
        if (_isContactsPermissionGranted.value != status)
            _isContactsPermissionGranted.value = status
        if (status)
            _isContactsPermissionDialogShown.value = false

    }

    fun showContactsDialog() {
        if (!_isContactsPermissionGranted.value)
            _isContactsPermissionDialogShown.value = true
    }

    fun hideContactsDialog() {
        _isContactsPermissionDialogShown.value = false
    }


    private val _isCallPermissionDialogShown = MutableStateFlow(false)
    val isCallPermissionDialogShown = _isCallPermissionDialogShown.asStateFlow()
    private val _isCallPermissionGranted = MutableStateFlow(true)
    val isCallPermissionGranted = _isCallPermissionGranted.asStateFlow()

    fun changePermissionToCall(status: Boolean) {
        if (_isCallPermissionGranted.value != status)
            _isCallPermissionGranted.value = status
        if (status)
            _isCallPermissionDialogShown.value = false

    }

    fun showCallPermissionDialog() {
        if (!_isCallPermissionGranted.value)
            _isCallPermissionDialogShown.value = true
    }

    fun hideCallPermissionDialog() {
        _isCallPermissionDialogShown.value = false
    }


}
