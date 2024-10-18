package com.example.comalert.viewModel

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comalert.data.remote.Alert
import com.example.comalert.data.remote.AlertRepository
import com.example.comalert.utils.LocationSender
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertViewModel @Inject constructor(
    private val repository: AlertRepository,
) : ViewModel() {

    val recentAlerts: LiveData<List<Alert>> = repository.recentAlerts

    private val triggerWordLiveData = MutableLiveData<String?>()
    val triggerWord: LiveData<String?> = triggerWordLiveData

    private val locationLiveData = MutableLiveData<String?>()
    val location: LiveData<String?> = locationLiveData

    // Save the trigger word
    fun saveTriggerWord(triggerWord: String) {
        triggerWordLiveData.postValue(triggerWord)
    }

    // Get the saved trigger word
    fun getTriggerWord(): String? {
        return triggerWordLiveData.value
    }

    // Get alert by ID
    fun getAlertById(id: String): LiveData<Alert?> {
        val alertLiveData = MutableLiveData<Alert?>()
        viewModelScope.launch {
            val alert = repository.getAlertById(id)
            alertLiveData.postValue(alert)
        }
        return alertLiveData
    }

    // Insert new alert
    fun insertAlert(alert: Alert) {
        viewModelScope.launch {
            repository.insertAlert(alert)
        }
    }

    // Start listening for the trigger word (using SpeechRecognizer)
    fun startListeningForVoice(context: Context) {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Ready for speech input
            }

            override fun onBeginningOfSpeech() {
                // Beginning of speech detected
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Handle volume changes, if necessary
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Handle buffer received
            }

            override fun onEndOfSpeech() {
                // Speech ended
            }

            override fun onError(error: Int) {
                // Handle errors
            }

            override fun onResults(bundle: Bundle) {
                val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val triggerWord = getTriggerWord()

                // Check if any of the recognized words match the trigger word
                matches?.forEach { match ->
                    if (match.equals(triggerWord, ignoreCase = true)) {
                        // Trigger word detected, get the location and send it to contacts
                        getLocationAndSendToContacts(context)
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Handle partial results
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Handle events
            }
        })

        speechRecognizer.startListening(intent)
    }

    // Get the current location and send it to contacts
    private fun getLocationAndSendToContacts(context: Context) {
        // You can use the LocationServices API to get the user's current location
        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val locationString = "Latitude: ${it.latitude}, Longitude: ${it.longitude}"
                locationLiveData.postValue(locationString)

                // Send location to emergency contacts
                LocationSender.sendLocationToContacts(context, locationString)
            } ?: run {
                Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


