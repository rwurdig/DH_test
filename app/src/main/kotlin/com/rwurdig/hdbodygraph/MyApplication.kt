// This class is used to initialize the Places API with the API key defined in strings.xml.


package com.rwurdig.hdbodygraph

import android.app.Application
import com.google.android.libraries.places.api.Places

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialise Places once with your key from strings.xml
        Places.initialize(this, getString(R.string.google_maps_key))
    }
}
