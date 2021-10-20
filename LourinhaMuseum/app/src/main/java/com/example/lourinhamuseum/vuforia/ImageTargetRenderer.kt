package com.example.lourinhamuseum.vuforia

import android.opengl.GLSurfaceView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ImageTargetRenderer(private val vuforiaARSession: VuforiaARSession) :
    GLSurfaceView.Renderer {

    /* Largura da imagem apresentada. */
    private var mWidth = 0

    /* Altura da imagem apresentada. */
    private var mHeight = 0

    /* Flag que indica se é necessário fazer as configurações no Vuforia. */
    private var mSurfaceChanged = false

    /**
     * Última imagem detetada na sessão
     */
    private val _lastImageDetected = MutableLiveData<Int?>(null)
    val lastImageDetected: LiveData<Int?>
        get() = _lastImageDetected


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Timber.d("onSurfaceCreated")
        vuforiaARSession.initVuforiaARRendering()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Timber.d("onSurfaceChanged")
        mHeight = height
        mWidth = width
        // configurar as texturas dos modelos 3D caso seja para apresenta-los quando as imagens
        // são reconhecidas

        mSurfaceChanged = true
    }

    override fun onDrawFrame(gl: GL10?) {
        // verificar se o reconhecimento de imagens esta activado
        if (vuforiaARSession.isVuforiaARRunning()) {
            // verificar se é necessário fazer algumas configurações no Vuforia
            if (mSurfaceChanged) {
                mSurfaceChanged = false

                vuforiaARSession.configureVuforiaARFrame(mWidth, mHeight, 0)
            }

            // desenhar a imagem captada pela câmara do dispositivo e os modelos 3D caso existam e se
            // a imagem for reconhecida
            // NOTA: O valor booleano que é retornado indica-nos se foi detectada a imagem que
            // queremos detectar. Por agora recebemos um valor booleano mas mais tarde podemos
            // receber o nome da imagem que detectamos, caso haja várias imagens para serem detectadas.
            val imageTargetDetected = vuforiaARSession.renderVuforiaARFrame()

            _lastImageDetected.postValue(imageTargetDetected)
        }
    }
}