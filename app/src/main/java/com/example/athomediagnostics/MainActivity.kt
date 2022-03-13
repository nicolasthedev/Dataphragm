package com.example.athomediagnostics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_audio.*

import androidx.core.app.ActivityCompat
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.AsyncTask
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_audio.*

//import android.app.Activity; import android.content.Intent; import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;

import android.util.Log;
import com.example.athomediagnostics.fragments.AssessmentFragment
import com.example.athomediagnostics.fragments.AudioFragment
import com.example.athomediagnostics.fragments.DemographicsFragment
import com.example.athomediagnostics.fragments.adapters.ViewPagerAdapter

//
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import java.io.ByteArrayOutputStream
import java.util.*

open class MainActivity : AppCompatActivity() {

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sets tabs
        setupTabs()

    }

    private fun setupTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(AudioFragment(), "Audio")
        adapter.addFragment(DemographicsFragment(), "Demographics")
        adapter.addFragment(AssessmentFragment(), "Assessment")

        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_speaker_phone_24)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_people_24)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_assessment_24)

    }
}

