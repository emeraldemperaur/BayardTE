package iot.empiaurhouse.bayardte.view

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.google.android.material.snackbar.Snackbar
import iot.empiaurhouse.bayardte.R
import iot.empiaurhouse.bayardte.databinding.SettingsActivityBinding
import iot.empiaurhouse.bayardte.utils.UserPreferenceManager
import iot.empiaurhouse.bayardte.utils.subscribeOnBackground
import iot.empiaurhouse.bayardte.viewmodel.ChromaProfileViewModel
import iot.empiaurhouse.bayardte.viewmodel.TEMarkerViewModel

class SettingsPreferencesActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding
    private lateinit var userManager: UserPreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        userManager = UserPreferenceManager(this)
        val viewSetup = binding.root
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Slide()
            enterTransition.duration = 769
        }
        setContentView(viewSetup)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        initView()
    }

    private fun initView() {
        binding.settingsClose.setOnClickListener {
            finish()
        }

        binding.settingsExitButton.setOnClickListener {
            finish()
        }

    }


    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var userManager: UserPreferenceManager
        private lateinit var teMarkersViewModel: TEMarkerViewModel
        private lateinit var chromaProfilesViewModel: ChromaProfileViewModel
        private lateinit var bulkDeleteTEMarkers: Preference
        private lateinit var bulkDeleteChromaProfiles: Preference
        private lateinit var findMEBayard: Preference
        private lateinit var r1Velocity: SeekBarPreference
        private lateinit var r2Velocity: SeekBarPreference
        private lateinit var r3Velocity: SeekBarPreference
        private lateinit var app: Application


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            userManager = UserPreferenceManager(requireContext())
            teMarkersViewModel = ViewModelProvider(this)[TEMarkerViewModel::class.java]
            chromaProfilesViewModel = ViewModelProvider(this)[ChromaProfileViewModel::class.java]
            app = requireActivity().application
            setPreferencesFromResource(R.xml.bte_preferences, rootKey)
            bulkDeleteTEMarkers = findPreference("bulkDeleteTEMarkers")!!
            bulkDeleteTEMarkers.setOnPreferenceClickListener {
                teMarkersViewModel.processTEMarkers(app)
                deleteDBSnackBar(requireView(), "TE Markers")
                subscribeOnBackground {
                    teMarkersViewModel.killTEMarkerDB()
                }
                true
            }
            bulkDeleteChromaProfiles = findPreference("bulkDeleteChromaProfiles")!!
            bulkDeleteChromaProfiles.setOnPreferenceClickListener {
                chromaProfilesViewModel.processChromaProfiles(app)
                deleteDBSnackBar(requireView(), "Chroma Profiles")
                subscribeOnBackground {
                    chromaProfilesViewModel.killChromaProfileDB()
                    userManager.storeChromaData("#000000", "#FFFFFF", "#000000")
                    userManager.storeChromaProfile(getString(R.string.monochrome))
                }
                true
            }
            findMEBayard = findPreference("sosBayard")!!
            findMEBayard.setOnPreferenceClickListener {
                findMEBayard(requireView())
                true
            }
            r1Velocity = findPreference("r1Velocity")!!
            if (userManager.getR1Velocity() != 0){
                r1Velocity.value = userManager.getR1Velocity()
            }
            r1Velocity.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    userManager.storeRing1VelocityData(newValue as Int)
                    true
                }
            r2Velocity = findPreference("r2Velocity")!!
            if (userManager.getR2Velocity() != 0){
                r2Velocity.value = userManager.getR2Velocity()
            }
            r2Velocity.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue2 ->
                    userManager.storeRing2VelocityData(newValue2 as Int)
                    true
                }
            r3Velocity = findPreference("r3Velocity")!!
            if (userManager.getR2Velocity() != 0){
                r3Velocity.value = userManager.getR3Velocity()
            }
            r3Velocity.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue3 ->
                    userManager.storeRing3VelocityData(newValue3 as Int)
                    true
                }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun deleteDBSnackBar(view: View, title: String){
            val deleteNote = Snackbar.make(view,"Bulk deleted $title database successfully!", Snackbar.LENGTH_SHORT)
            val deleteNoteView = deleteNote.view
            deleteNoteView.layoutParams
            val deleteNoteText = deleteNoteView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            deleteNoteText.setBackgroundColor(requireActivity().getColor(R.color.black))
            deleteNoteText.setTextColor(requireActivity().getColor(R.color.white))
            val fontFace = resources.getFont(R.font.montserratlight)
            deleteNoteText.typeface = fontFace
            deleteNoteText.maxLines = 4
            deleteNote.show()
        }

        private fun findMEBayard(view: View){
            val sosNote = Snackbar.make(view,"SOS sent to Bayard successfully!", Snackbar.LENGTH_SHORT)
            val sosNoteView = sosNote.view
            sosNoteView.layoutParams
            val sosNoteText = sosNoteView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            sosNoteText.setBackgroundColor(requireActivity().getColor(R.color.black))
            sosNoteText.setTextColor(requireActivity().getColor(R.color.white))
            val fontFace = resources.getFont(R.font.montserratlight)
            sosNoteText.typeface = fontFace
            sosNoteText.maxLines = 4
            sosNote.anchorView = view.rootView.findViewById(R.id.settings_exit_button)
            sosNote.show()
        }

    }
}