package com.example.comalert.presentation

import android.Manifest
import android.text.format.DateUtils
import android.widget.Toast
import  androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.comalert.viewModel.AlertViewModel
import com.example.comalert.viewModel.AuthState
import com.example.comalert.viewModel.AuthViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController, alertViewModel: AlertViewModel = hiltViewModel()) {
    val triggerWord by remember { mutableStateOf("") }
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO
        )
    )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFB8C8)) // Background color for the screen
                .statusBarsPadding() // Padding for status bar
                .navigationBarsPadding() // Padding for the navigation bar
                .padding(20.dp) // General padding for inner content
        ) {
            // Title Section
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(20.dp),
                text = "COM ALERT",
                fontSize = 28.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Gray,
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                )
            )
            SectionCard(
                title = "Trigger Word",
                content = {
                    TriggerWordSection { triggerWord ->
                        alertViewModel.saveTriggerWord(triggerWord)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Add Your Voice Section
            SectionCard(
                title = "Add Your Voice",
                content = {
                    AddYourVoiceSection(triggerWord) {
                        // Handle voice trigger detection, e.g., send location to contacts
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Emergency Contacts Section
            SectionCard(
                title = "Emergency Contacts",
                content = {
                    EmergencyContactsSection { name, phone ->
                        // Save emergency contact
                       // alertViewModel.saveEmergencyContact(name, phone)
                    }
                }
            )
        }
    }

    @Composable
    fun SectionCard(title: String, content: @Composable () -> Unit) {
        // Section Card with rounded corners and shadow for visual appeal
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(8.dp, shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
          //  backgroundColor = Color(0xFF4E342E), // Background color for each section
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp) // Padding inside the section card
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                content() // This is where the content (TriggerWordSection, AddYourVoiceSection, etc.) will be added
            }
        }
    }



@Composable
fun OptionItem(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF4E342E), shape = RoundedCornerShape(4.dp))
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = description, color = Color.Gray, fontSize = 14.sp)
        }
    }
}
@Composable
fun TriggerWordSection(
    onAddTriggerWord: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var triggerWord by remember { mutableStateOf("") }

    Column {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Trigger Word")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Trigger Word" )},
                text = {
                    TextField(
                        value = triggerWord,
                        onValueChange = { triggerWord = it },
                        label = { Text("Trigger Word") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onAddTriggerWord(triggerWord)
                            showDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
@Composable
fun AddYourVoiceSection(
    triggerWord: String,
    onTriggerDetected: () -> Unit
) {
    // You will need to implement voice recognition logic
    Text(text = "Add Your Voice", color = Color.White, fontSize = 16.sp)
    // You can add a button to start listening for voice
    Button(
        onClick = {
            // Start listening for voice and check if it matches the trigger word
          //  if (/* logic to check voice against triggerWord */) {
         //       onTriggerDetected() // Handle trigger detection (send location to contacts)
        //    }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Start Voice Recognition")
    }
}
@Composable
fun EmergencyContactsSection(
    onAddContact: (String, String) -> Unit // name and phone number
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column {
        Text("Add Emergency Contact", color = Color.White, fontSize = 16.sp)
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Button(
            onClick = {
                onAddContact(name, phone)
                name = ""
                phone = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Contact")
        }
    }
}



