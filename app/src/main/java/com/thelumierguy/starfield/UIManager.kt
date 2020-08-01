package com.thelumierguy.starfield

import androidx.lifecycle.Observer
import com.thelumierguy.starfield.utils.ScreenStates
import com.thelumierguy.starfield.views.BlinkingImage
import com.thelumierguy.starfield.views.LogoView


fun MainActivity.observeScreenStates() {
    mainViewModel.observeScreenState().observe(this, Observer {
        when (it) {
            ScreenStates.APP_INIT -> {
                transitionToScene(appInitScene)
            }
            ScreenStates.GAME_MENU -> {
                transitionToScene(gameMenuScene)
                gameMenuScene.sceneRoot.findViewById<LogoView>(R.id.imageView)?.enableTinkling =
                    true
                gameMenuScene.sceneRoot.findViewById<BlinkingImage>(R.id.iv_text)?.startBlinking()
            }
            ScreenStates.START_GAME -> {
                transitionToScene(startGameScene)
            }
        }
    })
}