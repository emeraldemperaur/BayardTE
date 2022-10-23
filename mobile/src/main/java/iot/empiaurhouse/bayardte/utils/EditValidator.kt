package iot.empiaurhouse.bayardte.utils

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import iot.empiaurhouse.bayardte.R
import iot.empiaurhouse.bayardte.model.TEMarker

class EditValidator {

    private lateinit var viewContext: View

    fun initValidator(context: View){
        viewContext = context
    }

    fun isValidText(inputField: TextInputEditText, inputFieldLayout: TextInputLayout): Boolean{
        var isValid = false
        if (inputField.text.isNullOrBlank()){
            isValid = false
            inputFieldLayout.error = "Missing Alias"
            inputFieldLayout.boxStrokeColor = Color.parseColor("#800020")
            inputFieldLayout.isFocusable = true
            inputFieldLayout.isFocusable = false
            val missingFieldPrompt = Snackbar.make(viewContext,"Please provide a valid alias", Snackbar.LENGTH_LONG)
            missingFieldPrompt.setBackgroundTint(Color.parseColor("#800020"))
            missingFieldPrompt.anchorView = viewContext.rootView.findViewById(R.id.edit_submit_button)
            missingFieldPrompt.show()

        }
        else if (!inputField.text.isNullOrBlank()){
            isValid = true
            inputFieldLayout.error = null
            inputFieldLayout.boxStrokeColor = Color.parseColor("#0c204f")

        }
        return isValid
    }

    fun inputUnique(teAlias: String, teMarkers: ArrayList<TEMarker>, inputFieldLayout: TextInputLayout): Boolean{
        var isUnique = true
        for (teMarker in teMarkers){
            if (teMarker.alias.lowercase().trim() == teAlias.lowercase().trim()){
                isUnique = false
                inputFieldLayout.error = "The alias '${teMarker.alias}' already exists in database"
                inputFieldLayout.boxStrokeColor = Color.parseColor("#800020")
                inputFieldLayout.isFocusable = true
                inputFieldLayout.isFocusable = false
                val missingFieldPrompt = Snackbar.make(viewContext,"'${teMarker.alias.capitalize().trim()}' already exists. " +
                        "Please try a unique alias", Snackbar.LENGTH_LONG)
                missingFieldPrompt.setBackgroundTint(Color.parseColor("#800020"))
                missingFieldPrompt.anchorView = viewContext.rootView.findViewById(R.id.edit_submit_button)
                missingFieldPrompt.show()
            }
        }
        return isUnique
    }

    fun hexCleaner(hexCode: String): String{
        var hexFound = hexCode
        if (hexCode.length == 4){
            val hex6 = "#${hexFound[1]}${hexFound[1]}${hexFound[2]}${hexFound[2]}${hexFound[3]}${hexFound[3]}"
            hexFound = hex6
        }
        return  hexFound
    }

}