package com.example.humandesign.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDataInputScreen(onSubmit: (LocalDate, LocalTime, String) -> Unit) {
    // State for date, time, and location input
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var locationQuery by remember { mutableStateOf("") }
    var locationSuggestions by remember { mutableStateOf(listOf<String>()) }

    // Dialog picker states
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Date/time formatters for display
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Enter Birth Data",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )
        // Date picker field
        OutlinedTextField(
            value = selectedDate?.format(dateFormatter) ?: "",
            onValueChange = { /* no-op (readOnly) */ },
            label = { Text("Birth Date") },
            placeholder = { Text("Select date") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date")
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        // Time picker field
        OutlinedTextField(
            value = selectedTime?.format(timeFormatter) ?: "",
            onValueChange = { /* no-op */ },
            label = { Text("Birth Time") },
            placeholder = { Text("Select time") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = "Select Time")
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        // Location autocomplete field with suggestions dropdown
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedTextField(
                value = locationQuery,
                onValueChange = { query ->
                    locationQuery = query
                    // Call Places API for suggestions (this example simulates suggestions after 3 chars)
                    locationSuggestions = if (query.length >= 3) {
                        listOf("$query City", "$query, Some Country")
                    } else {
                        emptyList()
                    }
                },
                label = { Text("Birth Location") },
                placeholder = { Text("Enter birth place") },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = locationSuggestions.isNotEmpty(),
                onDismissRequest = { locationSuggestions = emptyList() },
                modifier = Modifier.fillMaxWidth()
            ) {
                locationSuggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = {
                            // User selected a suggestion
                            locationQuery = suggestion
                            locationSuggestions = emptyList()
                            // Normally, here we would retrieve the Place details (lat/long, timezone) for the selected place.
                        }
                    )
                }
            }
        }
        // Submit button
        Button(
            onClick = {
                // Only invoke onSubmit if all data is provided
                if (selectedDate != null && selectedTime != null && locationQuery.isNotBlank()) {
                    onSubmit(selectedDate!!, selectedTime!!, locationQuery)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Human Design Chart")
        }
    }

    // DatePicker dialog (modal)
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Get selected date from picker state
                    // (DatePicker in Material3 provides selected date in UTC milliseconds)
                    val millis = datePickerState.selectedDateMillis ?: 0L
                    selectedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC).toLocalDate()
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli())
            DatePicker(state = datePickerState)
        }
    }

    // TimePicker dialog (modal)
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime?.hour ?: 12,
            initialMinute = selectedTime?.minute ?: 0,
            is24Hour = true
        )
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Construct LocalTime from picker state
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}
