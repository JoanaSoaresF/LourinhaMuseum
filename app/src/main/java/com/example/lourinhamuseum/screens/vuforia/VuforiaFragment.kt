package com.example.lourinhamuseum.screens.vuforia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.custom_view.PopupInfoView
import com.example.lourinhamuseum.databinding.VuforiaFragmentBinding
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber


class VuforiaFragment : Fragment() {


    private lateinit var binding: VuforiaFragmentBinding
    private val viewModel: VuforiaViewModel by lazy {
        ViewModelProvider(requireActivity()).get(VuforiaViewModel::class.java)
    }
    private lateinit var helpPopup: PopupInfoView
    private lateinit var cameraView: PreviewView

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.vuforia_fragment,
            container,
            false
        )

        helpPopup = binding.popupHelp
        cameraView = binding.previewView


        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))


        //image selected in the previous fragment
        val imageToDetect by navArgs<VuforiaFragmentArgs>()
        Timber.d("Vuforia Fragment Point: ${imageToDetect.imageToDetect}")
        viewModel.setPointToDetect(imageToDetect.imageToDetect)

        //Set the viewModel for data binding - this allows the bound layout access to
        // all of the data in the ViewModel
        binding.vuforiaViewModel = viewModel
        //specify the current activity as the lifecycle owner of the binding. This is
        // used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner


        helpPopup.setPopupController(viewModel)
        viewModel.detectionState.observe(viewLifecycleOwner, { state ->
            Timber.d("Vuforia Fragment State: $state")
            when (state) {
                VuforiaViewModel.DetectionState.CAPTURE_PRESSED -> navigateToInfo(
                    imageToDetect
                )
            }
        })
        Timber.d("Vuforia Fragment Created")

        return binding.root
    }


    private fun navigateToInfo(imageToDetect: VuforiaFragmentArgs) {
        Timber.d("Vuforia Fragment navigate to info")
        findNavController().navigate(
            VuforiaFragmentDirections.showInfo(
                imageToDetect.imageToDetect
            )
        )
        viewModel.navigationDone()
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        var preview : Preview = Preview.Builder()
            .build()

        var cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(cameraView.surfaceProvider)

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }


}