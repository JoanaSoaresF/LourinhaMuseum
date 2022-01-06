package com.example.lourinhamuseum.screens.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.VideoView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.custom_view.AudioPlayerView
import com.example.lourinhamuseum.custom_view.PopupInfoView
import com.example.lourinhamuseum.custom_view.QuizView
import com.example.lourinhamuseum.databinding.InformationFragmentBinding

class InformationFragment : Fragment() {

    private lateinit var viewModelFactory: InformationViewModelFactory
    private lateinit var viewModel: InformationViewModel
    private lateinit var binding: InformationFragmentBinding
    private lateinit var seekbar: SeekBar
    private lateinit var videoView: VideoView
    private lateinit var quizView: QuizView
    private lateinit var textPopup: PopupInfoView
    private lateinit var audioPlayerView: AudioPlayerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.information_fragment,
            container,
            false
        )

        seekbar = binding.audioPlayer.seekbar
        videoView = binding.audioPlayer.video
        quizView = binding.quizPopup
        textPopup = binding.popupInfoText
        audioPlayerView = binding.audioPlayer


        //image detected in the previous fragment
        val args by navArgs<InformationFragmentArgs>()

        viewModelFactory =
            InformationViewModelFactory(requireActivity().application, args.pointDetected)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(InformationViewModel::class.java)

        quizView.setQuizController(viewModel)
        viewModel.question.observe(viewLifecycleOwner) {
            quizView.setQuestion(it)
        }

        textPopup.setPopupController(viewModel)

        binding.informationViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.pointObservable.observe(viewLifecycleOwner) {
            binding.pointImage.point = it
        }

        //Binds the player to the fragment lifecycle
        lifecycle.addObserver(viewModel.player)

        if (viewModel.isVideo) {
            videoView.holder.addCallback(audioPlayerView)
        }

        audioPlayerView.setupAudioController(viewModel)
        seekbar.setOnSeekBarChangeListener(audioPlayerView)
        lifecycle.addObserver(audioPlayerView)

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        viewModel.backToInfoState()
        viewModel.updateDatabase()
    }
}
