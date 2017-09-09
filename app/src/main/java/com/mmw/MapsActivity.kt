package com.mmw

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.mmw.model.Stage

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        @JvmStatic val STAGES_INTENT_KEY = "stages"
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val builder = LatLngBounds.Builder()
        val stages = intent.getParcelableArrayListExtra<Stage>(STAGES_INTENT_KEY)
        for (stage: Stage in stages) {
            val marker = MarkerOptions()
                    .position(LatLng(stage.latitude!!, stage.longitude!!))
                    .title(stage.title)
            mMap.addMarker(marker)

            builder.include(marker.position)
        }

        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 120)
        mMap.moveCamera(cu)
    }
}
