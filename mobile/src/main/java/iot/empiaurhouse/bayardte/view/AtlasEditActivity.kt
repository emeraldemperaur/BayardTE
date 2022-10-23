package iot.empiaurhouse.bayardte.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.transition.Slide
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import iot.empiaurhouse.bayardte.databinding.ActivityAtlasEditBinding
import iot.empiaurhouse.bayardte.model.TEMarker
import iot.empiaurhouse.bayardte.utils.EditValidator
import iot.empiaurhouse.bayardte.utils.PaletteManager
import iot.empiaurhouse.bayardte.utils.SunDial
import iot.empiaurhouse.bayardte.utils.subscribeOnBackground
import iot.empiaurhouse.bayardte.viewmodel.TEMarkerViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class AtlasEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAtlasEditBinding
    private lateinit var teMarkerViewModel: TEMarkerViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private lateinit var geocoder: Geocoder
    private var addresses = arrayListOf<Address>()
    private var teMarkersFound = arrayListOf<TEMarker>()
    private var hexCodes = arrayListOf<String>()
    private lateinit var editLatitude: TextView
    private lateinit var editLongitude: TextView
    private lateinit var editTimeZone: TextView
    private lateinit var editCity: TextView
    private lateinit var editCountry: TextView
    private lateinit var editProvince: TextView
    private lateinit var editLatitudeStr: String
    private lateinit var editLongitudeStr: String
    private lateinit var editTimeZoneStr: String
    private lateinit var editCityStr: String
    private lateinit var editCountryCodeStr: String
    private lateinit var editCountryStr: String
    private lateinit var editProvinceStr: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtlasEditBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext!!)
        teMarkerViewModel = ViewModelProvider(this)[TEMarkerViewModel::class.java]
        val viewSetup = binding.root
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Slide()
            enterTransition.duration = 769
        }
        editLatitude = binding.editLatitudeText
        editLongitude = binding.editLongitudeText
        editTimeZone = binding.editTimeZoneText
        editCity = binding.editCityText
        editProvince = binding.editProvinceText
        editCountry = binding.editCountryText
        setContentView(viewSetup)
        initView()
        requestBTEPermissions()
        if (isLocationEnabled()){
            getLocationUpdates()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun initView() {
        teMarkerViewModel.processTEMarkers(this.application)
        editTimeZone.text = SunDial().fetchTimeZone()
        binding.editAtlasClose.setOnClickListener {
            finish()
        }

        binding.editSubmitButton.setOnClickListener {
            val title = EditValidator()
            title.initValidator(binding.root)
            if (title.isValidText(binding.aliasEditFieldText,binding.aliasEditField) &&
                title.inputUnique(binding.aliasEditFieldText.text.toString(),teMarkersFound,binding.aliasEditField)) {
                createAtlasMarker()
            }
        }
        teMarkersFound = fetchTEMarkerDB()

        //Handler(Looper.getMainLooper()).postDelayed({

       // }, 1111)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getLocationUpdates()
    {
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    currentLocation = location!!
                    geocoder = Geocoder(applicationContext, Locale.getDefault())
                    addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1) as ArrayList<Address>
                    editCountryCodeStr = addresses[0].locale.isO3Country
                    editLatitudeStr = addresses[0].latitude.toString()
                    editLongitudeStr = addresses[0].longitude.toString()
                    editCountryStr = addresses[0].countryName
                    hexCodes = PaletteManager(applicationContext).initCountryPalette(editCountryStr)
                    editLatitude.text = editLatitudeStr
                    editLongitude.text = editLongitudeStr
                    editCountry.text = editCountryCodeStr
                    if (addresses[0].locality != null){
                        editCityStr = addresses[0].locality
                        editCity.text = editCityStr
                    }else{
                        if (addresses[0].locality != null) editCityStr = addresses[0].subLocality
                        if (addresses[0].subLocality == null) editCityStr = addresses[0].subAdminArea
                        if (addresses[0].subAdminArea == null) editCityStr = addresses[0].adminArea
                        editCity.text = editCityStr
                    }
                    if (addresses[0].adminArea != null){
                        editProvinceStr = addresses[0].adminArea
                        editProvince.text = editProvinceStr
                    }else{
                        if (addresses[0].adminArea == null) editProvinceStr = addresses[0].subAdminArea
                        editProvince.text = editProvinceStr
                    }
                }
            }
        }
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            return
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun requestBTEPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) { if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun fetchTEMarkerDB(): ArrayList<TEMarker> {
        var result: Boolean
        val fetchedTEMarkers = arrayListOf<TEMarker>()

        teMarkerViewModel.fetchTEMarkerList()
        teMarkerViewModel.teMarkers.observe(
            this,
            androidx.lifecycle.Observer { reply ->
                reply?.let {
                    result = reply.isNotEmpty()
                    if (fetchedTEMarkers.isEmpty()) {
                        fetchedTEMarkers.addAll(reply)
                        teMarkersFound = fetchedTEMarkers
                        println("TE Markers response object is not empty: $result")
                        println("See TE Markers response result: $reply")
                    }
                }
            })
        return fetchedTEMarkers
    }


    private fun createAtlasMarker(){
        val alias = binding.aliasEditFieldText.text.toString().trim()
        val timeZoneObj = ZoneId.systemDefault().toString()
        val createdOnTimeStamp = LocalDate.now().toString()
        val hexCheck = EditValidator()
        val stagedTEMarker = TEMarker(alias = alias, country = editCountryStr, countryCode = editCountryCodeStr,
            province = editProvinceStr,city = editCityStr,timeZone = SunDial().fetchTimeZone(),
            timeZoneObj = timeZoneObj,latitude = editLatitudeStr,
            hexCode1=hexCheck.hexCleaner(hexCodes[0]), hexCode2=hexCheck.hexCleaner(hexCodes[1]),
            hexCode3=hexCheck.hexCleaner(hexCodes[2]),
            longitude = editLongitudeStr,createdOnTimeStamp = createdOnTimeStamp)
        subscribeOnBackground {
            teMarkerViewModel.insertTEMarker(stagedTEMarker)
            println("staged TEMarker added to DB: $stagedTEMarker")
        }
        finish()
    }


}