package iot.empiaurhouse.bayardte.adapters

import android.app.Application
import android.content.Context
import android.graphics.Color
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
import iot.empiaurhouse.bayardte.model.ChromaProfile
import iot.empiaurhouse.bayardte.utils.UserPreferenceManager
import iot.empiaurhouse.bayardte.utils.subscribeOnBackground
import iot.empiaurhouse.bayardte.viewmodel.ChromaProfileViewModel
import java.util.*


class ChromaProfilesAdapter(private val chromaProfileList: ArrayList<ChromaProfile>, private val profileViewObject: TextView,
                            private val activity: ViewModelStoreOwner,
                            private val application: Application,
                            private val hexPreview1: ImageView, private val hexPreviewCode1: TextView,
                            private val hexPreview2: ImageView, private val hexPreviewCode2: TextView,
                            private val hexPreview3: ImageView, private val hexPreviewCode3: TextView
): RecyclerView.Adapter<ChromaProfilesAdapter.ViewHolder>() {

    private lateinit var teContext: Context
    private lateinit var hubView: View
    private lateinit var chromaProfileViewModel: ChromaProfileViewModel
    private lateinit var userManager: UserPreferenceManager



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.chroma_list_view, parent, false)
        teContext = parent.context
        userManager = UserPreferenceManager(teContext)
        chromaProfileViewModel = ViewModelProvider(activity)[ChromaProfileViewModel::class.java]
        chromaProfileViewModel.processChromaProfiles(application)
        hubView = profileViewObject
        val holder = ViewHolder(v)
        holder.chromaProfileItem.setOnClickListener {  }
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return chromaProfileList.size
    }

    private fun profileInit(chromaProfile: ChromaProfile){
        userManager.storeChromaProfile("Custom")
        userManager.storeChromaData(chromaProfile.hexCode1,
            chromaProfile.hexCode2, chromaProfile.hexCode3)

    }

    private fun restoreChromaProfile(chromaProfile: ChromaProfile, position: Int){
        chromaProfileList.add(position, chromaProfile)
        subscribeOnBackground {
            chromaProfileViewModel.insertChromaProfile(chromaProfile)
            println("Restored Chroma Profile to DB:\n $chromaProfile")
        }
        notifyItemInserted(position)
    }

    fun deleteChromaProfile(position: Int){
        val focusChromaProfile = chromaProfileList[position]
        subscribeOnBackground {
            chromaProfileViewModel.deleteChromaProfile(focusChromaProfile)
            println("Deleted Chroma Profile from DB:\n $focusChromaProfile")
        }
        chromaProfileList.removeAt(position)
        notifyItemRemoved(position)
        val deletedPrompt = Snackbar.make(profileViewObject,"Deleted Chroma Profile | ${focusChromaProfile.alias.capitalize(
            Locale.ROOT
        )
        }", Snackbar.LENGTH_LONG)
        deletedPrompt.setAction("UNDO", View.OnClickListener {

            restoreChromaProfile(focusChromaProfile, position)

        })
        deletedPrompt.show()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chromaProfile = chromaProfileList[position]
        var hexCode1 = chromaProfile.hexCode1
        var hexCode2 = chromaProfile.hexCode2
        var hexCode3 = chromaProfile.hexCode3
        if (chromaProfile.hexCode1.isEmpty()){
            hexCode1 = "#FFD700"
        }
        if (chromaProfile.hexCode2.isEmpty()){
            hexCode2 = "#C0C0C0"
        }
        if (chromaProfile.hexCode3.isEmpty()){
            hexCode3 = "#A020F0"
        }
        holder.chromaHex1.setColorFilter(Color.parseColor(hexCode1))
        holder.chromaHex2.setColorFilter(Color.parseColor(hexCode2))
        holder.chromaHex3.setColorFilter(Color.parseColor(hexCode3))
        holder.chromaLabel.text = chromaProfile.alias.capitalize(Locale.ROOT)
        val holderText = "Custom"
        holder.chromaProfileItem.setOnClickListener {
            // SET TEM COLOR PROFILE ON WEAR DEVICE
            // SET ACTIVE CHROMA PROFILE TO ALIAS & HEX CODES
            profileViewObject.text = holderText
            profileViewObject.setTextColor(Color.parseColor("#0c204f"))
            hexPreview1.setColorFilter(Color.parseColor(hexCode1))
            hexPreview2.setColorFilter(Color.parseColor(hexCode2))
            hexPreview3.setColorFilter(Color.parseColor(hexCode3))
            hexPreviewCode1.text = hexCode1
            hexPreviewCode2.text = hexCode2
            hexPreviewCode3.text = hexCode3
            profileInit(chromaProfile)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val chromaLabel: TextView = itemView.findViewById(R.id.chroma_list_label)
        val chromaHex1: ImageView = itemView.findViewById(R.id.chroma_list_hex_1)
        val chromaHex2: ImageView = itemView.findViewById(R.id.chroma_list_hex_2)
        val chromaHex3: ImageView = itemView.findViewById(R.id.chroma_list_hex_3)
        val chromaProfileItem: MaterialCardView = itemView.findViewById(R.id.chromas_list_view)
    }


}