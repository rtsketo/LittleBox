package com.arti.games.littlebox;

import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;

public class LittleBox extends Activity {

	GameView gameView;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gameView.setTouch(event.getX(), event.getY());
		if (gameView.menuing&&event.getAction() == MotionEvent.ACTION_UP) gameView.setTouchUP();
		return super.onTouchEvent(event);
	}

	@Override
	public void onBackPressed() {
		if (gameView.gaming) gameView.reset();
		else super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameView = new GameView(this);
		setContentView(gameView);
	}

	@Override
	protected void onPause() {
		gameView.onGamePause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		gameView.onGameResume();
		super.onResume();
	}
}
