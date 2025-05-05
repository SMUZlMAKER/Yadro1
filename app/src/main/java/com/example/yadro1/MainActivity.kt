package com.example.yadro1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.yadro1.ui.theme.Yadro1Theme
import com.example.yadro1.view.Contact
import com.example.yadro1.viewModel.ContactsViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Yadro1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Contact(Modifier.padding(innerPadding), ContactsViewModel())

                }
            }
        }
    }
}