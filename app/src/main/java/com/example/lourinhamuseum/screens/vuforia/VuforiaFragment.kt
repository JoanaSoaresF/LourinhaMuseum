package com.example.lourinhamuseum.screens.vuforia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.custom_view.PopupInfoView
import com.example.lourinhamuseum.databinding.VuforiaFragmentBinding


class VuforiaFragment : Fragment() {


    private lateinit var binding: VuforiaFragmentBinding
    private val viewModel: VuforiaViewModel by lazy {
        ViewModelProvider(requireActivity()).get(VuforiaViewModel::class.java)
    }

    private lateinit var detectionView: ConstraintLayout
    private lateinit var helpPopup: PopupInfoView
    private lateinit var cameraView: PreviewView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.vuforia_fragment, container, false)

        cameraView = binding.viewFinder
        detectionView = binding.detectionView
        helpPopup = binding.popupHelp


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

        helpPopup.setPopupController(viewModel)
        viewModel.detectionState.observe(viewLifecycleOwner, { state ->
            when (state) {
                VuforiaViewModel.DetectionState.CAPTURE_PRESSED -> navigateToInfo(
                    imageToDetect
                )
            }
        })

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


}