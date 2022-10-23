package iot.empiaurhouse.bayardte.view

import android.Manifest
import android.app.ActivityOptions
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import iot.empiaurhouse.bayardte.R
import iot.empiaurhouse.bayardte.adapters.SwipeToDeleteCallback
import iot.empiaurhouse.bayardte.adapters.TEMarkersAdapter
import iot.empiaurhouse.bayardte.databinding.ActivityTeactivityBinding
import iot.empiaurhouse.bayardte.model.APIStatus
import iot.empiaurhouse.bayardte.model.TEMarker
import iot.empiaurhouse.bayardte.utils.SunDial
import iot.empiaurhouse.bayardte.utils.UserPreferenceManager
import iot.empiaurhouse.bayardte.utils.WeatherForcaster
import iot.empiaurhouse.bayardte.viewmodel.TEMarkerViewModel
import iot.empiaurhouse.bayardte.viewmodel.WeatherViewModel
import java.util.*


class TEActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeactivityBinding
    private lateinit var hubMenu: Menu
    private lateinit var quickMarkerButton: FloatingActionButton
    private lateinit var navigationView: NavigationView
    private lateinit var hubDrawer: DrawerLayout
    private lateinit var navView: View
    private lateinit var userManager: UserPreferenceManager
    private lateinit var teMarkerViewModel: TEMarkerViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var dashboardSwipeRefresh: SwipeRefreshLayout
    private var teMarkersFound = arrayListOf<TEMarker>()
    private var teMarkersRecyclerView: RecyclerView? = null
    private var teMarkersRVA: TEMarkersAdapter? = null
    private lateinit var app: Application
    private lateinit var noResultsText: TextView
    private lateinit var pendingResultsText: TextView
    private lateinit var imgBox: ImageView
    private lateinit var videoBox: VideoView
    private lateinit var bteTime: TextView
    private lateinit var bteTimePeriod: TextView
    private lateinit var bteTimeZone: TextView
    private lateinit var bteCountry: TextView
    private lateinit var bteProvince: TextView
    private lateinit var bteCity: TextView
    private lateinit var bteWeatherTV: TextView
    private lateinit var meCopyright: TextView
    private lateinit var bteWeather: String
    private var bteLiveWeather: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private lateinit var geocoder: Geocoder
    private var addresses = arrayListOf<Address>()
    private var weatherStatus = arrayListOf<APIStatus>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeactivityBinding.inflate(layoutInflater)
        teMarkerViewModel = ViewModelProvider(this)[TEMarkerViewModel::class.java]
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext!!)
        userManager = UserPreferenceManager(this)
        val viewSetup = binding.root
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale)
        setContentView(viewSetup)
        currentLocation = Location("LocationManager.GPS_PROVIDER")
        currentLocation.longitude = 0.0
        currentLocation.latitude = 0.0
        bteWeather = userManager.getBayardWeatherData().toString()
        hubMenu = binding.toolbar.menu
        navigationView = binding.hubDrawerNavView
        hubDrawer = binding.hubDrawerView
        quickMarkerButton = binding.hubMarkerButton
        dashboardSwipeRefresh = binding.bteView.bteSwipeRefresh
        noResultsText = binding.bteView.bayardResultsText
        pendingResultsText = binding.bteView.bayardPendingText
        bteWeatherTV = binding.bteView.bteWeatherText
        imgBox = binding.bteView.bayardImgBox
        videoBox = binding.bteView.bayardVideoBox
        bteTime = binding.bteView.bteTimeText
        bteTimePeriod = binding.bteView.bteTimeTitle
        bteTimeZone = binding.bteView.bteTzText
        bteCountry = binding.bteView.bteCountryText
        bteCity = binding.bteView.bteCityText
        bteProvince = binding.bteView.bteProvinceText
        meCopyright = binding.bteView.bteCopyright
        dashboardSwipeRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.black, null))
        initSideOptionsMenu()
        initView()
        if(isLocationEnabled()) getLocationUpdates()
    }



    private fun initView(){
        invalidateOptionsMenu()
        navView = navigationView.getHeaderView(0)
        val wearableName: TextView = navView.findViewById(R.id.wearable_name_sidemenu)
        bteCountry.text = " -- "
        bteProvince.text = " -- "
        bteCity.text = " -- "
        bteTimeZone.text = SunDial().fetchTimeZone()
        wearableName.text = "Chrome Test"
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(null) }
        quickMarkerButton.setOnClickListener {
            startActivity(Intent(this@TEActivity, AtlasEditActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this@TEActivity).toBundle()) }
        app = this.application
        teMarkerViewModel.processTEMarkers(app)
        teMarkersRecyclerView = binding.bteView.bayardHubRv
        teMarkersRecyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        teMarkersFound = fetchTEMarkerDB()
        if (weatherStatus == null) bteWeatherTV.text = userManager.getBayardWeatherData()
        meCopyright.text = SunDial().fetchCopyrightYear()
        if (teMarkersFound.size > 2) binding.bteView.bteCopyright.visibility = View.VISIBLE
        initRefresh()
        initSwipeDeleteGesture()
        binding.teMenu.setOnClickListener {
            if (!hubDrawer.isDrawerOpen(GravityCompat.START)) {
                hubDrawer.openDrawer(GravityCompat.START)
            } else {
                hubDrawer.closeDrawer(GravityCompat.END)
            }
        }
    }



    private fun initSideOptionsMenu(){
        binding.hubDrawerNavView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bayard_te_menu -> {
                    startActivity(
                        Intent(this@TEActivity, BTEActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this@TEActivity).toBundle()
                    )
                    true
                }
                R.id.wear_synopsis_menu -> {
                    startActivity(
                        Intent(this@TEActivity, WearSynopsisActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this@TEActivity).toBundle()
                    )
                    true
                }
                R.id.about_me_menu -> {
                    startActivity(
                        Intent(this@TEActivity, AboutMEActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this@TEActivity).toBundle()
                    )
                    true
                }
                R.id.chroma_profile -> {
                    startActivity(
                        Intent(this@TEActivity, ChromaProfileActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this@TEActivity).toBundle()
                    )
                    true
                }
                R.id.settings_menu -> {
                    startActivity(
                        Intent(this@TEActivity, SettingsPreferencesActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this@TEActivity).toBundle()
                    )
                    true
                }
                else -> false
            }
        }
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

    private fun viewRefresh(){
        bteTimeZone.text = SunDial().fetchTimeZone()
        bteWeatherTV.text = "${userManager.getBayardWeatherData()}"
        teMarkersRecyclerView?.adapter = null
        teMarkersRVA = null
        teMarkersRecyclerView = null
        pendingResultsText.visibility = View.VISIBLE
        meCopyright.visibility = View.INVISIBLE
        requestBTEPermissions()
        fetchBTEWeather()
        Handler(Looper.getMainLooper()).postDelayed({
            teMarkersFound = fetchTEMarkerDB()
            if (teMarkersFound.size > 2) binding.bteView.bteCopyright.visibility = View.VISIBLE
            if (teMarkersFound.size < 2) binding.bteView.bteCopyright.visibility = View.INVISIBLE
            teMarkersRecyclerView = binding.bteView.bayardHubRv
            teMarkersRVA =
                    TEMarkersAdapter(teMarkersFound, binding.root, this, app, currentLocation)
            teMarkersRecyclerView!!.adapter = teMarkersRVA
            noResultsView(teMarkersFound.size)
        }, 444)
        initSwipeDeleteGesture()
    }

    private fun initTEMBox(imgBox: ImageView, videoBox: VideoView){
        imgBox.visibility = View.INVISIBLE
        videoBox.visibility = View.VISIBLE
        val timeOfDay = SunDial()
        if (timeOfDay.fetchGreeting() == "Good Evening"){
            videoBox.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.cgy_nightskyline))
        }
        if (timeOfDay.fetchGreeting() == "Dusk"){
            videoBox.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.cgy_nightskyline))
        }
        if (timeOfDay.fetchGreeting() == "Merry Christmas"){
            imgBox.setImageResource(R.drawable.holiday_bckgrnd)
        }
        else if (timeOfDay.fetchGreeting() == "Good Morning" || timeOfDay.fetchGreeting() == "Good Afternoon") {
            videoBox.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.cgy_light_skyline))
        }
        videoBox.setOnPreparedListener(OnPreparedListener { mp -> mp.isLooping = true })
        videoBox.start()
    }

    private fun initRefresh(){
        dashboardSwipeRefresh.setOnRefreshListener {
            viewRefresh()
            initTEMBox(imgBox, videoBox)
            dashboardSwipeRefresh.isRefreshing = false
        }
    }

    private fun fetchBTEWeather(){
        Handler(Looper.getMainLooper()).postDelayed({
           bteWeatherTV.text = "${userManager.getBayardWeatherData()}"
        }, 1111)
    }

    private fun noResultsView(recordsFound: Int){
        if (recordsFound < 1){
            if (teMarkersRecyclerView != null) {
                teMarkersRecyclerView!!.visibility = View.GONE
            }
            pendingResultsText.visibility = View.INVISIBLE
            noResultsText.visibility = View.VISIBLE
        }
        if (recordsFound >= 3) {
            meCopyright.visibility = View.VISIBLE
            pendingResultsText.visibility = View.INVISIBLE
        }
        else if (recordsFound > 0){
            noResultsText.visibility = View.GONE
            if (teMarkersRecyclerView != null) {
                teMarkersRecyclerView!!.visibility = View.VISIBLE
                pendingResultsText.visibility = View.INVISIBLE
            }
        }
    }

    private fun initSwipeDeleteGesture(){
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = teMarkersRVA
                adapter!!.deleteTEMarker(viewHolder.adapterPosition)
                noResultsView(fetchTEMarkerDB().size)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(teMarkersRecyclerView)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
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

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) && isLocationEnabled()
                ) {
                    if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED)) {
                        viewRefresh()
                    }
                } else if ((grantResults.isNotEmpty() && (grantResults[1]) == PackageManager.PERMISSION_GRANTED) && isLocationEnabled()) {
                    viewRefresh()
                }else{
                    finish()
                }
                return
            }
        }
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
                    // get latest location
                    val location = locationResult.lastLocation
                    if(location != null){
                        currentLocation = location
                        bteLiveWeather = WeatherForcaster(location.latitude.toString(), location.longitude.toString(), applicationContext).processWeather()
                        if (bteLiveWeather != null) {
                            userManager.storeBayardWeatherData(bteLiveWeather)
                            bteWeatherTV.text = bteLiveWeather
                        }
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                    geocoder = Geocoder(applicationContext, Locale.getDefault())
                    addresses = geocoder.getFromLocation(location!!.latitude, location.longitude, 1) as ArrayList<Address>
                    bteCountry.visibility = View.VISIBLE
                    bteCountry.text = addresses[0].locale.isO3Country
                    if (addresses[0].locality != null){
                        bteCity.text = addresses[0].locality
                    }else{
                        if (addresses[0].locality != null) bteCity.text = addresses[0].subLocality
                        if (addresses[0].subLocality == null) bteCity.text = addresses[0].subAdminArea
                        if (addresses[0].subAdminArea == null) bteCity.text = addresses[0].adminArea
                    }
                    if (addresses[0].adminArea != null){
                        bteProvince.text = addresses[0].adminArea
                    }else{
                        if (addresses[0].adminArea == null) bteProvince.text = addresses[0].subAdminArea
                    }
                    }, 1111)

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


    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onPause() {
        super.onPause()
        if(isLocationEnabled()) stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if(isLocationEnabled()) startLocationUpdates()
        viewRefresh()
        initTEMBox(imgBox, videoBox)
        if ((!teMarkersFound.isNullOrEmpty())  && ((teMarkersFound.size) != (fetchTEMarkerDB().size))){
            Handler(Looper.getMainLooper()).postDelayed({
                viewRefresh()
            }, 1111)
        }
    }

    override fun onBackPressed()
    {
        if (hubDrawer.isDrawerOpen(GravityCompat.START)) {
            hubDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}