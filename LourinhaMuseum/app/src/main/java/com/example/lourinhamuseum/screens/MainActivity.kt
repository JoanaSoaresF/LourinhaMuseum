package com.example.lourinhamuseum.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.lourinhamuseum.R
import com.example.lourinhamuseum.databinding.MainActivityBinding
import com.example.lourinhamuseum.screens.allCards.AllCardsFragmentDirections
import com.example.lourinhamuseum.screens.vuforia.VuforiaViewModel
import com.example.lourinhamuseum.utils.ApplicationSoundsManager
import com.google.android.material.navigation.NavigationView
import timber.log.Timber

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    /**
     * ViewModel is linked to the activity so that the vuforia session doesn't need
     * to be initialize every time the Vuforia Fragment starts
     */
    private val viewModel by lazy {
        ViewModelProvider(this).get(VuforiaViewModel::class.java)
    }
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        val binding: MainActivityBinding = DataBindingUtil.setContentView(
            this, R.layout
                .main_activity
        )
        val navigationButton = binding.navigationButton
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        navController.addOnDestinationChangedListener { nc: NavController, nd:
        NavDestination, bundle: Bundle? ->
            when (nd.id) {
                nc.graph.startDestination -> {
                    navigationButton.visibility = View.GONE
                }
                R.id.all_cards_fragment -> {
                    navigationButton.setImageResource(R.drawable.menu_icon)
                    navigationButton.setOnClickListener {
                        drawerLayout.openDrawer(GravityCompat.START)
                    }
                    navigationButton.visibility = View.VISIBLE
                }
                else -> {
                    navigationButton.setImageResource(R.drawable.back)
                    navigationButton.setOnClickListener {
                        nc.popBackStack()
                    }
                    navigationButton.visibility = View.VISIBLE
                }
            }
        }

        val navView = binding.navView
//        NavigationUI.setupWithNavController(navView, navController)
        navView.setNavigationItemSelectedListener(this)

        //define the behaviour of the back button in the toolbar
        lifecycle.addObserver(ApplicationSoundsManager.getSoundManager())
    }

    /**
     * Este método é responsável por iniciar o reconhecimento de imagens captadas pela câmara do
     * dispositivo.
     *
     * NOTA: Este método é chamado por uma classe nativa - VuforiaWrapper.cpp - quando a inicialização
     * da biblioteca for inicializada corretamente.
     */
    fun vuforiaARInitialized() {
        Timber.d("vuforiaARInitialized from cpp call")
        viewModel.sinalizeCorrectARInitialization()
    }

    /**
     * Este método é responsável por apresentar uma mensagem de erro quando ocorre algum erro na
     * inicialização da biblioteca Vuforia.
     *
     * NOTA: Este método é chamado por uma classe nativa - VuforiaWrapper.cpp - quando a inicialização
     * da biblioteca não for inicializada corretamente.
     */
    fun errorInitializingVuforiaAR(message: String) {
        // mostrar a mensagem de erro
        Timber.e(" Vuforia init error: $message")
        warningDialog()
    }

    /**
     * Este método é responsável por apresentar uma mensagem de erro quando ocorre um erro
     * de inicialização da sessão do Vuforia.
     */
    private fun warningDialog() {
        val okOnClickListener = DialogInterface.OnClickListener { dialog, which ->
            viewModel.eliminateVuforiaResources()
        }

        val alertDialogBuilder = AlertDialog.Builder(applicationContext)
        alertDialogBuilder.setMessage(R.string.vuforia_error_message)
        alertDialogBuilder.setPositiveButton(R.string.ok_button, okOnClickListener)
        alertDialogBuilder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("Main activity on destroy")
        viewModel.eliminateVuforiaResources()
    }

    override fun onBackPressed() {
        //parar temporariamente a apresentação das imagens captadas pela câmara do
        // dispositivo
        viewModel.pauseVuforiaAR()
        //confirmar saída
        exitDialog()
    }

    /**
     * Apresenta uma caixa de diálogo a confirmar que o utilizador pretende sair da
     * aplicação. Se sim termina a atividade, se não continua na aplicação
     */
    private fun exitDialog() {
        val okOnClickListener = DialogInterface.OnClickListener { dialog, which ->
            finish()
        }
        val notOkOnClickListener = DialogInterface.OnClickListener { dialog, which ->
            viewModel.resumeVuforiaAR()
        }

        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setMessage(R.string.leaving_message)
        alertDialogBuilder.setPositiveButton(R.string.yes_button, okOnClickListener)
        alertDialogBuilder.setNegativeButton(R.string.no_button, notOkOnClickListener)
        alertDialogBuilder.create().show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        when (item.itemId) {
            R.id.scoresFragment -> {
                navController.navigate(AllCardsFragmentDirections.showScoresAction())
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        // Load the external library with Vuforia engine
        init {
            System.loadLibrary("VuforiaExample")
        }
    }
}