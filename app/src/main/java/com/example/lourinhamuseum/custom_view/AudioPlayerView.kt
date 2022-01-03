package com.example.lourinhamuseum.custom_view


import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.data.domain.Slideshow
import com.example.lourinhamuseum.utils.convertTime
import timber.log.Timber
import java.io.File


class AudioPlayerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr), MediaPlayer.OnCompletionListener,
    SeekBar.OnSeekBarChangeListener,
    SurfaceHolder.Callback, LifecycleObserver {

    private var controller: AudioPlayerController? = null

    private var isPlaying: Boolean = false
    private var slideshow: String? = null
    private var nextSlideshowIndex: Int = 0
    private var nextSlideshow: Slideshow? = null

    //To handle the progress of the seek bar and the update of currentTime
    private var mHandler = Handler(Looper.myLooper()!!)
    private var mRunnable: Runnable? = null
    private var progressTime = 0

    var mRootView: View = inflate(context, R.layout.audio_player, this)
    val imageSlideshow: ImageView = findViewById(R.id.slideshow_image)
    val video: VideoView = findViewById(R.id.video_view)
    val playButton: ImageView = findViewById(R.id.play_button)
    val duration: TextView = findViewById(R.id.duration)
    val currentTime: TextView = findViewById(R.id.current_time)
    val seekbar: SeekBar = findViewById(R.id.seekBar)


    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    fun setupAudioController(audioPlayerController: AudioPlayerController) {
        controller = audioPlayerController
        val durationTime = audioPlayerController.player.getDuration()
        duration.text = convertTime(durationTime)
        seekbar.max = durationTime
        currentTime.text = convertTime(0)
        playButton.setOnClickListener {
            onPlayButtonClicked()
        }
        if (audioPlayerController.isVideo) {
            Timber.d("View is video")
            imageSlideshow.visibility = View.GONE
        } else {
            video.visibility = View.GONE
            setupSlideshow()
        }

    }

    private fun setupSlideshow() {
        controller?.point?.let {
            slideshow = if (controller!!.isVideo) it.video else (it.slideshow[0].url)
            setSlideshowImage(slideshow!!)
            if (!controller!!.isVideo && it.slideshow.size > 1) {
                nextSlideshowIndex = 1
                nextSlideshow = it.slideshow[nextSlideshowIndex]
            }
        }
    }

    private fun onPlayButtonClicked() {
        controller?.let {
            it.onPlayButtonClicked()
            if (isPlaying) {
                pauseAudio()
            } else {
                playAudio()
            }
            setButton()
        }
    }

    private fun setButton() {
        val button = if (isPlaying)
            AppCompatResources.getDrawable(context, R.drawable.pause)
        else AppCompatResources.getDrawable(context, R.drawable.play)
        playButton.setImageDrawable(button)
    }


    private fun playAudio() {
        if (!isPlaying) {
            isPlaying = true
            /**
             * Updates need to be perform when audio is playing:
             * update the [currentTime], update the [slideshow] and check id the audio is finish
             */
            mRunnable = object : Runnable {
                override fun run() {
                    controller?.let { controller ->
                        // Update the progress of seekBar and the display of currentTime
                        val timePlayer = controller.player.getCurrentTime()
                        Timber.d("Runnable $timePlayer")
                        setTime(timePlayer)
                        //update the slideshow image
                        updateSlideshow(timePlayer)
                        // Schedule the task to repeat after 1 second
                        mHandler.postDelayed(this, 1000)
                    }
                }
            }
            mHandler.post(mRunnable!!)
            setButton()
            Timber.d("Play audio ${controller?.player == null}")
            controller?.player?.play()
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pauseAudio() {
        if (isPlaying) {
            isPlaying = false
            setButton()
            cancelRunnable()
            controller?.player?.pause()
        }
    }

    private fun setTime(time: Int) {
        currentTime.text = convertTime(time)
        seekbar.progress = time
    }

    private fun updateSlideshow(time: Int) {
        controller?.let { controller ->
            if (!controller.isVideo) {
                val point = controller.point
                if (nextSlideshow?.time == time / 1000) {

                    nextSlideshow?.url?.let { setSlideshowImage(it) }
                    //update next slideshow
                    nextSlideshowIndex =
                        if (nextSlideshowIndex + 1 < point.slideshow.size)
                            nextSlideshowIndex + 1
                        else 0

                    nextSlideshow = point.slideshow[nextSlideshowIndex]
                }
            }
        }
    }

    private fun setSlideshowImage(image: String) {
        val fileUrl = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), image
        )
        Glide.with(context)
            .load(fileUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageSlideshow)
    }

    /**
     * Cancels the runnable responsible for tht time and slideshow updates.
     * The runnable only need to be running when the audio is playing
     */
    private fun cancelRunnable() {
        mRunnable?.let {
            mHandler.removeCallbacks(it)
        }
        mRunnable = null
    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            moveSeekBar(false, progress)
            progressTime = progress
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        progressTime = 0
        pauseAudio()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // write custom code for progress is stopped
        moveSeekBar(true, progressTime)
        playAudio()
    }

    /**
     * Updates the [currentTime] due to user changes in the seekbar
     * @param setPlayer if the move stopped the audio will be set to the current time
     * @param pos position to alter the [currentTime]
     */
    private fun moveSeekBar(setPlayer: Boolean, pos: Int) {
        controller?.let {
            if (setPlayer) {
                it.player.seekTo(pos)
            }
            currentTime.text = convertTime(pos)
            if (!it.isVideo) {
                val point = it.point
                //Update slideshow image
                for (i in 0..point.slideshow.size - 2) {
                    val image = point.slideshow[i]
                    val image2 = point.slideshow[i + 1]
                    val time = pos / 1000
                    if (image.time <= time && time <= image2.time) {
                        setSlideshowImage(image.url)
                        nextSlideshow = image2
                        nextSlideshowIndex = i + 1
                        break
                    } else if (i == point.slideshow.size - 2 && time >= image2.time) {
                        setSlideshowImage(image2.url)
                        nextSlideshow = point.slideshow[0]
                        nextSlideshowIndex = 0
                    }
                }
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        controller?.player?.setDisplay(holder)
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Timber.d("On audio complete")
        //get to the beginning
        controller?.let {
            it.player.seekTo(0)
            it.player.pause()
            val point = it.point
            setSlideshowImage(point.slideshow[0].url)
            nextSlideshowIndex = 1
            nextSlideshow = point.slideshow[nextSlideshowIndex]
            it.onAudioComplete()
            cancelRunnable()
        }
    }
}

@BindingAdapter("isPlaying")
fun bindStopPlaying(view: AudioPlayerView, isPlaying: Boolean) {
    if (!isPlaying) {
        view.pauseAudio()
    }
}
