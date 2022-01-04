package com.example.lourinhamuseum.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.custom_view.PapyrusLoading.Companion.START_DELAY


class PopupInfoView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr), PapyrusLoading {


    private var controller: PopupInfoController? = null
    var mRootView: View = inflate(context, R.layout.popup_info, this@PopupInfoView)
    val popupTitle: TextView = findViewById(R.id.popup_title)
    val exitButton: ImageView = findViewById(R.id.popup_exit_button)
    val text: TextView = findViewById(R.id.popup_text)
    val textScroll: ScrollView = findViewById(R.id.text_scroll)
    override val papyrusImage: ImageView = findViewById(R.id.papiro_backgroud)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)


    private fun loadingBackground() {
        visibility = View.VISIBLE
        textScroll.animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        textScroll.animation.startOffset = START_DELAY
        textScroll.animate().start()
        exitButton.visibility = View.INVISIBLE
        popupTitle.visibility = View.INVISIBLE

    }


    fun setPopupController(popupInfoController: PopupInfoController) {
        controller = popupInfoController
        exitButton.setOnClickListener { hide() }

    }

    override fun onShowEnd() {
        exitButton.visibility = View.VISIBLE
        popupTitle.visibility = View.VISIBLE
        popupTitle.text = controller?.popupTitle
    }

    override fun onShowStart() {
        loadingBackground()
        text.text = controller?.popupText
    }

    override fun onCloseEnd() {
        visibility = View.GONE
        textScroll.visibility = View.INVISIBLE
        papyrusImage.visibility = View.INVISIBLE
        popupTitle.visibility = View.INVISIBLE
        controller?.onClosePopup()
    }

    override fun onCloseStart() {
        popupTitle.visibility = View.INVISIBLE
        exitButton.visibility = View.INVISIBLE
        textScroll.animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        textScroll.animate().start()
    }


}

@BindingAdapter("popupShow")
fun bindHelpPopup(popup: PopupInfoView, show: Boolean) {
    if (show) {
        popup.show()
    }
}
