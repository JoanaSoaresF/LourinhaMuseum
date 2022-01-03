package com.example.lourinhamuseum.custom_view

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.example.lourinhamuseum.R


class LoadingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    RelativeLayout(context, attrs, defStyleAttr) {
    /* Tag utilizada para fazer debug. */
    private val TAG = "LoadingView"

    /* Layout utilizado para representar o LoadingView. */
    private var mRootView: View? = null

    /* ProgressBar utilizado para indicar que há alguma operação está a ser executada e é necessário esperar. */
    private var mProgressPB: ProgressBar? = null

    /* Contexto utilizado para obter os recursos da aplicação consoante o idioma configurado. */
    private var mResourcesContext: Context? = null

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet):  this(context, attrs, 0)

    init {
        mRootView = inflate(context, R.layout.view_loading, this@LoadingView)
        mProgressPB = findViewById(R.id.view_loading_progress_bar)
        setResourcesContext(context)
    }

    /**
     * Este método é utilizado para configurar o contexto dos recursos que são apresentados
     * no layout LoadingView.
     * O contexto dos recursos é, normalmente, actualizado quando há a alteração do idioma definido
     * para a aplicação.
     *
     * @param context Context. Contexto utilizado para obter os recursos da aplicação.
     */
    fun setResourcesContext(context: Context) {
        mResourcesContext = context
        updateLoadingView()
    }

    /**
     * Este método é utilizado para mostrar o layout LoadingView.
     *
     * @param animate boolean. Flag que indica se é para animar o aparecimento do layout LoadingView.
     */
    fun show(animate: Boolean) {
        if (animate) {
            mRootView!!.animate()
                .alpha(1.0f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        mRootView!!.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
        } else {
            mRootView!!.alpha = 1.0f
            mRootView!!.visibility = View.VISIBLE
        }
    }

    /**
     * Este método é utilizado para esconder o layout LoadingView.
     *
     * @param animate boolean. Flag que indica se é para animar o desaparecimento do layout LoadingView.
     */
    fun hide(animate: Boolean) {
        if (animate) {
            mRootView!!.animate()
                .alpha(0.0f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        mRootView!!.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
        } else {
            mRootView!!.alpha = 0.0f
            mRootView!!.visibility = View.GONE
        }
    }

    /**
     * Este método é utilizado para actualizar o layout LoadingView após a contexto dos recursos
     * da aplicação ser alterado, ou caso algum sistema de acessibilidade (ex: leitor de ecrã)
     * seja activado/desativado.
     */
    private fun updateLoadingView() {}

}