package com.example.mengyueli.placebook.ui

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.example.mengyueli.placebook.R
import com.example.mengyueli.placebook.adapter.BookmarkInfoWindowAdapter
import com.example.mengyueli.placebook.viewmodel.MapsViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlacePhotoMetadata
import com.google.android.gms.location.places.Places

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mapsViewModel: MapsViewModel

    private fun setupViewModel() {
        mapsViewModel =
                ViewModelProviders.of(this).get(MapsViewModel::class.java)
        createBookmarkMarkerObserver()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "Google play connection failed: " +
                connectionResult.errorMessage)
    }

    companion object {
        private const val REQUEST_LOCATION = 1
        private const val TAG = "MapsActivity"
    }

    private lateinit var map: GoogleMap

    private lateinit var googleApiClient: GoogleApiClient

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //private var locationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupGoogleClient()

        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupLocationClient()

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private fun setupMapListeners() {
        map.setInfoWindowAdapter(BookmarkInfoWindowAdapter(this))
        map.setOnPoiClickListener {
            displayPoi(it)
        }
        map.setOnInfoWindowClickListener {
            handleInfoWindowClick(it)
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap


        setupMapListeners()
        setupViewModel()
        getCurrentLocation()

        /*map.setOnPoiClickListener {
            Toast.makeText(this, it.name, Toast.LENGTH_LONG).show()
        }
        */




    }

    private fun setupGoogleClient() {
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API)
                .build()
    }

    private fun setupLocationClient() {
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION)
    }

    private fun getCurrentLocation() {
        // 1
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
// 2
            requestLocationPermissions()
        } else {


            /*
            if (locationRequest == null) {
                locationRequest = LocationRequest.create()
                locationRequest?.let { locationRequest ->
                    // 1
                    locationRequest.priority =
                            LocationRequest.PRIORITY_HIGH_ACCURACY
// 2
                    locationRequest.interval = 5000
                    // 3
                    locationRequest.fastestInterval = 1000
                    // 4
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            getCurrentLocation()
                        }
                    }
// 5
                    fusedLocationClient.requestLocationUpdates(locationRequest,
                            locationCallback, null)
                }
            }
            */
            map.isMyLocationEnabled = true
// 3
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.result != null) {
// 4
                    val latLng = LatLng(it.result.latitude, it.result.longitude)
                    // 5

                    //map.clear()

                    //map.addMarker(MarkerOptions().position(latLng).title("You are here!"))
                    // 6
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
                    // 7
                    map.moveCamera(update)
                } else { // 8
                    Log.e(TAG, "No location found")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Log.e(TAG, "Location permission denied")
            }
        }
    }


    private fun displayPoi(pointOfInterest: PointOfInterest) {
        // 1
        displayPoiGetPlaceStep(pointOfInterest)

    }

    private fun displayPoiGetPlaceStep(pointOfInterest: PointOfInterest) {
        Places.GeoDataApi.getPlaceById(googleApiClient,
                pointOfInterest.placeId)
                // 2
                .setResultCallback { places ->
                    // 3
                    if (places.status.isSuccess && places.count > 0) {
                        // 4
                        val place = places.get(0).freeze()
                        // 5
                        /*Toast.makeText(this,
                                "${place.name} ${place.phoneNumber}",
                                Toast.LENGTH_LONG).show()
                                */
                        displayPoiGetPhotoMetaDataStep(place)
                    } else {
                        Log.e(TAG,
                                "Error with getPlaceById ${places.status.statusMessage}")
                    }
    // 6
                    places.release()
                }
    }


    private fun displayPoiGetPhotoMetaDataStep(place: Place) {
        Places.GeoDataApi.getPlacePhotos(googleApiClient, place.id)
                .setResultCallback { placePhotoMetadataResult ->
                    if (placePhotoMetadataResult.status.isSuccess) {
                        val photoMetadataBuffer =
                                placePhotoMetadataResult.photoMetadata
                        if (photoMetadataBuffer.count > 0) {
                            val photo = photoMetadataBuffer.get(0).freeze()

                            displayPoiGetPhotoStep(place, photo)
                        }
                        photoMetadataBuffer.release()
                    }
                }
    }

    private fun displayPoiGetPhotoStep(place: Place, photo: PlacePhotoMetadata) {
        photo.getScaledPhoto(googleApiClient,
                resources.getDimensionPixelSize(R.dimen.default_image_width),
                resources.getDimensionPixelSize(R.dimen.default_image_height))
                .setResultCallback { placePhotoResult ->
                    if (placePhotoResult.status.isSuccess) {
                        val image = placePhotoResult.bitmap
                        displayPoiDisplayStep(place, image)
                    } else {
                        displayPoiDisplayStep(place, null)
                    }
                }
    }

    private fun displayPoiDisplayStep(place: Place, photo: Bitmap?) {
        /*
        val iconPhoto = if (photo == null) {
            BitmapDescriptorFactory
                    .defaultMarker()
        } else {
            BitmapDescriptorFactory.fromBitmap(photo)
        }
        map.addMarker(MarkerOptions()
                .position(place.latLng)
                .icon(iconPhoto)
                .title(place.name as String?)
                .snippet(place.phoneNumber as String?)
        )
        */

        val marker = map.addMarker(MarkerOptions()
                .position(place.latLng)
                .title(place.name as String?)
                .snippet(place.phoneNumber as String?)
        )
        marker?.tag = PlaceInfo(place, photo)
        marker?.showInfoWindow()


    }

    private fun handleInfoWindowClick(marker: Marker) {
        val placeInfo = (marker.tag as PlaceInfo)
        if (placeInfo.place != null && placeInfo.image != null) {

            launch(CommonPool) {
                mapsViewModel.addBookmarkFromPlace(placeInfo.place,
                        placeInfo.image)


            }
            marker.remove()
        }
    }

    private fun addPlaceMarker(
            bookmark: MapsViewModel.BookmarkMarkerView): Marker? {
        val marker = map.addMarker(MarkerOptions()
                .position(bookmark.location)
                .title(bookmark.name)
                .snippet(bookmark.phone)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_AZURE))
                .alpha(0.8f))
        marker.tag = bookmark
        return marker
    }

    private fun displayAllBookmarks(
            bookmarks: List<MapsViewModel.BookmarkMarkerView>) {
        for (bookmark in bookmarks) {
            addPlaceMarker(bookmark)
        }
    }

    private fun createBookmarkMarkerObserver() {
        // 1
        mapsViewModel.getBookmarkMarkerViews()?.observe(
                this, android.arch.lifecycle
                .Observer<List<MapsViewModel.BookmarkMarkerView>> {
                    // 2
                    map.clear()
// 3
                    it?.let {
                        displayAllBookmarks(it)
                    }
                })
    }



    class PlaceInfo(val place: Place? = null,
                     val image: Bitmap? = null)
}

