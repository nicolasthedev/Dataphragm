package com.example.athomediagnostics.fragments

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.athomediagnostics.R
import kotlinx.android.synthetic.main.fragment_demographics.*
import kotlinx.android.synthetic.main.fragment_demographics.view.*
import android.util.Log
import java.io.File
import android.widget.Toast

import android.widget.RadioButton
import android.widget.RadioGroup

class DemographicsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var radioGroup: RadioGroup
    lateinit var radioButton: RadioButton
    lateinit var submitBtn: Button
    lateinit var spinner: Spinner
    lateinit var age: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_demographics, container, false)

        submitDemographicData(view)


        raceDropDownMenue(view)

        return view
    }
    fun raceDropDownMenue(view: View){
        val raceDropDown: Spinner = view.findViewById(R.id.raceAnswer)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(), R.array.RaceSpinner, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        raceDropDown.adapter = adapter
        raceDropDown.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = parent?.getItemAtPosition(position).toString();
        Toast.makeText(parent?.context, text, Toast.LENGTH_SHORT).show()

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    fun submitDemographicData(view: View){

        radioGroup = view.findViewById<RadioGroup>(R.id.sexRadioGroup)
        submitBtn = view.findViewById<Button>(R.id.demographicsSubmitButton)
        spinner = view.findViewById<Spinner>(R.id.raceAnswer)
        age = view.findViewById<EditText>(R.id.ageAnswer)


        // create a new file
        val path = context?.getFilesDir()
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()

        val file = File(letDirectory, "demographic_data.txt")


        submitBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                // get selected radio button from radioGroup
                val selectedId = radioGroup.checkedRadioButtonId

                // find the radiobutton by returned id
                radioButton = view.findViewById(selectedId) as RadioButton
                val radioData: String = radioButton.getText() as String
                val spinnerData: String = spinner.selectedItem as String
                val ageData = age.text.toString()

                file.writeText(radioData)
                file.appendText("\n")
                file.appendText(spinnerData)
                file.appendText("\n")
                file.appendText(ageData)

            }
        })

    }
}