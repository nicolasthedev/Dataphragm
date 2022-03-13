package com.example.athomediagnostics.fragments.adapters

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.athomediagnostics.R
import com.example.athomediagnostics.fragments.AudioModel

class AudioListAdapter(val requireContext: Context, val audioList: ArrayList<AudioModel>) : RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    lateinit var mp: MediaPlayer

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tvTitle)
        val btnplay = itemView.findViewById<TextView>(R.id.btnPlay)
        val icon = itemView.findViewById<ImageView>(R.id.icon)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(requireContext).inflate(R.layout.list_audio, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val audio = audioList[position]
        val no = position+1
        holder.title.setText("Audio " + no)
        holder.btnplay.setOnClickListener {
            val audioPath = audio.audioFilePath

            if (holder.btnplay.text.equals("Play")) {
                playAudio(audioPath)
                holder.btnplay.text = "Pause"
                holder.icon.setBackgroundResource(R.drawable.ic_baseline_stop_circle_24)
            } else {
                // If music is playing this line will stop it
                mp.stop()
                mp.reset()
                mp.release()
                //Toast is used to show that music has paused
                Toast.makeText(requireContext, "Audio has been paused", Toast.LENGTH_SHORT)
                    .show()
                holder.btnplay.text = "Play"
                holder.icon.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24)
            }
        }
    }
    override fun getItemCount() = audioList.size
    private fun playAudio(audioPath: String) {
        Log.i("playaudio", audioPath)

        mp = MediaPlayer()
        mp.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        try {
            // below line is use to set our url to our media player.
            mp.setDataSource(audioPath)
            // below line is use to prepare
            // and start our media player.
            mp.prepare()
            mp.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        // below line is use to display a toast message.
        Toast.makeText(requireContext, "Audio started playing..", Toast.LENGTH_SHORT).show()
    }
}

