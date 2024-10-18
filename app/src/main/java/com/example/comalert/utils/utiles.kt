
package com.example.comalert.utils

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast

object LocationSender {
    fun sendLocationToContacts(context: Context, location: String) {
        // Retrieve the emergency contacts (you can store them in SharedPreferences or a database)
        val contacts = listOf("1234567890", "0987654321") // Example phone numbers

        val smsManager = SmsManager.getDefault()

        contacts.forEach { contact ->
            smsManager.sendTextMessage(contact, null, "Emergency! My location is: $location", null, null)
        }

        Toast.makeText(context, "Location sent to emergency contacts", Toast.LENGTH_SHORT).show()
    }
}
