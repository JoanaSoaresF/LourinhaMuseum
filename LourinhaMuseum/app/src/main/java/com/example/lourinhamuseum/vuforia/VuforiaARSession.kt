    package com.example.lourinhamuseum.vuforia

import android.app.Activity
import android.content.res.AssetManager
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import java.nio.ByteBuffer

class VuforiaARSession : LifecycleObserver {
    enum class State {
        UNKNOWN, INIT_DONE, START_AR_SUCCESS, START_AR_ERROR, PAUSED_AR
    }

    private var started = false

    /**
     * Estado da sessão vuforia
     */
    private val _vuforiaSessionState = MutableLiveData(State.UNKNOWN)
    val vuforiaSessionState: LiveData<State>
        get() = _vuforiaSessionState


    /**
     * Métodos externos nos definidos no VuforiaWrapper.cpp
     */
    private external fun initAR(activity: Activity, assetManager: AssetManager, target: Int)

    private external fun deinitAR()

    private external fun startAR(): Boolean
    private external fun stopAR()

    private external fun pauseAR()
    private external fun resumeAR()

    external fun cameraPerformAutoFocus()
    external fun cameraRestoreAutoFocus()

    private external fun initRendering()
    private external fun setTextures(
        astronautWidth: Int,
        astronautHeight: Int,
        astronautBytes: ByteBuffer,
        landerWidth: Int, landerHeight: Int,
        landerBytes: ByteBuffer
    )

    private external fun deinitRendering()
    private external fun configureRendering(
        width: Int,
        height: Int,
        orientation: Int
    ): Boolean

    private external fun renderFrame(): String?


    /**
     * Este método é responsável por inicializar a biblioteca Vuforia.
     *
     * -> Internamente são realizadas várias tarefas:
     *      1 - Iniciar o funcionamento do Vuforia com as configurações iniciais definidas;
     *      2 - Iniciar o objecto que faz a gestão do seguimento das imagens - initTrackers();
     *      3 - Carregar os dados das imagens que queremos que sejam detectadas - loadTrackerData().
     * Caso seja criado um novo ficheiro com dados de novas imagens que queremos que sejam reconhecidas
     * é preciso meter os ficheiros nos assets e alterar o nome do ficheiro na função loadTrackerData()
     * onde o target é definido pelo IMAGE_TARGET_ID;
     *
     * NOTA: A API_KEY é inserida manualmente no ficheiro AppController.cpp.
     *
     * NOTA 2: Quando a biblioteca Vuforia for inicializada, correct ou incorrectamente, a actividade
     * que chamou este método será informada.
     *
     * Função a ser executada por uma coroutine, dado que é um processo demorado e não
     * queremos bloquear a main thread
     *
     * @param activity Activity. Actividade que vai utilizar a biblioteca de realidade
     * aumentada.
     * @param target int. Identificador do tipo de reconhecimento feito pela aplicação.
     */

    fun initializeVuforiaAR(activity: Activity, target: Int) {
        if(_vuforiaSessionState.value==State.UNKNOWN){
            initAR(activity, activity.assets, target)
        }
        //Timber.d("Initialized Vuforia ${vuforiaSessionState.value}")
    }

    fun startVuforiaAR() {
        //verify if the image recognition was not already started
        if (!started) {
            Timber.d("startVuforiaAR called")
            started = startAR()
            _vuforiaSessionState.postValue( if (started) State.START_AR_SUCCESS else State
                .START_AR_ERROR)
        }
    }

    /**
     * Este método é responsável por reiniciar o reconhecimento das imagens captadas pela câmara do
     * dispositivo.
     *
     * -> Internamente são realizadas as seguintes operações:
     *      1 - Iniciar o seguimento das imagens - startTrackers() - (que foram carregadas através
     * da função loadTrackerData());
     *      2 - Verificar se a câmara já foi anteriormente iniciada e que ainda não está activa.
     * Iniciar a câmara ou iniciar a visualização dos frames captados pela câmara do dispositivo;
     */
    fun resumeVuforiaAR() {
        if (started) {
            Timber.d("resumeVuforiaAR called")
            resumeAR()
            _vuforiaSessionState.postValue(State.START_AR_SUCCESS)
        }
    }

