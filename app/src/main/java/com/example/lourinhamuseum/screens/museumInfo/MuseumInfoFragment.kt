package com.example.lourinhamuseum.screens.museumInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.data.repository.MuseumRepository
import com.example.lourinhamuseum.databinding.FragmentMuseumInfoBinding
import com.example.lourinhamuseum.databinding.WelcomeFragmentBinding
import com.example.lourinhamuseum.screens.welcome.WelcomeFragmentDirections
import com.example.lourinhamuseum.screens.welcome.WelcomeViewModel
import timber.log.Timber


class MuseumInfoFragment : Fragment() {

    private lateinit var binding: FragmentMuseumInfoBinding
    private lateinit var museumInfo: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_museum_info,
            container,
            false
        )
        museumInfo = binding.museuInfoText

        val museum = MuseumRepository.getMuseumRepository(requireContext()).museum!!

        museumInfo.text = museum.contents[0].content
        Timber.d("${museum.contents[0].content}xxxx")



        return binding.root
    }


}