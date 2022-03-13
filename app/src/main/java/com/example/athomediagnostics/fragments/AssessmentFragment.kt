package com.example.athomediagnostics.fragments

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.athomediagnostics.MainActivity
import com.example.athomediagnostics.R
import com.google.android.material.navigation.NavigationView
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import java.io.*
import java.lang.NullPointerException
import java.lang.RuntimeException
import java.util.*
import java.util.Collections.min
import kotlin.collections.ArrayList

class AssessmentFragment : Fragment() {
    lateinit var inputStream: InputStream
    lateinit var submitBtn: Button
    var femaleProbability: Double = 0.0
    var maleProbability: Double = 0.0
    var under65AgeProbability: Double = 0.0
    var over65AgeProbability: Double = 0.0
    var nativeProbability: Double = 0.12
    var asianProbability: Double = 0.000855
    var blackProbability: Double = 0.00208
    var islanderProbability: Double = 0.000855
    var whiteProbability: Double = 0.001689

    var userPrbability: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_assessment, container, false)
        inputStream = resources.openRawResource(R.raw.heart)
        val demographicMatrix: ArrayList<List<*>> = read()

        setProbabilities(demographicMatrix)

        updateResults(view)

        sshFunctionality(view)

        // Inflate the layout for this fragment
        return view
    }

    fun updateResults(view: View) {

        submitBtn = view.findViewById<Button>(R.id.assessmentButton)

        submitBtn.setOnClickListener(object : View.OnClickListener {
            @SuppressLint("SdCardPath")
            override fun onClick(v: View?) {
                val inputStream: InputStream = File("/data/user/0/com.example.athomediagnostics/files/LET/demographic_data.txt").inputStream()
                val reader = inputStream.bufferedReader()
                val sexData: String = reader.readLine()
                val raceData: String = reader.readLine()
                val ageData: Int = reader.readLine().toInt()
                reader.close()

                assessDemographicDiseaseRate(view, ageData, sexData, raceData)
            }

        })

    }

    fun assessDemographicDiseaseRate(view:View, age: Int, sex: String, race: String){
        var prob: Double = 0.0
        if (age >= 65) {
            prob += over65AgeProbability
        }
        else{
            prob += under65AgeProbability
        }
        if (sex == "male"){
            prob += maleProbability
        }
        else{
            prob += femaleProbability
        }

        when(race){
            "AmericanIndianOrAlaskaNative" -> prob += nativeProbability
            "Asian" -> prob += asianProbability
            "Black or African American" -> prob += blackProbability
            "Native Hawaiian or Other Pacific Islander" -> prob += islanderProbability
            "White" -> prob += whiteProbability
        }
        userPrbability = (prob/3)*100

        val assessmentText: TextView = view.findViewById(R.id.assessmentResults)
        assessmentText.text = userPrbability.toString()



    }




    fun read(): ArrayList<List<*>> {
        val resultList: ArrayList<List<*>> = ArrayList()
        val reader = BufferedReader( InputStreamReader(inputStream))

        try {
            var csvLine: String  = reader.readLine()
            while (true) {
                val row = csvLine.split(",")
                resultList.add(row)
                try{
                    csvLine = reader.readLine()
                }
                catch(e: NullPointerException){
                    break;
                }
            }
        } catch (ex: IOException) {
            throw RuntimeException("Error in reading CSV file: $ex")
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                throw RuntimeException("Error while closing input stream: $e")
            }
        }
        return resultList
    }
    fun setProbabilities(list: ArrayList<List<*>>){
        var sex: Int = 1
        var male: Int = 1
        var female: Int = 0
        var age: Int = 0
        var over65: Int = 1
        var under65: Int = 0
        maleProbability = findProbabilities(list, sex, male)
        femaleProbability = findProbabilities(list, sex, female)
        under65AgeProbability = findProbabilities(list, age, over65)
        over65AgeProbability = findProbabilities(list, age, under65)

    }

    fun findProbabilities(list: ArrayList<List<*>>, column: Int, param: Int): Double{
        var total: Double = 0.0
        var probability: Double = 0.0
        var ignoreFirstRow: Int = 0
        var totalItemsCounted: Int = 0
        val target: Int = 13

        for ( item in list){
            if (ignoreFirstRow != 0) {
                var value: Int = item[column].toString().toInt()
                if ( column == 0 && param == 1 && value >= 65){
                    total += item[target].toString().toInt()
                }
                else if (column == 0 && param == 0 && value < 65){
                    total += item[target].toString().toInt()
                }
                if (column == 1 && param == value){
                    total += item[target].toString().toInt()
                }
                else if (column == 1 && param == value){
                    total += item[target].toString().toInt()
                }
            }
            else{
                ignoreFirstRow++
            }

            totalItemsCounted++
        }

        probability = total/totalItemsCounted
        return probability
    }

    // ssh test code
    class SshTask: AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {

            //val stuff = .findViewById<EditText>(R.id)
            val output = executeRemoteCommand(params[0]!!,"root", "password", "localhost", 2380)
            Log.i("sshtag", output)
            return output
        }


    }
    private fun sshFunctionality(view: View) {
        val sshbtn = view.findViewById<Button>(R.id.sshbutton)
        val sshcmd = view.findViewById<EditText>(R.id.sshinput)
        val sshout = view.findViewById<TextView>(R.id.sshoutput)

        sshbtn.setOnClickListener{
            val sshInput = sshcmd.text.toString()
            // ssh test code
            val out = SshTask().execute(sshInput)
            sshout.text = out.get().toString()
            Log.i("sshtag", out.get().toString())
        }

    }
}
fun executeRemoteCommand(command: String, username: String,
                         password: String,
                         hostname: String,
                         port: Int = 22): String {
    val jsch = JSch()
    val session = jsch.getSession(username, hostname, port)
    session.setPassword(password)



    // Avoid asking for key confirmation.
    val properties = Properties()
    properties.put("StrictHostKeyChecking", "no")
    session.setConfig(properties)

    session.connect()

    // Create SSH Channel.
    val sshChannel = session.openChannel("exec") as ChannelExec
    val outputStream = ByteArrayOutputStream()
    sshChannel.outputStream = outputStream

    // Execute command.
    sshChannel.setCommand(command)
    sshChannel.connect()

    // Sleep needed in order to wait long enough to get result back.
    Thread.sleep(1_000)
    sshChannel.disconnect()

    session.disconnect()

    return outputStream.toString()
}