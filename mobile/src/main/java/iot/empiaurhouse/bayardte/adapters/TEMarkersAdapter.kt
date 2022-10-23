package iot.empiaurhouse.bayardte.adapters

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import iot.empiaurhouse.bayardte.R
import iot.empiaurhouse.bayardte.model.TEMarker
import iot.empiaurhouse.bayardte.utils.UserPreferenceManager
import iot.empiaurhouse.bayardte.utils.subscribeOnBackground
import iot.empiaurhouse.bayardte.viewmodel.TEMarkerViewModel
import java.lang.String
import java.util.*

class TEMarkersAdapter(private val teMarkerList: ArrayList<TEMarker>, private val temViewObject: View,
                       private val activity: ViewModelStoreOwner,
                       private val application: Application,
                       private val bteLocation: Location
): RecyclerView.Adapter<TEMarkersAdapter.ViewHolder>() {

    private lateinit var teContext: Context
    private lateinit var hubView: View
    private lateinit var teMarkerViewModel: TEMarkerViewModel
    private lateinit var userManager: UserPreferenceManager



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.te_marker_list_view, parent, false)
        teContext = parent.context
        userManager = UserPreferenceManager(teContext)
        teMarkerViewModel = ViewModelProvider(activity)[TEMarkerViewModel::class.java]
        teMarkerViewModel.processTEMarkers(application)
        hubView = temViewObject
        val holder = ViewHolder(v)
        holder.teMarkersItem.setOnClickListener {  }
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return teMarkerList.size
    }

    private fun restoreTEMarker(teMarker: TEMarker, position: Int){
        teMarkerList.add(position, teMarker)
        subscribeOnBackground {
            teMarkerViewModel.insertTEMarker(teMarker)
            println("Restored TE Marker to DB:\n $teMarker")
        }
        notifyItemInserted(position)
    }

    fun deleteTEMarker(position: Int){
        val focusTEMarker = teMarkerList[position]
        subscribeOnBackground {
            teMarkerViewModel.deleteTEMarker(focusTEMarker)
            println("Deleted TE Marker from DB:\n $focusTEMarker")
        }
        teMarkerList.removeAt(position)
        notifyItemRemoved(position)
        val deletedPrompt = Snackbar.make(temViewObject,"Deleted TE Marker | ${focusTEMarker.alias.capitalize(
            Locale.ROOT
        )
        }", Snackbar.LENGTH_LONG)
        deletedPrompt.setAction("UNDO", View.OnClickListener {

            restoreTEMarker(focusTEMarker, position)

        })
        deletedPrompt.show()

    }

    private fun profileInit(teMarker: TEMarker){
        userManager.storeChromaProfile(teMarker.country)
        userManager.storeChromaData(teMarker.hexCode1!!,
            teMarker.hexCode2!!, teMarker.hexCode3!!)
    }

    private fun fetchKmFrom(thisLocation: Location, teMarker: TEMarker): kotlin.String{
        var kmStr = ""
        var kmFrom = (0).toFloat()
        val teLocation = Location("LocationManager.GPS_PROVIDER")
        if (teMarker.latitude  != "No Data" || teMarker.longitude != "No Data"){
            teLocation.latitude = teMarker.latitude.toDouble()
            teLocation.longitude = teMarker.longitude.toDouble()
            kmFrom = thisLocation.distanceTo(teLocation)/ 1000
        }else if (teMarker.latitude  == "No Data" || teMarker.longitude == "No Data"){
            kmStr = "No Data"
        }
        kmStr = String.format(Locale.getDefault(), "%.2f", kmFrom)
            .toString() + " Km"
        return kmStr
    }
    

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val teMarker = teMarkerList[position]
        holder.teMarkersHex1.setColorFilter(Color.parseColor(teMarker.hexCode1))
        holder.teMarkersHex2.setColorFilter(Color.parseColor(teMarker.hexCode2))
        holder.teMarkersHex3.setColorFilter(Color.parseColor(teMarker.hexCode3))
        holder.teMarkersLabel.text = teMarker.alias.capitalize(Locale.ROOT)
        if (teMarker.countryCode.isNotEmpty()){
            holder.teMarkersCountry.text = teMarker.countryCode.uppercase()
        }else{
            holder.teMarkersCountry.text = "NDF"
        }
        if (teMarker.city.isNotEmpty()) holder.teMarkersCity.text = teMarker.city.capitalize(Locale.ROOT)
        else if (teMarker.province.isNotEmpty()) holder.teMarkersCity.text = teMarker.province.capitalize(Locale.ROOT)
        if (teMarker.city.isEmpty() && teMarker.province.isEmpty()) holder.teMarkersCity.text = "No Data"
        if (teMarker.timeZone.isNotEmpty()) holder.teMarkersLocalTime.text = teMarker.timeZone
        if (fetchKmFrom(bteLocation, teMarker).isNotEmpty()) holder.teMarkersDistance.text = fetchKmFrom(bteLocation, teMarker)
        if (fetchKmFrom(bteLocation, teMarker).isEmpty()) holder.teMarkersDistance.text = "No Data"
        val holderText = ""
        holder.teMarkersItem.setOnClickListener {
            // SET TEM COLOR PROFILE ON WEAR DEVICE
            // SET ACTIVE CHROMA PROFILE TO ALIAS & HEX CODES
            profileInit(teMarker)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val teMarkersLabel: TextView = itemView.findViewById(R.id.marker_list_label)
        val teMarkersHex1: ImageView = itemView.findViewById(R.id.tem_hex_view_1)
        val teMarkersHex2: ImageView = itemView.findViewById(R.id.tem_hex_view_2)
        val teMarkersHex3: ImageView = itemView.findViewById(R.id.tem_hex_view_3)
        val teMarkersCity: TextView = itemView.findViewById(R.id.marker_city_list_label)
        val teMarkersCountry: TextView = itemView.findViewById(R.id.country_list_label)
        val teMarkersLocalTime: TextView = itemView.findViewById(R.id.country_tz_text)
        val teMarkersDistance:  TextView = itemView.findViewById(R.id.distance_km_text)
        val teMarkersItem: MaterialCardView = itemView.findViewById(R.id.tem_list_view)

    }


}