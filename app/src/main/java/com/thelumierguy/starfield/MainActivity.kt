package com.thelumierguy.starfield

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.thelumierguy.starfield.utils.ScreenStates
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.scene_game_start.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val transitionManager: TransitionManager by lazy {
        TransitionManager().apply {
            setTransition(appInitScene, gameMenuScene, transition)
            setTransition(gameMenuScene, startGameScene, transition)
            setTransition(startGameScene, gameMenuScene, transition)
        }
    }

    val mainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }


    private val transition: Transition by lazy {
        TransitionInflater.from(this)
            .inflateTransition(R.transition.screen_transitions)
    }


    private val gyroscopeSensor: Sensor by lazy {
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private val gyroscopeSensorListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
//            space_ship?.processSensorEvents(sensorEvent)
//            star_field?.processSensorEvents(sensorEvent)
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    val appInitScene: Scene by lazy { createScene(R.layout.scene_app_init) }
    val gameMenuScene: Scene by lazy { createScene(R.layout.scene_menu) }
    val startGameScene: Scene by lazy { createScene(R.layout.scene_game_start) }

    fun transitionToScene(scene: Scene) {
        transitionManager.transitionTo(scene)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goFullScreen()
        observeScreenStates()
        addTouchHandler()
        initMenu()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addTouchHandler() {
        root_view.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                handleTouch()
            }
            true
        }
    }

    private fun handleTouch() {
        when (mainViewModel.observeScreenState().value) {
            ScreenStates.START_GAME -> {
                space_ship.boost()
                star_field.setTrails()
            }
            ScreenStates.GAME_MENU -> {
                pushUIState(ScreenStates.START_GAME)
            }
            else -> {
                Toast.makeText(this@MainActivity, "Hello", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initMenu() {
        lifecycleScope.launch(Dispatchers.Main) {
            pushUIState(ScreenStates.APP_INIT)
            delay(600)
            pushUIState(ScreenStates.GAME_MENU)
        }
    }

    private fun pushUIState(screenStates: ScreenStates) {
        mainViewModel.updateUIState(screenStates)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            gyroscopeSensorListener,
            gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(gyroscopeSensorListener)
    }

    private fun goFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun createScene(@LayoutRes layout: Int) =
        Scene.getSceneForLayout(id_frame as ViewGroup, layout, this)

    override fun onBackPressed() {
        if (mainViewModel.observeScreenState().value == ScreenStates.START_GAME) {
            pushUIState(ScreenStates.GAME_MENU)
        } else {
            super.onBackPressed()
        }
    }
}
