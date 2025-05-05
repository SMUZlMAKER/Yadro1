package com.example.yadro1.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestContactsPermission(permissionState: PermissionState, context: Context) {
    RequestPermission(permissionState, context) {}
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCallPermission(
    permissionState: PermissionState,
    context: Context,
    onDismiss: () -> Unit
) {
    RequestPermission(permissionState, context, onDismiss)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestPermission(
    permissionState: PermissionState,
    context: Context,
    onDismiss: () -> Unit
) {
    var lambda = { permissionState.launchPermissionRequest() }
    var message = "Предоставьте в следующем окне"

    if (permissionState.status.shouldShowRationale) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(
                "package",
                context.packageName,
                null
            )
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        lambda = { context.startActivity(intent) }
        message = "\nПредоставьте его в настройках."
    }
    var title = ""
    if (permissionState.permission == "android.permission.READ_CONTACTS") {
        title = "Для отображения контактов требуется разрешение."
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = lambda) {
                    Text("Продолжить")
                }
            },
            title = { Text(title) },
            text = { Text(message) },
        )
    } else {
        title = "Для совершения звонка требуется разрешение."
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    lambda()
                    onDismiss()
                }) {
                    Text("Продолжить")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
            },
            title = { Text(title) },
            text = { Text(message) },
        )
    }

}


