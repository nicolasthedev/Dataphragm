package com.example.athomediagnostics.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.athomediagnostics.R
import com.example.athomediagnostics.Timer
import com.example.athomediagnostics.fragments.adapters.AudioListAdapter
import kotlinx.android.synthetic.main.fragment_audio.*
import java.io.File

//import kotlinx.android.synthetic.main.activity_main.*


class AudioFragment: Fragment(), Timer.OnTimerTickListener {

    private lateinit var amplitudes: ArrayList<Float>
    private lateinit var timer: Timer
    private lateinit var vibrator: Vibrator
    var mr = MediaRecorder()
    val mp = MediaPlayer()

    // test code for list of audio
    lateinit var rv: RecyclerView
    lateinit var adapter: AudioListAdapter
    var audioFileList = ArrayList<AudioModel>() // add to this list
    // end of test code

    //private lateinit var audios: ArrayList<AudioModel>



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val view: View = inflater.inflate(R.layout.fragment_audio, container, false)

        audioButtons(view)
        //audioListLayout(view)

        timer = Timer(this)
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Test code for list audio
        rv = view.findViewById(R.id.rv)
        rv.layoutManager = LinearLayoutManager(context)
        audioListLayout()

        // end of test code

        // Inflate the layout for this fragment
        //audioGraph(view)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    fun audioButtons(view: View){
        val path: String = context?.getExternalFilesDir("").toString() + "/myRecording.3gp"
        Log.i("findPathOfStorage", path)

        val btnAudioRecordOn = view.findViewById<ImageView>(R.id.btnAudioRecordOn)
        val btnAudioRecordOff = view.findViewById<ImageView>(R.id.btnAudioRecordOff)
        val btnPlayRecording = view.findViewById<ImageView>(R.id.btnPlayRecording)
        val btnStopRecording = view.findViewById<ImageView>(R.id.btnStopRecording)

        btnAudioRecordOn.isEnabled = false
        btnPlayRecording.isEnabled = false

        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 111)
        btnAudioRecordOn.isEnabled = true
        btnPlayRecording.isEnabled = true

        // Start Recording Audio
        btnAudioRecordOn.setOnClickListener{
            mr.setAudioSource(MediaRecorder.AudioSource.MIC)
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            // AMR: Adaptive Multi-Rate: Many modern mobile telephone handsets can store short audio recordings in the AMR format.
            mr.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            mr.setOutputFile(path)
            mr.prepare()
            mr.start()
            btnAudioRecordOff.isEnabled = true
            btnAudioRecordOn.isEnabled = false

            // Starts audio timer on screen
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            timer.start()

        }
        // Stop Recording Audio
        btnAudioRecordOff.setOnClickListener{
            mr.stop()
            btnAudioRecordOn.isEnabled = true
            btnAudioRecordOff.isEnabled = false

            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            timer.stop()
            amplitudes = waveFormView.clear()

        }

        btnPlayRecording.setOnClickListener{
            mp.setDataSource(path)
            mp.prepare()
            mp.start()
            btnStopRecording.isEnabled = true
            btnPlayRecording.isEnabled = false
        }

        btnStopRecording.setOnClickListener{
            mp.stop()
            btnPlayRecording.isEnabled = true
            btnStopRecording.isEnabled = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            btnAudioRecordOn.isEnabled = true

    }

    override fun onTimerTick(duration: String){
        tvTimer.text = duration

        waveFormView.addAmplitude(mr.maxAmplitude.toFloat())

    }

    fun audioListLayout(){

        // Test code for list audio

        val audioFiles: Array<out File>? = context?.getExternalFilesDirs("")
        audioFileList.clear()

        for (audioFile in audioFiles!!){
            audioFileList.add(AudioModel(audioFile.toString()))
            Log.i("stuffs", audioFile.path)
        }

        adapter = AudioListAdapter(requireContext(), audioFileList)
        rv.adapter = adapter

        // end of test code

    }

}