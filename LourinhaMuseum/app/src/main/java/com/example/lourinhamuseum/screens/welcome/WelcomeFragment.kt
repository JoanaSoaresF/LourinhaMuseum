package com.example.lourinhamuseum.screens.welcome

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.databinding.WelcomeFragmentBinding
import com.example.lourinhamuseum.utils.ApplicationSoundsManager
import timber.log.Timber


class WelcomeFragment : Fragment() {

    private lateinit var binding: WelcomeFragmentBinding
    private val viewModel: WelcomeViewModel by lazy {
        ViewModelProvider(this).get(WelcomeViewModel::class.java)
    }
    private lateinit var buttonText: TextView
    private lateinit var progressBarButton: ProgressBar
    private lateinit var usernameEditText: EditText
    private lateinit var usernameButton: Button
    private lateinit var welcomeTitle: TextView
    private lateinit var soldier1: ImageView
    private lateinit var soldier2: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.welcome_fragment,
            container,
            false
        )
        buttonText = binding.downloadText
        progressBarButton = binding.filesProgressBar
        usernameEditText = binding.usernameTextEdit
        usernameButton = binding.usernameButton
        welcomeTitle = binding.welcomeTitle
        soldier1 = binding.soldier1
        soldier2 = binding.soldier2


        //Set the viewModel for data binding - this allows the bound layout access to
        // all of the data in the ViewModel
        binding.viewModel = viewModel
        //specify the current activity as the lifecycle owner of the binding. This is
        // used so that the binding can observe LiveData updates
        binding.lifecycleOwner = this


        //Changes the button appearance according to the state
        viewModel.buttonState.observe(viewLifecycleOwner, { state ->
            Timber.d("Welcome state $state")
            when (state) {
                WelcomeViewModel.State.READY -> setButtonDownload()
                WelcomeViewModel.State.DOWNLOADING -> animate()
                WelcomeViewModel.State.PLAY -> setButtonPlay()
                WelcomeViewModel.State.LOADING -> setLoadingButton()
                WelcomeViewModel.State.ERROR -> setErrorButton()
            }
        })
        //
        viewModel.navigateToGame.observe(viewLifecycleOwner, { navigate ->
            if (navigate) {
                animateFinal()
            }
        })
        viewModel.isUserDefined.observe(viewLifecycleOwner) {

            Timber.d("Is user defined: $it")
            if (it) {
                setUserDefined()
            }
        }
        usernameButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val success = viewModel.chooseUsername(username)
            if (!success) {
                Toast.makeText(
                    context,
                    getString(R.string.username_error),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                animateInverse()
            }
            hideKeyboard()
        }
        binding.root.setOnClickListener {
            usernameEditText.clearFocus()
        }

        usernameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
            }
        }
        return binding.root
    }

    private fun navigate() {
        findNavController().navigate(WelcomeFragmentDirections.startGame())
        viewModel.navigationDone()
    }

    private fun setErrorButton() {
        buttonText.text = getString(R.string.error_loading_museum_button)
//        button.setBackgroundColor(getColor(requireContext(), R.color.color_button_cancel))
//        buttonText.background = getDrawable(requireContext(), R.drawable.load_button)
        ViewCompat.setBackgroundTintList(
            buttonText, getColorStateList(
                requireContext(), R.color
                    .color_button_cancel
            )
        )
        Toast.makeText(
            context,
            getString(R.string.error_loading_museum_warning),
            Toast.LENGTH_LONG
        ).show()
    }


    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as
                    InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(usernameEditText.windowToken, 0)
    }

    private fun setUserDefined() {
        welcomeTitle.visibility = View.GONE
//        animateFinal()
        usernameButton.visibility = View.GONE
        usernameEditText.visibility = View.GONE
        welcomeTitle.text = getString(R.string.welcome_username, viewModel.getUsername())

    }


    private fun setLoadingButton() {
        progressBarButton.visibility = View.INVISIBLE
        buttonText.text = getString(R.string.loading)
//        button.setBackgroundColor(getColor(requireContext(), R.color.loading_color))
        buttonText.background = getDrawable(requireContext(),R.drawable.cards_background)
        ViewCompat.setBackgroundTintList(
            buttonText, getColorStateList(
                requireContext(), R.color.loading_color
            )
        )
    }


    private fun setButtonPlay() {
        progressBarButton.visibility = View.INVISIBLE
        buttonText.text = getString(R.string.welcome_button_play)
//        buttonText.background = getDrawable(requireContext(),R.drawable.cards_background)
//        button.setBackgroundColor(getColor(requireContext(), R.color.color_button_play))
        ViewCompat.setBackgroundTintList(
            buttonText, getColorStateList(
                requireContext(), R.color
                    .color_button_play
            )
        )
    }


    private fun setButtonDownload() {
        progressBarButton.visibility = View.VISIBLE
        buttonText.text = getString(R.string.welcome_button_download)
//        buttonText.background = null
        ViewCompat.setBackgroundTintList(
            buttonText, getColorStateList(
                requireContext(), R.color.transparent)
        )
//        button.setBackgroundColor(getColor(requireContext(), R.color.color_button_play))
    }

    private fun animate() {
        soldier1.animate().translationXBy(-600F).withEndAction {
            welcomeTitle.visibility = View.VISIBLE
            usernameButton.visibility = View.VISIBLE
            usernameEditText.visibility = View.VISIBLE
        }.setDuration(1000L).start()
        soldier2.animate().translationXBy(500F).setDuration(1000L).start()
        ApplicationSoundsManager.getSoundManager().onDownloadClick(requireContext())

    }

    private fun animateInverse() {
        welcomeTitle.visibility = View.GONE
        usernameButton.visibility = View.GONE
        usernameEditText.visibility = View.GONE

        soldier1.animate()
            .translationXBy(600F)
            .setDuration(1000L)
            .start()

        soldier2.animate()
            .translationXBy(-500F)
            .setDuration(1000L)
            .start()

    }

    private fun animateFinal() {
        welcomeTitle.visibility = View.VISIBLE

        soldier1.animate()
            .translationX(-300F)
            .setDuration(1500L)
            .withEndAction {
                navigate()
            }.start()

        soldier2.animate()
            .translationX(200F)
            .setDuration(1500L)
            .start()
        ApplicationSoundsManager.getSoundManager().onDownloadClick(requireContext())

    }

}
