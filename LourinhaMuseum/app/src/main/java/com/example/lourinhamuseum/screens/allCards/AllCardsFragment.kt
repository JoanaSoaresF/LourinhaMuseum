package com.example.lourinhamuseum.screens.allCards

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.databinding.AllCardsFragmentBinding


class AllCardsFragment : Fragment() {

    companion object {
        private const val REQUIRED_PERMISSIONS = Manifest.permission.CAMERA
        const val SPAN_WIDTH_SIZE = 3
    }

    private lateinit var binding: AllCardsFragmentBinding
    private val viewModel: AllCardsViewModel by lazy {
        ViewModelProvider(this).get(AllCardsViewModel::class.java)
    }


    private val permissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                retryRequestPermissions()
            } else {
                viewModel.permissionGranted()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.all_cards_fragment,
            container,
            false
        )

        //Allows Data Binding to Observe LiveData with the lifecycle of this fragment
        binding.lifecycleOwner = this
        //Giving the binding access to the view model
        binding.viewModel = viewModel

        val manager = GridLayoutManager(activity, SPAN_WIDTH_SIZE)

        /**
         * Sets the adapter od the CardGrid [RecyclerView] with clickHandler lambda that
         * tells the view model when our point is clicked
         */
        val adapter = CardGridAdapter(CardGridAdapter.OnCardClicked {
            viewModel.imageClicked(it)
        })

        val cardsGrid = binding.cardsGrid

        cardsGrid.adapter = adapter

        val headers = viewModel.museum.headersIndexes

        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                in headers -> SPAN_WIDTH_SIZE
                else -> 1
            }
        }
        binding.cardsGrid.layoutManager = manager


        val decoration = DividerItemDecorator(requireContext())
        cardsGrid.addItemDecoration(decoration)

        viewModel.state.observe(viewLifecycleOwner, {
            when (it) {
                AllCardsViewModel.State.REQUEST_PERMISSIONS -> requestCameraPermissions()
                AllCardsViewModel.State.DETECTION -> navigateToDetection()
                AllCardsViewModel.State.INFO -> navigateToInfo()
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.playLoop()
    }

    private fun navigateToDetection() {
        findNavController().navigate(
            AllCardsFragmentDirections.startDetection(viewModel.pointToDetect!!)
        )
        viewModel.navigationDone()
    }

    private fun navigateToInfo() {
        findNavController().navigate(
            AllCardsFragmentDirections.showInfoFound(viewModel.pointToDetect!!)
        )
        viewModel.navigationDone()
    }

    private fun requestCameraPermissions() {
        permissions.launch(REQUIRED_PERMISSIONS)
    }

    private fun retryRequestPermissions() {
        if (viewModel.confirmPermissionsDenied()) {
            warningDialog()
        } else {
            requireActivity().finish()
        }
    }

    private fun warningDialog() {
        val okOnClickListener = DialogInterface.OnClickListener { dialog, which ->
            viewModel.permissionRequested = true
            requestCameraPermissions()
        }
        val notOkOnClickListener = DialogInterface.OnClickListener { dialog, which ->
            requireActivity().finish()
        }

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setMessage(R.string.camera_permissions_message)
        alertDialogBuilder.setPositiveButton(R.string.yes_button, okOnClickListener)
        alertDialogBuilder.setNegativeButton(R.string.no_button, notOkOnClickListener)
        alertDialogBuilder.create().show()
    }

}

