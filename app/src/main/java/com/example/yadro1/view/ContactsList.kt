package com.example.yadro1.view

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yadro1.viewModel.ContactsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.yadro1.R
import com.example.yadro1.data.Contact

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Contact(modifier: Modifier = Modifier, viewModel: ContactsViewModel) {
    val context = LocalContext.current
    val showContactsDialog = viewModel.isContactsPermissionDialogShown.collectAsState()
    val showCallDialog = viewModel.isCallPermissionDialogShown.collectAsState()

    val contactsAndCallPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE
        )
    )

    LaunchedEffect(Unit) { contactsAndCallPermissions.launchMultiplePermissionRequest() }

    viewModel.changePermissionToContacts(contactsAndCallPermissions.permissions[0].status.isGranted)
    if (showContactsDialog.value)
        RequestContactsPermission(contactsAndCallPermissions.permissions[0], context)

    viewModel.changePermissionToCall(contactsAndCallPermissions.permissions[1].status.isGranted)
    if (showCallDialog.value)
        RequestCallPermission(
            contactsAndCallPermissions.permissions[1],
            context
        ) { viewModel.hideCallPermissionDialog() }


    ContactsList(modifier, viewModel)
}


@Composable
fun ContactsList(
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel
) {
    val context = LocalContext.current
    val isContactsPermissionGranted = viewModel.isContactsPermissionGranted.collectAsState()
    val isCallPermissionGranted = viewModel.isCallPermissionGranted.collectAsState()

    if (isContactsPermissionGranted.value)
        viewModel.loadContacts(context.applicationContext)

    val groupedContacts by viewModel.groupedContacts.collectAsState(emptyMap())
    if (isContactsPermissionGranted.value) {
        if (groupedContacts.isEmpty())
            Box(modifier = modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
                Text("Нет контактов для отображения")
            }
        else
            LazyColumn(modifier = modifier.fillMaxSize()) {
                groupedContacts.map { (letter, contacts) ->
                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(start = 12.dp, top = 6.dp, bottom = 6.dp)
                        ) {
                            Text(
                                text = letter,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 20.sp
                                ),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    items(contacts) { contact ->
                        ContactListItem(contact = contact, onCallClick = {

                            if (!isCallPermissionGranted.value) {
                                viewModel.showCallPermissionDialog()

                            } else {
                                val intent =
                                    Intent(Intent.ACTION_CALL, "tel:${contact.phoneNumber}".toUri())
                                context.startActivity(intent)
                            }
                        })
                    }

                }

            }
    }
}

@Composable
fun ContactListItem(contact: Contact, onCallClick: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable {
                onCallClick(contact.phoneNumber)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(end = 10.dp)
        ) {
            AsyncImage(
                model = contact.photoUri ?: (R.drawable.sharp_account_circle_24),
                contentDescription = "",
                modifier = Modifier
                    .padding(10.dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                colorFilter = if (contact.photoUri == null)
                    ColorFilter.tint(MaterialTheme.colorScheme.primary)
                else
                    null
            )
            Column(modifier = Modifier.weight(1f, true)) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
