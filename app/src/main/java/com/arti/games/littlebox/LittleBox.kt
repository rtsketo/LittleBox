package com.arti.games.littlebox

import android.os.Bundle
import android.app.Activity
import android.view.MotionEvent

class LittleBox : Activity() {

    private lateinit var gameView: GameView

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gameView.setTouch(event.x, event.y)
        if (gameView.menuing && event.action == MotionEvent.ACTION_UP) gameView.setTouchUP()
        return super.onTouchEvent(event)
    }

    override fun onBackPressed() {
        if (gameView.gaming)
            gameView.reset()
        else
            super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = GameView(this)
        setContentView(gameView)
    }

    override fun onPause() {
        gameView.onGamePause()
        super.onPause()
    }

    override fun onResume() {
        gameView.onGameResume()
        super.onResume()
    }
}
