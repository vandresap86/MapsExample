package com.example.mapsexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map:GoogleMap
    private var starRoute: String = ""
    private var endRoute: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker(LatLng(19.427931, -99.056285),
            MarkerOptions().position(LatLng(19.427931, -99.056285)).title("Mi playa favorita"))
        starRoute = "-99.056285".plus(",").plus("19.427931")
        createMarker(LatLng(19.427384, -99.056210),
            MarkerOptions().position(LatLng(19.427384, -99.056210)).title("Mi Canton"))
        endRoute = "-99.056210".plus(",").plus("19.427384")
        createRoute(starRoute, endRoute)

        //2da Entrega
        createMarker(LatLng(19.428360, -99.056094),
            MarkerOptions().position(LatLng(19.428360, -99.056094)).title("2da Entrega"))
        starRoute = "-99.056210".plus(",").plus("19.427384")
        endRoute = "-99.056094".plus(",").plus("19.428360")
        createRoute(starRoute, endRoute)
        //3er Entrega
        createMarker(LatLng(19.428906, -99.058355),
            MarkerOptions().position(LatLng(19.428906, -99.058355)).title("3er Entrega"))
        starRoute = "-99.056094".plus(",").plus("19.428360")
        endRoute = "-99.058355".plus(",").plus("19.428906")
        createRoute(starRoute, endRoute)
/*        createMarker(LatLng(19.42722029737091, -99.05675105962034),
            MarkerOptions().position(LatLng(19.42722029737091, -99.05675105962034)).title("Vecino")
        )*/
    }

    private fun createMarker(coordinate: LatLng, marker: MarkerOptions){
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinate, 18f),
            400,
            null
        )
    }

    private fun createRoute(starRoute: String, endRoute: String) {
        Log.d("VAAP", starRoute)
        Log.d("VAAP", endRoute)
        CoroutineScope(Dispatchers.IO).launch {
         val call = getRetrofit()
             .create(ApiService::class.java)
             .getRoute("5b3ce3597851110001cf6248bbeffefb51fa443c81055a40c22883e2",
                 starRoute,
                 endRoute
             )
            if (call.isSuccessful){
                Log.d("VAAP", "OK")
                drawRoute(call.body())
            }else{
                Log.d("VAAP", "KO")
            }
        }
    }

    private fun drawRoute(routeResponse: RouteResponse?) {
        val polyLineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polyLineOptions.add(LatLng(it[1], it[0]))
        }
        runOnUiThread {
            val poly = map.addPolyline(polyLineOptions)
        }
    }

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}