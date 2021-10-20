package com.example.lourinhamuseum.custom_view

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.lourinhamuseum.R

interface PapyrusLoading {

    companion object {
        const val START_DELAY = 800L
    }

    val papyrusImage: ImageView

    private fun context() = papyrusImage.context

    /**
     * Callback to identify the end of the show animation
     */
    private fun showAnimationCallback() = object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            onShowEnd()
        }
    }

    /**
     * Listener to setup the show animations
     * Musts register the end callback [showAnimationCallback],
     * Sets that the gif must reproduce only once
     */
    private fun showListener() = object : RequestListener<GifDrawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<GifDrawable>?,
            isFirstResource: Boolean
        ): Boolean {
            e?.printStackTrace()
            return false
        }

        override fun onResourceReady(
            resource: GifDrawable?,
            model: Any?,
            target: Target<GifDrawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            resource?.setLoopCount(1)
            papyrusImage.visibility = View.VISIBLE
            resource?.registerAnimationCallback(showAnimationCallback())
            return false
        }
    }

    /**
     * Callback to identify the end of the close animation
     */
    private fun hideAnimationCallback() = object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            onCloseEnd()
        }
    }

    /**
     * Listener to setup the hide animations
     * Must register the end callback [hideAnimationCallback],
     * Sets that the gif must reproduce only once
     */
    private fun hideListener() = object : RequestListener<GifDrawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<GifDrawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: GifDrawable?,
            model: Any?,
            target: Target<GifDrawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            resource?.setLoopCount(1)
            resource?.registerAnimationCallback(hideAnimationCallback())
            return false
        }

    }

    /**
     * Starts the animation of the [papyrusImage]
     */
    fun show() {
        onShowStart()
        Glide.with(context())
            .asGif()
            .addListener(showListener())
            .load(R.drawable.papiro)
            .into(papyrusImage)
    }

    /**
     * Starts the animation to hide the [papyrusImage]
     */
    fun hide() {
        onCloseStart()
        Glide.with(context())
            .asGif()
            .addListener(hideListener())
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.papiro_desenrolado)
            .skipMemoryCache(true)
            .load(R.drawable.papiro_rev)
            .into(papyrusImage)
    }


    /**
     * Defines the behaviour desired in the end of the show animation
     */
    fun onShowEnd()


    /**
     * Defines the behaviour desired in the start of the show animation
     */
    fun onShowStart()

    /**
     * Defines the behaviour desired in the end of the close animation
     */
    fun onCloseEnd()

    /**
     * Defines the behaviour desired in the start of the close animation
     */
    fun onCloseStart()

}