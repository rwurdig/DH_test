package com.rwurdig.hdbodygraph.ui

import androidx.lifecycle.viewmodel.compose.viewModel
import com.rwurdig.hdbodygraph.viewmodel.BirthDataViewModel
import com.rwurdig.hdbodygraph.R          


@Composable
fun BirthDataInputScreen(
    viewModel: BirthDataViewModel = viewModel()
) {
    // Reading UI state from ViewModel using StateFlow
    val suggestions by viewModel.placeSuggestions.collectAsState()
    val selectedDate by viewModel.birthDate.collectAsState()
    val selectedTime by viewModel.birthTime.collectAsState()
    val locationText by viewModel.locationName.collectAsState()
}        Places.initialize(this, applicationContext.getString(R.string.google_maps_key))
