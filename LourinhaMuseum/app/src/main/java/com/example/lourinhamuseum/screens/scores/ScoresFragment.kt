package com.example.lourinhamuseum.screens.scores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.custom_view.PapyrusLoading
import com.example.lourinhamuseum.custom_view.PapyrusLoading.Companion.START_DELAY
import com.example.lourinhamuseum.data.repository.MuseumRepository
import com.example.lourinhamuseum.databinding.ScoresFragmentBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoresFragment : Fragment(), PapyrusLoading {

    private lateinit var binding: ScoresFragmentBinding
    private lateinit var museumRepository: MuseumRepository
    private lateinit var background: ImageView
    private lateinit var ranking: RecyclerView
    private lateinit var title: TextView
    private lateinit var header: ConstraintLayout


    override val papyrusImage: ImageView
        get() = background


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.scores_fragment,
            container, false
        )

        binding.lifecycleOwner = this
        background = binding.scoreBackgroud
        ranking = binding.rankingList
        title = binding.scoresTitle
        header = binding.header
        show()
        val adapter = ScoresAdapter()
        binding.rankingList.adapter = adapter

        museumRepository = MuseumRepository.getMuseumRepository(requireContext())
        init()
        val ranking = museumRepository.ranking
        ranking.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }
        return binding.root
    }

    private fun init() {
        GlobalScope.launch {
            museumRepository.updateScores()
        }
    }

    override fun onShowEnd() {
        header.visibility = View.VISIBLE
    }

    override fun onShowStart() {
        title.visibility = View.VISIBLE
        ranking.animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        ranking.animation.startOffset = START_DELAY
        ranking.animate()
    }

    override fun onCloseEnd() {
    }

    override fun onCloseStart() {
        header.visibility = View.INVISIBLE
    }

}

