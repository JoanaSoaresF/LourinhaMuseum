package com.example.lourinhamuseum.screens.vuforia

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.custom_view.LoadingView
import com.example.lourinhamuseum.custom_view.PopupInfoView
import com.example.lourinhamuseum.databinding.VuforiaFragmentBinding
import com.example.lourinhamuseum.vuforia.ImageTargetRenderer
import com.example.lourinhamuseum.vuforia.VuforiaARSession
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule


class VuforiaFragment : Fragment() {


    private lateinit var binding: VuforiaFragmentBinding
    private val viewModel: VuforiaViewModel by lazy {
        ViewModelProvider(requireActivity()).get(VuforiaViewModel::class.java)
    }

    /* Layout utilizado para inserir a view que apresenta as imagens captadas pela câmara do dispositivo. */
    private lateinit var mGLViewLayout: RelativeLayout
    private lateinit var detectionView: ConstraintLayout
    private lateinit var helpPopup: PopupInfoView

    /* View utilizada para apresentar as imagens captadas pela câmara do dispositivo. */
    private var mGLView: GLSurfaceView? = null
    private lateinit var mImageTargetRenderer: ImageTargetRenderer

    /* Layout utilizado para apresentar o carregamento da sessão do Vuforia. */
    private lateinit var mLoadingView: LoadingView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.vuforia_fragment, container, false)

        mGLViewLayout = binding.activityMainGlviewLayout
        mLoadingView = binding.activityMainLoadingView
        detectionView = binding.detectionView
        helpPopup = binding.popupHelp

        mLoadingView.show(false)

        //image selected in the previous fragment
        val imageToDetect by navArgs<VuforiaFragmentArgs>()
        viewModel.setPointToDetect(imageToDetect.imageToDetect)

        //Set the viewModel for data binding - this allows the bound layout access to
        // all of the data in the ViewModel
        binding.vuforiaViewModel = viewModel
        //specify the current activity as the lifecycle owner of the binding. This is
        // used so that the binding can observe LiveData updates
        binding.lifecycleOwner = this

        //create vuforia session
        viewModel.initializeVuforiaSession(requireActivity())

        viewModel.mVuforiaState.observe(viewLifecycleOwner, {
            /**
             * Quando a sessão de reconhecimento estiver completamente carregada
             * esconder a vista de carregamento e mostrar a imagem da câmara
             */
            Timber.d("Vuforia state: $it")
            when (it) {
                VuforiaARSession.State.INIT_DONE -> initializeARDetection()
                VuforiaARSession.State.START_AR_SUCCESS -> readyToRender()
                VuforiaARSession.State.PAUSED_AR -> initializeARDetection()
            }

        })

        helpPopup.setPopupController(viewModel)
        viewModel.detectionState.observe(viewLifecycleOwner, { state ->
            Timber.d("Vuforia fragment state: $state")
            when (state) {
                VuforiaViewModel.DetectionState.CAPTURE_PRESSED -> navigateToInfo(
                    imageToDetect
                )
            }
        })

        detectionView.setOnClickListener {
            //Calls the autofocus Native Method
            viewModel.cameraPerformAutoFocus()

            //After triggering a focus event wait 2 seconds before restoring continuous
            // autofocus
            Timer("RestoreAutoFocus", false).schedule(2000){
                viewModel.cameraRestoreAutoFocus()
            }
        }

        return binding.root
    }


    private fun navigateToInfo(imageToDetect: VuforiaFragmentArgs) {
        findNavController().navigate(
            VuforiaFragmentDirections.showInfo(
                imageToDetect.imageToDetect
            )
        )
        viewModel.navigationDone()
    }

    private fun initializeARDetection() {
        Timber.d("Start AR ${viewModel.correctARInitialization()}")
        if (viewModel.correctARInitialization()) {
            vuforiaInitializeAR()
        }
    }

    private fun readyToRender() {
        mGLViewLayout.visibility = View.VISIBLE
        detectionView.visibility = View.VISIBLE
        mLoadingView.visibility = View.GONE
        mLoadingView.hide(false)
    }


    /**
     * Este método é responsável por iniciar o reconhecimento de imagens captadas pela câmara do
     * dispositivo.
     *
     * NOTA: Este método é chamado por uma classe nativa - VuforiaWrapper.cpp - quando a inicialização
     * da biblioteca for inicializada corretamente.
     * É chamado a partir da atividade Vuforia, e gerido pelo ViewModel. Sinalizamos
     * através do viewModel que este método deve ser iniciado
     */
    private fun vuforiaInitializeAR() {
        Timber.d("Vuforia start image detection")

        createGLView()
        // iniciar o reconhecimento de imagens
        viewModel.startVuforiaAR()
    }

    private fun createGLView() {

        if (mGLView == null) {
            // criar o objecto que faz a renderização das imagens captadas pela câmara do dispositivo
            mImageTargetRenderer = viewModel.createImageTargetRenderer()
            observeImageRenderer()
            // forçar o ecrã a ficar sempre ligado
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            // apresentar o layout que mostra as imagens captadas pela câmara do dispositivo
            // Create an OpenGL ES 3.0 context (also works for 3.1, 3.2)
            mGLView = GLSurfaceView(requireContext())
            mGLView!!.holder.addCallback(object : SurfaceHolder.Callback {

                override fun surfaceCreated(holder: SurfaceHolder) {
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    viewModel.deinitVuforiaARRendering()
                }
            })
            mGLView!!.setEGLContextClientVersion(3)
            mGLView!!.setRenderer(mImageTargetRenderer)
            mGLViewLayout.addView(
                mGLView,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

    }

    private fun observeImageRenderer() {
        mImageTargetRenderer.lastImageDetected.observe(viewLifecycleOwner) { image ->
            viewModel.monitorizeDetectionProgress(image)
        }
    }

    override fun onResume() {
        Timber.d("Vuforia on resume")
        super.onResume()
        viewModel.resumeVuforiaAR()
        viewModel.initARRendering()
    }

    override fun onPause() {
        Timber.d("Vuforia on pause")
        super.onPause()
        viewModel.deinitVuforiaARRendering()
        viewModel.pauseVuforiaAR()

    }

}