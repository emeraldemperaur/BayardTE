package iot.empiaurhouse.bayardte.view

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Slide
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.card.MaterialCardView
import iot.empiaurhouse.bayardte.R
import iot.empiaurhouse.bayardte.adapters.ChromaProfilesAdapter
import iot.empiaurhouse.bayardte.adapters.SwipeToDeleteCallback
import iot.empiaurhouse.bayardte.databinding.ActivityChromaProfileBinding
import iot.empiaurhouse.bayardte.model.ChromaProfile
import iot.empiaurhouse.bayardte.utils.UserPreferenceManager
import iot.empiaurhouse.bayardte.utils.subscribeOnBackground
import iot.empiaurhouse.bayardte.viewmodel.ChromaProfileViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates
import kotlin.random.Random


class ChromaProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChromaProfileBinding
    private lateinit var chromaProfileViewModel: ChromaProfileViewModel
    private lateinit var profileSwipeRefresh: SwipeRefreshLayout
    private var chromaProfilesFound = arrayListOf<ChromaProfile>()
    private var chromaProfilesRecyclerView: RecyclerView? = null
    private var chromaProfilesRVA: ChromaProfilesAdapter? = null
    private lateinit var userManager: UserPreferenceManager
    private lateinit var app: Application
    private lateinit var noResultsText: TextView
    private lateinit var pendingResultsText: TextView
    private lateinit var profileInfoText: TextView
    private lateinit var previewHex1: ImageView
    private lateinit var previewHexCard1: MaterialCardView
    private lateinit var previewHexText1: TextView
    private lateinit var previewHex2: ImageView
    private lateinit var previewHexCard2: MaterialCardView
    private lateinit var previewHexText2: TextView
    private lateinit var previewHexCard3: MaterialCardView
    private lateinit var previewHexText3: TextView
    private lateinit var previewHex3: ImageView
    private lateinit var pendingHexText1: TextView
    private lateinit var pendingHex1: ImageView
    private lateinit var pendingHexText2: TextView
    private lateinit var pendingHex2: ImageView
    private lateinit var pendingHexText3: TextView
    private lateinit var pendingHex3: ImageView
    private var mDefaultColor by Delegates.notNull<Int>()
    private val root: View? = null
    private var currentBackgroundColor by Delegates.notNull<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChromaProfileBinding.inflate(layoutInflater)
        chromaProfileViewModel = ViewModelProvider(this)[ChromaProfileViewModel::class.java]
        userManager = UserPreferenceManager(this)
        val viewSetup = binding.root
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Slide()
            enterTransition.duration = 769
        }
        setContentView(viewSetup)
        profileSwipeRefresh = binding.chromaProfileRefreshView
        profileSwipeRefresh.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.black, null))
        noResultsText = binding.chromaProfileNoResults
        pendingResultsText = binding.chromaProfileResultsPending
        previewHex1 = binding.chromaCustomPreview1
        previewHex2 = binding.chromaCustomPreview2
        previewHex3 = binding.chromaCustomPreview3
        previewHexCard1 = binding.chromaCustom1
        previewHexCard2 = binding.chromaCustom2
        previewHexCard3 = binding.chromaCustom3
        previewHexText1 = binding.chromaCustom1Hex
        previewHexText2 = binding.chromaCustom2Hex
        previewHexText3 = binding.chromaCustom3Hex
        pendingHex1 = binding.pendingHexcode1
        pendingHex2 = binding.pendingHexcode2
        pendingHex3 = binding.pendingHexcode3
        pendingHexText1 = binding.chromaProfile1Hex
        pendingHexText2 = binding.chromaProfile2Hex
        pendingHexText3 = binding.chromaProfile3Hex

        initView()
        initRefresh()
        initSwipeDeleteGesture()
    }


    override fun onStart() {
        super.onStart()
        //chromaProfileViewModel = ViewModelProvider(this)[ChromaProfileViewModel::class.java]


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
        binding.chromaProfileClose.setOnClickListener {
            finish()
        }
        binding.applyButton.setOnClickListener {
            buildChromaProfile()
        }
        app = this.application
        chromaProfileViewModel.processChromaProfiles(app)
        chromaProfilesRecyclerView = binding.chromaProfileRv
        profileInfoText = binding.chromaProfileInfo
        chromaProfilesRecyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        chromaProfilesFound = fetchChromaProfileDB()
        profileInfoText.text = userManager.getProfileInfo()
        pendingHex1.setColorFilter(Color.parseColor(userManager.getHexCode1()))
        pendingHex2.setColorFilter(Color.parseColor(userManager.getHexCode2()))
        pendingHex3.setColorFilter(Color.parseColor(userManager.getHexCode3()))
        pendingHexText1.text = userManager.getHexCode1()
        pendingHexText2.text = userManager.getHexCode2()
        pendingHexText3.text = userManager.getHexCode3()
        binding.chromaCustom1.setOnClickListener {
            initChromaPicker(this,Color.parseColor(previewHexText1.text.toString()), previewHex1, previewHexText1)
        }
        binding.chromaCustom2.setOnClickListener {
            initChromaPicker(this,Color.parseColor(previewHexText2.text.toString()), previewHex2, previewHexText2)
        }
        binding.chromaCustom3.setOnClickListener {
            initChromaPicker(this,Color.parseColor(previewHexText3.text.toString()), previewHex3, previewHexText3)
        }

    }

    private fun buildChromaProfile(){
        val date = LocalDate.now()
        val randomInt = Random.nextInt(1,69)
        val alias = date.format(DateTimeFormatter.ofPattern("dd::MM::yy:$randomInt"))
        val hexCode1 = previewHexText1.text.toString()
        val hexCode2 = previewHexText2.text.toString()
        val hexCode3 = previewHexText3.text.toString()
        val createdOnTimeStamp = date.toString()
        val stagedChromaProfile = ChromaProfile(alias = alias, hexCode1 = hexCode1, hexCode2 = hexCode2,
            hexCode3 = hexCode3,createdOnTimeStamp = createdOnTimeStamp)
        subscribeOnBackground {
            userManager.storeChromaData(hexCode1, hexCode2, hexCode3)
            chromaProfileViewModel.insertChromaProfile(stagedChromaProfile)
            println("staged Chroma Profile added to DB: $stagedChromaProfile")
        }
        pendingHex1.setColorFilter(Color.parseColor(hexCode1))
        pendingHex2.setColorFilter(Color.parseColor(hexCode2))
        pendingHex3.setColorFilter(Color.parseColor(hexCode3))
        pendingHexText1.text = hexCode1
        pendingHexText2.text = hexCode2
        pendingHexText3.text = hexCode3
        userManager.storeChromaProfile("Custom")
        profileInfoText.text = "Custom"
        fetchChromaProfileDB()
        viewRefresh()
    }

    private fun initChromaPicker(context: Context, currentColor: Int, hexPreview: ImageView, hexPreviewCode: TextView){
        var colorHex = "#000000"
        ColorPickerDialogBuilder.with(context).setTitle("Choose chroma").initialColor(currentColor)
            .setColorEditTextColor(Color.parseColor("#000000"))
            .showAlphaSlider(false)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(12)
            .setOnColorChangedListener { selectedColor ->
                colorHex = "#${Integer.toHexString(selectedColor).substring(2)}"
                println("Focus Chroma Hex: $colorHex")
                println("Chroma Changed: 0x" + Integer.toHexString(selectedColor))
            }
            .setOnColorSelectedListener { selectedColor ->
                colorHex = "#${Integer.toHexString(selectedColor).substring(2)}"
                println("Chroma Hex Selected: $colorHex")
                println("Chroma Selected: 0x" + Integer.toHexString(selectedColor))
            }
            .setPositiveButton("ok")
            { dialog, selectedColor, allColors -> changeBackgroundColor(selectedColor, hexPreview, hexPreviewCode, colorHex)
                colorHex = "#${Integer.toHexString(selectedColor).substring(2)}"
                println("Chroma Hex Applied: $colorHex")
                println("Chroma Applied: 0x" + Integer.toHexString(selectedColor))
            }
            .setNegativeButton("cancel") { dialog, which -> }
            .showColorEdit(true)
            .build()
            .show()
    }

    private fun changeBackgroundColor(selectedColor: Int, hexPreview: ImageView, hexPreviewCode: TextView, hexCode: kotlin.String) {
        currentBackgroundColor = selectedColor
        hexPreview.setColorFilter(selectedColor)
        hexPreviewCode.text = hexCode.uppercase()
    }

    private fun fetchChromaProfileDB(): ArrayList<ChromaProfile> {
        var result: Boolean
        val fetchedChromaProfiles = arrayListOf<ChromaProfile>()

        chromaProfileViewModel.fetchChromaProfileList()
        chromaProfileViewModel.chromaProfiles.observe(
            this,
            androidx.lifecycle.Observer { reply ->
                reply?.let {
                    result = reply.isNotEmpty()
                    if (fetchedChromaProfiles.isEmpty()) {
                        fetchedChromaProfiles.addAll(reply)
                        chromaProfilesFound = fetchedChromaProfiles
                        println("Chroma Profiles response object is not empty: $result")
                        println("See Chroma Profiles response result: $reply")
                    }
                }
            })
        return fetchedChromaProfiles
    }

    private fun viewRefresh(){
        chromaProfilesRecyclerView?.adapter = null
        chromaProfilesRVA = null
        chromaProfilesRecyclerView = null
        Handler(Looper.getMainLooper()).postDelayed({
            chromaProfilesFound = fetchChromaProfileDB()
            chromaProfilesRecyclerView = binding.chromaProfileRv
            chromaProfilesRVA = ChromaProfilesAdapter(chromaProfilesFound, binding.chromaProfileInfo,
                this, app, binding.pendingHexcode1, binding.chromaProfile1Hex, binding.pendingHexcode2,
                binding.chromaProfile2Hex, binding.pendingHexcode3, binding.chromaProfile3Hex)
            chromaProfilesRecyclerView!!.adapter = chromaProfilesRVA
            noResultsView(chromaProfilesFound.size)
        }, 444)
        initSwipeDeleteGesture()
    }

    private fun profileRefresh(){
        profileInfoText.text = getString(R.string.monochrome)
        userManager.storeChromaProfile(getString(R.string.monochrome))
        userManager.storeChromaData("#000000", "#FFFFFF", "#000000")
        pendingResultsText.visibility = View.VISIBLE
        pendingHex1.setColorFilter(Color.parseColor("#000000"))
        pendingHex2.setColorFilter(Color.parseColor("#FFFFFF"))
        pendingHex3.setColorFilter(Color.parseColor("#000000"))
        pendingHexText1.text = "#000000"
        pendingHexText2.text = "#FFFFFF"
        pendingHexText3.text = "#000000"
    }

    private fun initSwipeDeleteGesture(){
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = chromaProfilesRVA
                adapter!!.deleteChromaProfile(viewHolder.adapterPosition)
                noResultsView(chromaProfilesFound.size)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(chromaProfilesRecyclerView)

    }

    private fun initRefresh(){
        profileSwipeRefresh.setOnRefreshListener {
            profileRefresh()
            viewRefresh()
            profileSwipeRefresh.isRefreshing = false
        }
    }

    private fun noResultsView(recordsFound: Int){
        if (recordsFound < 1){
            if (chromaProfilesRecyclerView != null) {
                chromaProfilesRecyclerView!!.visibility = View.GONE
            }
            pendingResultsText.visibility = View.GONE
            noResultsText.visibility = View.VISIBLE
        }
        else if (recordsFound > 0){
            noResultsText.visibility = View.GONE
            if (chromaProfilesRecyclerView != null) {
                chromaProfilesRecyclerView!!.visibility = View.VISIBLE
                pendingResultsText.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewRefresh()
    }

    override fun onBackPressed()
    {
        moveTaskToBack(true)
    }

}