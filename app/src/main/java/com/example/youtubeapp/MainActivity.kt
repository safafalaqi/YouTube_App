package com.example.youtubeapp


import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker

class MainActivity : AppCompatActivity() {

    private lateinit var youTubeView: YouTubePlayerView
    private lateinit var ytplayer: YouTubePlayer
    private lateinit var myRv: RecyclerView
    private lateinit var rvAdapter: RVAdapter

    private val playlist: Array<Array<String>> = arrayOf(
        arrayOf("Week3 Day1", "lINActKAOLg"),
        arrayOf("Week3 Day2", "c6m9XkE4YVo"),
        arrayOf("Week3 Day3", "2bmWQPGYnrk"),
        arrayOf("Week3 Day4", "4a6TdwIb7H4"),
        arrayOf("Week3 Day5", "6CyV7QN-x3g"))
        var playedVideo=0
        var timeStamp= 0f
        var timestamplist= floatArrayOf(0f,0f,0f,0f,0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //set the toolbar
          supportActionBar?.setDisplayShowHomeEnabled(true)
          supportActionBar?.setIcon(R.drawable.icon)
          supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.custom_button))

        //-----Perform an internet connection check before trying to display a video-----
        checkInternetConnection()

        myRv = findViewById(R.id.rvVideo)
        youTubeView = findViewById(R.id.videoViewer)
        lifecycle.addObserver(youTubeView)
        //define the youtube player listener
        youTubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            //tracker
            val tracker = YouTubePlayerTracker()
            override fun onReady(youTubePlayer: YouTubePlayer) {
                ytplayer = youTubePlayer
                ytplayer.loadVideo(playlist[playedVideo][1], timestamplist[playedVideo])
                ytplayer.pause()
                rvAdapter= RVAdapter(playlist, ytplayer,timestamplist)
                myRv.adapter = rvAdapter
                myRv.layoutManager = LinearLayoutManager(this@MainActivity)
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer,state: PlayerConstants.PlayerState ) {
                super.onStateChange(youTubePlayer, state)
                //when video is paused save state so when we press the button video get where I stop last time
                if (state == PlayerConstants.PlayerState.PAUSED) {
                    ytplayer=youTubePlayer
                    ytplayer.addListener(tracker)
                    timeStamp = tracker.currentSecond
                    playedVideo = getindexById(tracker.videoId)
                    println("timestamp: ${timestamplist[playedVideo]}")
                    timestamplist[playedVideo] = timeStamp
                    println("playedVideo: $playedVideo")

                }
            }
        })
    }
     //to get the index number for the giving video id
     fun getindexById(vidId:String?):Int{
         var index=0
         for(i in playlist.indices)
         {
             if(playlist[i][1]==vidId)
                 index= i
         }
         return index
     }
    //to check if connected to the internet or not
    private fun connect():Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting ==true
    }
    //to check if connected to the internet and change the visibility based on this
    private fun checkInternetConnection() {
        //if connected it will change the layout to the main layout
        visibility( connect())
        //if not user will press the button to reconnect
        findViewById<Button>(R.id.btInternet).setOnClickListener {
            visibility( connect())
        }
    }
    //this function is to change the layouts visibility when connected
    private fun visibility(connect: Boolean) {
        if(connect) {
        findViewById<LinearLayout>(R.id.llMain).isVisible = true
        findViewById<LinearLayout>(R.id.llConnection).isVisible = false
        } }
    //---Save video id and time stamp to allow continuous play after device rotation---
    //to save the video timeStamp and the video id
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("playedVideo", playedVideo)
        outState.putFloatArray("timeStamp", timestamplist)
    }
    //to retrieve the video timeStamp and the video id
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        playedVideo = savedInstanceState.getInt("playedVideo", 0)

        timestamplist = savedInstanceState.getFloatArray("timeStamp",)!!
    }
    //to check the orientation and
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youTubeView.enterFullScreen()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            youTubeView.exitFullScreen()
        }
    }
}