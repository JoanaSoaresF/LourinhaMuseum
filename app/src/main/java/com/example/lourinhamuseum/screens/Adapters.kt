package com.example.lourinhamuseum.screens

import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.data.domain.Point
import com.example.lourinhamuseum.screens.allCards.CardGridAdapter
import com.example.lourinhamuseum.screens.allCards.DataItem
import com.example.lourinhamuseum.screens.vuforia.VuforiaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

@BindingAdapter("cardImage")
fun bindCardImage(imgView: ImageView, point: Point?) {
    point?.let {
        val context = imgView.context
        val name = if (point.isFound) point.imageFound else point.imageNotFound
        val fileUrl = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), name
        )
        Glide.with(context)
            .load(fileUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image)
            )
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imgView)
    }
}

@BindingAdapter("listAllCards")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<DataItem>?) {
    val adapter = recyclerView.adapter as CardGridAdapter
    GlobalScope.launch(Dispatchers.Default) {
        adapter.submitList(data)
    }
}

@BindingAdapter("changeButton")
fun bindDetectionButton(button: Button, found: VuforiaViewModel.DetectionState) {
    val ctx = button.context
    val text: String
    if (found != VuforiaViewModel.DetectionState.FOUND) {
        text = ctx.getString(R.string.searching)
//        button.background = AppCompatResources.getDrawable(ctx, R.drawable.load_button_grey)
        ViewCompat.setBackgroundTintList(
            button, ContextCompat.getColorStateList(
                ctx, R.color
                    .loading_color
            )
        )
    } else {
        text = ctx.getString(R.string.found)
//        button.background = AppCompatResources.getDrawable(ctx,
//            R.drawable.cards_background
//        )
        ViewCompat.setBackgroundTintList(
            button, ContextCompat.getColorStateList(
                ctx, R.color
                    .color_button_play
            )
        )
    }
    button.text = text

}

@BindingAdapter("setImage")
fun bindOutlineImage(imgView: ImageView, image: String?) {
    image?.let {
        val context = imgView.context
        val fileUrl = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), image
        )
        Glide.with(context)
            .load(fileUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imgView)
    }
}