    /**
     * Este método é responsável por parar temporariamente o reconhecimento das imagens captadas pela
     * câmara do dispositivo.
     *
     * -> Internamente são realizadas as seguintes operações:
     *      1 - Verificar se a câmara está activa. Se estiver, a apresentação dos frames captados
     * pela câmara do dispositivo é cancelada e todos os recursos utilizados pela câmara são eliminados;
     *      2 - Parar o seguimento das imagens - stopTrackers();
     */
    fun pauseVuforiaAR() {
        Timber.d("pauseVuforiaAR called")
        if (started && _vuforiaSessionState.value == State.START_AR_SUCCESS) {
            _vuforiaSessionState.postValue(State.PAUSED_AR)
            pauseAR()
        }
    }

    /**
     * Este método é responsável por parar o reconhecimento das imagens captadas pela câmara do
     * dispositivo.
     *
     * -> Internamente são realizadas as seguintes operações:
     *      1 - Verificar se a câmara do dispositivo está activa. Se estiver, a apresentação dos frames
     * captados pela câmara do dispositivo é cancelada e todos os recursos utilizados pela câmara são
     * eliminados;
     *      2 - Parar o seguimento das imagens - stopTrackers();
     */
    fun stopVuforiaAR() {
        if (started) {
            Timber.d("stopVuforiaAR called")
            started = false
            _vuforiaSessionState.postValue(State.INIT_DONE)
            deinitRendering()
            pauseAR()
            stopAR()
        }
    }

    /**
     * Este método é responsável por eliminar todos os recursos utilizados pela biblioteca Vuforia
     * que faz o reconhecimento das imagens pretendidas.
     *
     * -> Internamente são realizadas as seguintes operações:
     *      1 - Eliminar os dados das imagens que foram carregadas para serem reconhecidas em tempo
     * real - unloadTrackerData();
     *      2 - Libertar os recursos do objecto que faz a gestão do seguimento das imagens - deinitTrackers();
     */
    fun destroyVuforiaAR() {
        Timber.d("Destroying VuforiaAR")
        deinitAR()
    }

    /**
     * Este método é utilizado para inicializar a renderização das imagens captadas pela
     * câmara do dispositivo.
     */
    fun initVuforiaARRendering() {
        Timber.d("Initializing VuforiaAR Rendering")
        initRendering()
        //_vuforiaSessionState.postValue(State.READY_TO_RENDER)
    }

    /**
     * Este método é utilizado para eliminar todos os recursos utilizados para fazer a renderização
     * das imagens captadas pela câmara do dispositivo.
     */
    fun deinitVuforiaARRendering() {
        Timber.d("De-initializing VuforiaAR Rendering")
        deinitRendering()
    }

    /**
     * Este método é utilizado para configurar as dimensões e a orientação das imagens captadas pela
     * câmara do dispositivo.
     *
     * @param width int. Largura da imagem.
     * @param height int. Altura da imagem.
     * @param orientation int. Orientação do dispositivo. (Portrait - 0|1, Landscape 2)
     */
    fun configureVuforiaARFrame(width: Int, height: Int, orientation: Int) {
        configureRendering(width, height, orientation)
    }

    /**
     * Este método é utilizado para apresentar uma imagem captada pela câmara do dispositivo.
     *
     * NOTA: Neste método também é feito o reconhecimento da imagem captada, caso a imagem seja
     * reconhecida é apresentado o modelo 3D definido.
     */
    fun renderVuforiaARFrame(): Int? {
        val image = renderFrame()
        return image?.toInt()
    }

    /**
     * Método responsável por indicar se o reconhecimento de imagens está ativo
     */
    fun isVuforiaARRunning(): Boolean {
        return started
    }

    /**
     * Called if the vuforia initialization was successfully
     */
    fun initVuforiaSessionDone() {
        _vuforiaSessionState.postValue( State.INIT_DONE)
    }


}