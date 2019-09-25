package com.arti.games.littlebox;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {
	SurfaceHolder    surfaceHolder;
	Context          context;
	Saves            saves;

	Canvas           tavla;
	Thread           viewLoop;
	Rect             menuBounds           = new Rect();
	Random           rand                 = new Random();
	SoundPool        sp                   = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

	Bitmap           littleFood;
	Bitmap           littlePower[]        = new Bitmap[6];
	Bitmap           littleBox;
	Bitmap           littleStar;
	Bitmap           littleDyingFood[]    = new Bitmap[5];
	Bitmap           littleDyingBox[]     = new Bitmap[9];
	Bitmap           littleDyingSpeed[][] = new Bitmap[2][13];
	Bitmap           littleDyingBad[]     = new Bitmap[11];
	Bitmap           littleSpeed[][]      = new Bitmap[2][6];
	Bitmap           littleBad[]          = new Bitmap[2];
	Bitmap           littleLight[]        = new Bitmap[5];

	float            x[]                  = new float[12];
	float            y[]                  = new float[12];
	float            score[]              = new float[2];
	float            radMenu[]            = new float[4];

	float            star[][]             = new float[50][2];
	float            food[][]             = new float[20][3];
	float            bad[][]              = new float[10][3];
	float            speed[][]            = new float[5][4];
	float            powup[]              = new float[3];
	float            speedBox[]           = new float[2];

	float            radLight;
	float            radFood;
	float            radPower;
	float            radBox;
	float            radStar;
	float            radBad;
	float            radSpeed;
	float            radDyingBox;
	float            radDyingBad;
	float            dyingSpeedSpeed[]    = new float[5];
	float            dyingPowerSpeed;

	Paint            alpha60              = new Paint();
	Paint            alpha230             = new Paint();
	Paint            alphaPower           = new Paint();
	Paint            textPaint            = new Paint();
	Paint            menuScore            = new Paint();
	Paint            menuWhiteLarge       = new Paint();
	Paint            menuCyanLarge        = new Paint();

	String           menuText[]           = new String[3];

	int              level;
	int              height;
	int              width;

	int              frame;
	int              speedGlobal;
	int              badChance;
	int              speedBad;
	int              dyingSpeedRelated[];
	int              dyingFoodRelated[];
	int              dyingBoxRelated[];
	int              dyingBadRelated[];
	int              dyingPowerRelated;

	int              dyingSpeedSound[][]  = { { R.raw.speeddown01, R.raw.speeddown02 },
	        { R.raw.speedup01, R.raw.speedup02 } };
	int              dyingPowerSound[]    = { R.raw.bloop01, R.raw.bloop02, R.raw.bloop03,
	        R.raw.bloop04                };

	int              foodConsume[]        = { R.drawable.foodcons01, R.drawable.foodcons02,
	        R.drawable.foodcons03, R.drawable.foodcons04, R.drawable.foodcons05 };

	int              speedUP[]            = { R.drawable.speedup01, R.drawable.speedup02,
	        R.drawable.speedup03, R.drawable.speedup04, R.drawable.speedup05, R.drawable.speedup06 };
	int              speedUPDying[]       = { R.drawable.speedupdying01, R.drawable.speedupdying02,
	        R.drawable.speedupdying04, R.drawable.speedupdying05, R.drawable.speedupdying06,
	        R.drawable.speedupdying07, R.drawable.speedupdying08, R.drawable.speedupdying09,
	        R.drawable.speedupdying10, R.drawable.speedupdying11, R.drawable.speedupdying12 };

	int              speedDown[]          = { R.drawable.speeddown01, R.drawable.speeddown02,
	        R.drawable.speeddown03, R.drawable.speeddown04, R.drawable.speeddown05,
	        R.drawable.speeddown06       };
	int              speedDownDying[]     = { R.drawable.speeddowndying01,
	        R.drawable.speeddowndying02, R.drawable.speeddowndying03, R.drawable.speeddowndying04,
	        R.drawable.speeddowndying05, R.drawable.speeddowndying06, R.drawable.speeddowndying07,
	        R.drawable.speeddowndying08, R.drawable.speeddowndying09, R.drawable.speeddowndying10,
	        R.drawable.speeddowndying11, R.drawable.speeddowndying12, R.drawable.speeddowndying13 };

	int              power[]              = { R.drawable.power01, R.drawable.power02,
	        R.drawable.power03, R.drawable.power04, R.drawable.power05, R.drawable.power06 };

	int              dyingBoxSound        = R.raw.boxexplo;
	int              dyingBoxFrame[]      = { R.drawable.xplo01, R.drawable.xplo02,
	        R.drawable.xplo03, R.drawable.xplo04, R.drawable.xplo05, R.drawable.xplo06,
	        R.drawable.xplo07, R.drawable.xplo08, R.drawable.xplo09 };

	int              foodPop[]            = { R.raw.pip01, R.raw.pip02, R.raw.pip03, R.raw.pip04,
	        R.raw.pip05, R.raw.pip06, R.raw.pip07, R.raw.pip08, R.raw.pip09, R.raw.pip10 };

	int              explo[]              = { R.raw.explo01, R.raw.explo02, R.raw.explo03,
	        R.raw.explo04, R.raw.explo05 };

	int              dyingBadSound[]      = { R.raw.noo1, R.raw.noo2, R.raw.noo3, R.raw.noo4 };
	int              dyingBadFrame[]      = { R.drawable.baddying01, R.drawable.baddying02,
	        R.drawable.baddying03, R.drawable.baddying04, R.drawable.baddying05,
	        R.drawable.baddying06, R.drawable.baddying07, R.drawable.baddying08,
	        R.drawable.baddying09, R.drawable.baddying10, R.drawable.baddying11, };
	int              badBarking[]         = { R.raw.arf01, R.raw.arf02, R.raw.arf03, R.raw.arf04,
	        R.raw.arf05, R.raw.arf06, R.raw.arf07, R.raw.arf08, R.raw.arf09, R.raw.arf10 };

	long             frametime[]          = new long[3];

	boolean          showFPS              = false;
	volatile boolean running              = true;
	volatile boolean gaming               = false;
	volatile boolean menuing              = true;
	volatile boolean optioning            = false;
	volatile boolean firstTime            = true;
	volatile boolean empowered            = false;
	volatile boolean dyingBox             = false;
	volatile boolean dyingBad[]           = new boolean[10];
	volatile boolean pursuingBad[]        = new boolean[10];

	public void tavlaInit(final boolean firstTime) {
		this.firstTime = firstTime;
		tavlaInit();
	}

	private void tavlaInit() {
		if (firstTime) {

			level = 0;
			frame = 1;
			speedGlobal = 3;
			badChance = 14;
			speedBad = 190;
			score[0] = 0;
			speedBox[1] = 1;
			speedBox[0] = (float) .25;

			dyingPowerSpeed = (float) 0.1;
			for (int i = 0; i < 5; i++)
				dyingSpeedSpeed[i] = (float) 0.1;

			dyingPowerRelated = 0;
			dyingSpeedRelated = new int[5];
			dyingFoodRelated = new int[20];
			dyingBoxRelated = new int[3];
			dyingBadRelated = new int[10];

			dyingBox = false;

			alpha60.setAlpha(60);
			alpha230.setAlpha(230);
			alphaPower.setAlpha(255);

			textPaint.setColor(Color.WHITE);
			textPaint.setTextSize(pixelDP(16));

			menuScore.setColor(Color.WHITE);
			menuScore.setTextSize(pixelDP(50));
			menuWhiteLarge.setColor(Color.WHITE);
			menuWhiteLarge.setTextSize(pixelDP(50));
			menuCyanLarge.setColor(Color.CYAN);
			menuCyanLarge.setTextSize(pixelDP(50));

			menuText[0] = "Start";
			menuText[1] = "Options";
			menuText[2] = "Exit";

			for (int i = 0; i < menuText.length; i++)
				radMenu[i] = menuWhiteLarge.measureText(menuText[i]) / 2;

			menuWhiteLarge.getTextBounds("SOE", 0, 1, menuBounds);

			width = tavla.getWidth();
			height = tavla.getHeight();

			for (int i = 0; i < littleDyingBox.length; i++)
				littleDyingBox[i] = BitmapFactory.decodeResource(getContext().getResources(),
				        dyingBoxFrame[i]);

			for (int i = 0; i < littleDyingBad.length; i++)
				littleDyingBad[i] = BitmapFactory.decodeResource(getContext().getResources(),
				        dyingBadFrame[i]);
			for (int i = 0; i < dyingBad.length; i++)
				dyingBad[i] = false;
			for (int i = 0; i < pursuingBad.length; i++)
				pursuingBad[i] = false;

			littleBox = BitmapFactory.decodeResource(getContext().getResources(),
			        R.drawable.littlebox);
			littleStar = BitmapFactory.decodeResource(getContext().getResources(),
			        R.drawable.littlestar);
			littleFood = BitmapFactory.decodeResource(getContext().getResources(),
			        R.drawable.spheregrey);
			littleBad[0] = BitmapFactory.decodeResource(getContext().getResources(),
			        R.drawable.bad01);
			littleBad[1] = BitmapFactory.decodeResource(getContext().getResources(),
			        R.drawable.bad02);

			for (int i = 0; i < 13; i++) {
				if (i < 11) littleDyingSpeed[1][i] = BitmapFactory.decodeResource(getContext()
				        .getResources(), speedUPDying[i]);
				littleDyingSpeed[0][i] = BitmapFactory.decodeResource(getContext().getResources(),
				        speedDownDying[i]);
			}

			for (int i = 0; i < 5; i++)
				littleDyingFood[i] = BitmapFactory.decodeResource(getContext().getResources(),
				        foodConsume[i]);

			for (int i = 0; i < 6; i++)
				littlePower[i] = BitmapFactory
				        .decodeResource(getContext().getResources(), power[i]);

			for (int i = 0; i < 6; i++) {
				littleSpeed[1][i] = BitmapFactory.decodeResource(getContext().getResources(),
				        speedUP[i]);
				littleSpeed[0][i] = BitmapFactory.decodeResource(getContext().getResources(),
				        speedDown[i]);
			}

			radBad = littleBad[0].getHeight() / 2;
			radSpeed = littleSpeed[0][0].getHeight() / 2;
			radFood = littleFood.getHeight() / 2;
			radPower = littlePower[0].getHeight() / 2;
			radBox = littleBox.getHeight() / 2;
			radDyingBad = littleDyingBad[0].getHeight() / 2;
			radDyingBox = littleDyingBox[0].getHeight() / 2;
			radLight = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.light)
			        .getHeight() / 4;

			powup[0] = 13371337;
			for (int i = 0; i < speed.length; i++)
				speed[i][0] = 13371337;
			for (int i = 0; i < food.length; i++)
				food[i][0] = 13371337;
			for (int i = 0; i < bad.length; i++)
				bad[i][0] = 13371337;
			for (int i = 0; i < 5; i++)
				littleLight[i] = Bitmap
				        .createScaledBitmap(BitmapFactory.decodeResource(getContext()
				                .getResources(), R.drawable.light), (int) (radLight - i * radLight
				                / 5) * 2, (int) (radLight - i * radLight / 5) * 2, false);

			for (int i = 0; i < star.length; i++) {
				star[i][0] = rand.nextInt(width * 2);
				star[i][1] = rand.nextInt(height + 1);
			}

			for (int i = 0; i < 12; i++) {
				x[i] = width / 2;
				y[i] = height * 5 / 6;
			}

			sp.play(dyingBoxSound, 0, 0, 0, 0, 2);
			for (int i = 0; i < dyingSpeedSound.length; i++)
				for (int j = 0; j < dyingSpeedSound[i].length; j++)
					sp.play(dyingSpeedSound[i][j], 0, 0, 0, 0, 2);
			for (int i = 0; i < dyingPowerSound.length; i++)
				sp.play(dyingPowerSound[i], 0, 0, 0, 0, 2);
			for (int i = 0; i < dyingBadSound.length; i++)
				sp.play(dyingBadSound[i], 0, 0, 0, 0, 2);
			for (int i = 0; i < badBarking.length; i++)
				sp.play(badBarking[i], 0, 0, 0, 0, 2);
			for (int i = 0; i < foodPop.length; i++)
				sp.play(foodPop[i], 0, 0, 0, 0, 2);
			for (int i = 0; i < explo.length; i++)
				sp.play(explo[i], 0, 0, 0, 0, 2);

			firstTime = false;
			System.gc();
		}
	}

	@Override
	public void run() {
		while (running) {
			if (surfaceHolder.getSurface().isValid()) try {
				tavla = surfaceHolder.lockCanvas();

				if (menuing) {
					tavlaInit();
					movementLog(0);

					drawBG();
					drawLittleStar();
					drawLittleBox();
					drawMainMenu();
				}
				else
					if (gaming) {
						tavlaInit();
						movementLog(0);

						drawBG();
						drawLittleStar();
						drawLittleFood();
						drawLittlePower();
						drawLittleSpeed();
						drawLittleBox();
						drawLittleBad();

						showFPS();
						showSpeed();
						showLevel();
						showScore();

						levelUP();
						drawEnding();
					}
					else
						if (optioning) {}

				surfaceHolder.unlockCanvasAndPost(tavla);
				frame++;
				Thread.sleep(30);
			} catch (InterruptedException e) {
				System.out.println(e);
			}

		}
	}

	private void drawEnding() {
		if (dyingBox) {
			if (menuScore.getAlpha() < 255) menuScore.setAlpha(menuScore.getAlpha() + 1);
			else menuScore.setAlpha(0);
			tavla.drawText("Game Over!", width / 2 - menuScore.measureText("Game Over!") / 2,
			        height / 2, menuScore);
			tavla.drawText("Score: " + (int) score[0],
			        width / 2 - menuScore.measureText("Score: " + (int) score[0]) / 2, height / 2
			                + pixelDP(70), menuScore);
			if (frame >= dyingBoxRelated[0] + 166) reset();
		}
	}

	private void drawMainMenu() {
		String scoreText = "Score: " + saves.read("Score");
		tavla.drawText(scoreText, width / 2 - textPaint.measureText(scoreText) / 2, height / 2
		        + pixelDP(200), textPaint);

		// tavla.drawRect(width/2-radMenu[0], height/2-
		// pixelDP(100)+ menuBounds.top, width/2+radMenu[0],
		// height/2- pixelDP(100)-menuBounds.bottom, menuPaint);

		if (x[0] > width / 2 - radMenu[0] && x[0] < width / 2 + radMenu[0]
		        && y[0] > height / 2 - pixelDP(100) + menuBounds.top / 2
		        && y[0] < height / 2 - pixelDP(100) - menuBounds.bottom) tavla.drawText(
		        menuText[0], width / 2 - radMenu[0], height / 2 - pixelDP(100), menuCyanLarge);
		else tavla.drawText(menuText[0], width / 2 - radMenu[0], height / 2 - pixelDP(100),
		        menuWhiteLarge);

		if (x[0] > width / 2 - radMenu[0] && x[0] < width / 2 + radMenu[0]
		        && y[0] > height / 2 + menuBounds.top / 2 && y[0] < height / 2 - menuBounds.bottom) {
			tavla.drawText(menuText[1], width / 2 - radMenu[1], height / 2, menuCyanLarge);
			tavla.drawText("(unavailable)", width / 2, height / 2 + pixelDP(20), textPaint);
		}
		else tavla.drawText(menuText[1], width / 2 - radMenu[1], height / 2, menuWhiteLarge);

		if (x[0] > width / 2 - radMenu[0] && x[0] < width / 2 + radMenu[0]
		        && y[0] > height / 2 + pixelDP(100) + menuBounds.top / 2
		        && y[0] < height / 2 + pixelDP(100) - menuBounds.bottom) tavla.drawText(
		        menuText[2], width / 2 - radMenu[2], height / 2 + pixelDP(100), menuCyanLarge);

		else tavla.drawText(menuText[2], width / 2 - radMenu[2], height / 2 + pixelDP(100),
		        menuWhiteLarge);

	}

	private void speedReduction() {
		Thread speedReduction = new Thread() {
			public void run() {
				try {
					speedBox[0] = speedBox[0] / 16;
					sleep(100);
					speedBox[0] = speedBox[0] * 2;
					sleep(50);
					speedBox[0] = speedBox[0] * 2;
					sleep(35);
					speedBox[0] = speedBox[0] * 2;
					sleep(15);
					speedBox[0] = speedBox[0] * 2;
				} catch (Exception e) {}
			}
		};
		speedReduction.setPriority(1);
		speedReduction.start();
	}

	private void levelUP() {
		if (frame % 1000 == 0) {
			level++;
			if (speedGlobal < 10) {
				speedGlobal += 2;
				if (speedBad > 101) speedBad -= 30;
			}
			if (level > 2 && badChance > 6) badChance -= 4;
			if (level > 5) badChance = 1;
			;
		}
	}

	private float collide(float x, float y, float rad) {
		if (Math.pow(powup[0] - x, 2) + Math.pow(powup[1] - y, 2) < Math.pow(radPower + rad, 2)) return 13371337;

		for (int i = 0; i < food.length; i++)
			if (food[i][0] != 13371337) if (Math.pow(food[i][0] - x, 2)
			        + Math.pow(food[i][1] - y, 2) < Math.pow(radFood + rad, 2)) return 13371337;

		for (int i = 0; i < speed.length; i++)
			if (speed[i][0] != 13371337) if (Math.pow(speed[i][0] - x, 2)
			        + Math.pow(speed[i][1] - y, 2) < Math.pow(radSpeed + rad, 2)) return 13371337;

		for (int i = 0; i < bad.length; i++)
			if (bad[i][0] != 13371337) if (Math.pow(bad[i][0] - x, 2) + Math.pow(bad[i][1] - y, 2) < Math
			        .pow(radBad + rad, 2)) return 13371337;

		return x;
	}

	private void showSpeed() {
		String speedText = "Speed: x" + (speedBox[1]);
		tavla.drawText(speedText, 25, 25, textPaint);
	}

	private void showLevel() {
		String levelText = "Level: " + level;
		tavla.drawText(levelText, (width - textPaint.measureText(levelText)) / 2, 25, textPaint);
	}

	private void showScore() {
		score[1] = score[1] + (score[0] - score[1]) * (float) 0.25;
		String scoreText = "Score: " + (Math.round(score[1]));
		tavla.drawText(scoreText, width - textPaint.measureText(scoreText) - 25, 25, textPaint);
	}

	private void showFPS() {
		if (showFPS) {
			frametime[1] = frametime[0];
			frametime[0] = android.os.SystemClock.elapsedRealtime();
			frametime[2] = frametime[0] - frametime[1];

			String fpsText = "FPS: " + ((int) ((float) 1000 / (float) frametime[2]));
			tavla.drawText(fpsText, (width - textPaint.measureText(fpsText)) / 2, height - 50,
			        textPaint);
		}
	}

	private void drawLittleFood() {
		boolean create = true;
		for (int i = 0; i < food.length; i++)
			if (food[i][0] != 13371337) {
				if (Math.pow(food[i][0] - x[0], 2) + Math.pow(food[i][1] - y[0], 2) < Math.pow(
				        radFood + radBox, 2)) consumeFood(i);

				if (dyingFoodRelated[i] != 0) consumingFood(i);
				else keepCalmLittleFood(i);
			}
			else
				if (frame % (7 - ((int) (speedGlobal - 3) / 10)) == 0 && create) {
					if (rand.nextInt(3) < 1) createFood(i);
					create = false;
				}
	}

	private void keepCalmLittleFood(int i) {
		if (food[i][1] < height + radFood) {
			tavla.drawBitmap(littleFood, food[i][0] - radFood, food[i][1] - radFood, null);
			if (!dyingBox) {

				if (Math.pow(food[i][0] - x[0], 2) + Math.pow(food[i][1] - y[0], 2) < Math.pow(
				        radFood + radBox * 6, 2)) {

					food[i][0] = food[i][0] + (x[0] - food[i][0]) * (float) 1
					        / (Math.abs(x[0] - food[i][0]) + 1);
					food[i][1] = food[i][1] + (y[0] - food[i][1]) * (float) 1
					        / (Math.abs(y[0] - food[i][1]) + 1);
				}
				food[i][1] += food[i][2];
			}
		}
		else food[i][0] = 13371337;
	}

	private void consumingFood(int i) {
		if (frame - dyingFoodRelated[i] < 5) {
			tavla.drawBitmap(littleDyingFood[(frame - dyingFoodRelated[i]) % 5], food[i][0]
			        - radFood, food[i][1] - radFood, null);
			food[i][1] += food[i][2];
		}
		else {
			food[i][0] = 13371337;
			dyingFoodRelated[i] = 0;
		}
	}

	private float findSign(float f) {
		if (f < 0) return -1;
		else
			if (f == 0) return 0;
		return 1;
	}

	private float devideByZero(float num) {
		if (num == 0) return 0;
		return 1 / num;
	}

	private void consumeFood(int i) {
		if (dyingFoodRelated[i] == 0) {
			score(5);
			dyingFoodRelated[i] = frame;
			sp.play(foodPop[frame % foodPop.length], 1, 1, 0, 0, 1);
		}
	}

	private void createFood(final int i) {
		Thread createFood = new Thread() {
			public void run() {
				food[i][1] = 0 - radFood;
				food[i][0] = collide((float) rand.nextInt((width - (int) radFood * 2) * 100) / 100
				        + radFood, food[i][1], radFood);
				food[i][2] = rand.nextInt(3) + speedGlobal;
				if (rand.nextInt(10) < 2) for (int j = 0; j < food.length; j++)
					if (food[j][0] == 13371337 && rand.nextInt(3) < 1) {
						createFood(j);
						break;
					}
			}
		};
		createFood.setPriority(1);
		createFood.start();
	}

	private void drawLittleSpeed() {
		boolean create = true;
		for (int i = 0; i < speed.length; i++)
			if (speed[i][0] != 13371337) {
				if (Math.pow(speed[i][0] - x[0], 2) + Math.pow(speed[i][1] - y[0], 2) < Math.pow(
				        radSpeed + radBox, 2)) consumeSpeed(i);

				if (dyingSpeedRelated[i] != 0) consumingSpeed(i);
				else
					if (speed[i][1] < height + radSpeed) {
						tavla.drawBitmap(littleSpeed[(int) speed[i][3]][frame / 3 % 6], speed[i][0]
						        - radSpeed, speed[i][1] - radSpeed, null);
						if (!dyingBox) speed[i][1] += speed[i][2];
					}
					else speed[i][0] = 13371337;
			}
			else
				if (frame % (319 - ((int) (speedGlobal - 3) / 10)) == 0 && create) {
					if (rand.nextInt(3) < 2) createSpeed(i);
					create = false;
				}
	}

	private void consumingSpeed(int i) {
		if (frame - dyingSpeedRelated[i] < determineSpeed(i) * 3) {
			speed[i][0] = speed[i][0] + (x[0] - speed[i][0]) * dyingSpeedSpeed[i];
			speed[i][1] = speed[i][1] + (y[0] - speed[i][1]) * dyingSpeedSpeed[i];

			tavla.drawBitmap(littleDyingSpeed[(int) speed[i][3]][(frame - dyingSpeedRelated[i]) / 3
			        % determineSpeed(i)], speed[i][0] - radSpeed, speed[i][1] - radSpeed, null);

			if (dyingSpeedSpeed[i] < 1) dyingSpeedSpeed[i] += (float) 0.1;
		}
		else {
			speed[i][0] = 13371337;
			dyingSpeedRelated[i] = 0;
			dyingSpeedSpeed[i] = (float) 0.1;
		}
	}

	private int determineSpeed(int i) {
		if (speed[i][3] == 0) return 13;
		else return 11;
	}

	private void createSpeed(final int i) {
		Thread createSpeed = new Thread() {
			public void run() {
				speed[i][1] = 0 - radSpeed;
				speed[i][0] = collide((float) rand.nextInt((width - (int) radSpeed * 2) * 100)
				        / 100 + radSpeed, speed[i][1], radSpeed);
				speed[i][2] = rand.nextInt(3) + speedGlobal;
				speed[i][3] = rand.nextInt(2);
				if (rand.nextInt(10) < 2) for (int j = 0; j < speed.length; j++)
					if (speed[j][0] == 13371337 && rand.nextInt(3) < 2) {
						createSpeed(j);
						break;
					}
			}
		};
		createSpeed.setPriority(1);
		createSpeed.start();
	}

	private void consumeSpeed(int i) {
		if (dyingSpeedRelated[i] == 0) {
			dyingSpeedRelated[i] = frame;
			score(30);
			sp.play(dyingSpeedSound[(int) speed[i][3]][frame
			        % dyingSpeedSound[(int) speed[i][3]].length], 1, 1, 0, 0, 1);
			if (speed[i][3] == 0) {
				if (speedBox[0] > 0.25) speedBox[1]--;
				else speedBox[1] = speedBox[1] / 2;
				speedBox[0] = speedBox[0] / (float) 1.5;
			}
			else
				if (speedBox[0] < 1.8) {
					if (speedBox[0] > 0.25) speedBox[1]++;
					else speedBox[1] = speedBox[1] * 2;
					speedBox[0] = speedBox[0] * (float) 1.5;
				}
		}
	}

	private void drawLittlePower() {
		if (powup[0] != 13371337) {
			if (Math.pow(powup[0] - x[0], 2) + Math.pow(powup[1] - y[0], 2) < Math.pow(radPower
			        + radBox, 2)) consumePower();

			if (dyingPowerRelated == 1) consumingPower();
			else
				if (powup[1] < height + radPower) {
					tavla.drawBitmap(littlePower[frame / 4 % 6], powup[0] - radPower, powup[1]
					        - radPower, null);
					if (!dyingBox) powup[1] += powup[2];
				}
				else powup[0] = 13371337;
		}
		else
			if (frame % (907 - ((int) (speedGlobal - 3) / 10)) == 0) {
				if (rand.nextInt(3) < 1) createPower();
			}

	}

	private void consumingPower() {
		Thread thread = new Thread() {
			public void run() {

				if (alphaPower.getAlpha() > 0) {
					alphaPower.setAlpha(alphaPower.getAlpha() - 5);

					powup[0] = powup[0] + (x[0] - powup[0]) * dyingPowerSpeed;
					powup[1] = powup[1] + (y[0] - powup[1]) * dyingPowerSpeed;

					tavla.drawBitmap(littlePower[frame / 4 % 6], powup[0] - radPower, powup[1]
					        - radPower, alphaPower);

					if (dyingPowerSpeed < 1) dyingPowerSpeed += (float) 0.1;
				}

				else {
					if (empowered) score(20);
					else {
						empowered = true;
						littleBox = BitmapFactory.decodeResource(getContext().getResources(),
						        R.drawable.littleboxe);
						sp.play(dyingPowerSound[frame % dyingPowerSound.length], 1, 1, 0, 0, 1);
					}
					dyingPowerRelated = 0;
					dyingPowerSpeed = (float) 0.1;
					powup[0] = 13371337;
					alphaPower.setAlpha(255);
				}

			}
		};
		thread.setPriority(1);
		thread.start();
	}

	private void createPower() {
		Thread createPower = new Thread() {
			public void run() {
				powup[1] = 0 - radPower;
				powup[0] = collide((float) rand.nextInt((width - (int) radPower * 2) * 100) / 100
				        + radPower, powup[1], radPower);
				powup[2] = rand.nextInt(3) + speedGlobal;

			}
		};
		createPower.setPriority(1);
		createPower.start();
	}

	private void drawLittleBad() {
		boolean create = true;
		for (int i = 0; i < bad.length; i++)
			if (bad[i][0] != 13371337) {
				if (!dyingBad[i]) {
					if (Math.pow(bad[i][0] - x[0], 2) + Math.pow(bad[i][1] - y[0], 2) < Math.pow(
					        radBad + radBox, 2)) interactBad(i);
					else
						if (bad[i][1] < height + radBad) {
							if (dyingBoxRelated[1] != i) {
								if (Math.pow(bad[i][0] - x[0], 2) + Math.pow(bad[i][1] - y[0], 2) < Math
								        .pow(radBad * 4 + radBox, 2)
								        && bad[i][1] - radBad < y[0]
								        && !dyingBox) pursueLittleBox(i);
								else keepCalmAndCarryOn(i);
							}
						}
						else bad[i][0] = 13371337;
				}
				else badIsDying(i);

			}
			else
				if (frame % badChance == 0 && create) {
					if (rand.nextInt(3) < 1) createBad(i);
					create = false;
				}
	}

	private void keepCalmAndCarryOn(int i) {
		if (!dyingBox) bad[i][1] += bad[i][2];
		tavla.drawBitmap(littleBad[1], bad[i][0] - radBad, bad[i][1] - radBad, null);
	}

	private void pursueLittleBox(final int i) {
		bad[i][0] = bad[i][0] + (x[0] - bad[i][0]) * (bad[i][2] / speedBad);
		bad[i][1] = bad[i][1] + +(y[0] - bad[i][1]) * (bad[i][2] / speedBad);
		tavla.drawBitmap(littleBad[(frame / 5) % 2], bad[i][0] - radBad, bad[i][1] - radBad, null);
		if (!pursuingBad[i]) {
			Thread badBark = new Thread() {
				public void run() {
					while ((Math.pow(bad[i][0] - x[0], 2) + Math.pow(bad[i][1] - y[0], 2) < Math
					        .pow(radBad * 4 + radBox, 2) && bad[i][1] - radBad < y[0] && !dyingBox && running))
						try {
							sp.play(badBarking[(frame + i) % 10], (float) 0.5, (float) 0.5, 1, 0,
							        (float) 1);

							Thread.sleep(300);
						} catch (InterruptedException e) {}
					pursuingBad[i] = false;
				}
			};
			pursuingBad[i] = true;
			badBark.setPriority(2);
			badBark.start();
		}

	}

	private void badIsDying(int i) {
		if (frame - dyingBadRelated[i] - 6 < littleDyingBad.length) {
			if (!dyingBox) bad[i][1] += bad[i][2];
			if (frame - dyingBadRelated[i] < 6) tavla.drawBitmap(littleDyingBad[0], bad[i][0]
			        - radDyingBad, bad[i][1] - radDyingBad, null);
			else tavla.drawBitmap(littleDyingBad[frame % (dyingBadRelated[i] + 6)], bad[i][0]
			        - radDyingBad, bad[i][1] - radDyingBad, null);
		}
		else {
			bad[i][0] = 13371337;
			dyingBad[i] = false;
		}
	}

	private void interactBad(final int i) {
		if (bad[i][1] - radBad * .6 >= y[1]) {
			if (intersectLineCircle(x[1], y[1], x[0], y[0], bad[i][0], bad[i][1], radBad)) {
				speedReduction();
				dieLittleBad(i);
			}
		}
		else
			if (empowered) {
				empowered = false;

				Thread depower = new Thread() {
					public void run() {
						speedReduction();
						dieLittleBad(i);
						littleBox = BitmapFactory.decodeResource(getContext().getResources(),
						        R.drawable.littlebox);
					}
				};
				depower.setPriority(1);
				depower.start();
			}
			else dieLittleBox(i);

		// TODO

	}

	private boolean intersectLineCircle(float x2, float y2, float x1, float y1, float cx, float cy,
	        float rad) {
		float l = (y2 - y1) / (x2 - x1);
		float D = (float) (Math.pow(2 * l * y1 - 2 * cx - 2 * x1 * Math.pow(l, 2) - 2 * l * cy, 2) - 4
		        * (Math.pow(l, 2) + 1)
		        * (Math.pow(l * x1 - y1, 2) + Math.pow(cx, 2) + (2 * l * x1 - 2 * y1 + 1) * cy - Math
		                .pow(rad, 2)));
		if (D >= 0) return true;
		return false;
	}

	private void dieLittleBox(final int i) {
		if (!dyingBox) {
			dyingBoxRelated[0] = frame;
			dyingBoxRelated[1] = i;

			if (saves.read("Score") < score[0]) saves.write("Score", (int) score[0]);
			sp.play(dyingBoxSound, 1, 1, 1, 0, 1);

			dyingBox = true;
		}
	}

	private void dieLittleBad(int i) {
		if (!dyingBad[i]) {
			dyingBad[i] = true;
			dyingBadRelated[i] = frame;
			score(50);
			Thread badDeath = new Thread() {
				public void run() {
					try {
						sp.play(dyingBadSound[frame % dyingBadSound.length], 1, 1, 0, 0, (float) 1);

						Thread.sleep(300);
						sp.play(explo[frame % explo.length], 1, 1, 0, 0, (float) 1);
					} catch (Exception e) {}
				}
			};
			badDeath.setPriority(1);
			badDeath.start();
		}
	}

	private void score(int points) {
		score[0] += points * (level + 1);
	}

	private void createBad(final int i) {
		Thread createBad = new Thread() {
			public void run() {
				bad[i][1] = 0 - radBad;
				bad[i][0] = collide((float) rand.nextInt((width - (int) radBad * 2) * 100) / 100
				        + radBad, bad[i][1], radBad);
				bad[i][2] = rand.nextInt(3) + speedGlobal;
				if (rand.nextInt(10) < 2) for (int j = 0; j < bad.length; j++)
					if (bad[j][0] == 13371337 && rand.nextInt(3) < 1) {
						createBad(j);
						break;
					}

			}
		};
		createBad.setPriority(1);
		createBad.start();
	}

	private void drawLittleStar() {
		boolean create = true;
		for (int i = 0; i < star.length; i++)
			if (star[i][0] != 13371337) {
				if (star[i][1] < height + radStar) {
					tavla.drawBitmap(littleStar, star[i][0] - radStar, star[i][1] - radStar,
					        alpha230);
					if (!dyingBox) star[i][1] += speedGlobal;
				}
				else star[i][0] = 13371337;
			}
			else
				if (!dyingBox) if (frame % (12 - ((int) (speedGlobal - 3) / 10)) == 0 && create) {
					if (rand.nextInt(3) < 2) createStar(i);
					create = false;
				}
	}

	private void createStar(final int i) {
		Thread createStar = new Thread() {
			public void run() {
				star[i][0] = (float) rand.nextInt(width * 100) / 100;
				star[i][1] = 0 - radStar;
				if (rand.nextInt(10) < 1) for (int j = 0; j < star.length; j++)
					if (star[j][0] == 13371337 && rand.nextInt(3) < 2) {
						createStar(j);
						break;
					}
			}
		};
		createStar.setPriority(1);
		createStar.start();
	}

	private void consumePower() {
		dyingPowerRelated = 1;
	}

	private void drawLittleBox() {
		if (!dyingBox) {
			for (int i = 0; i < 5; i++)
				tavla.drawBitmap(littleLight[i], x[i * 2 + 1] - littleLight[i].getWidth() / 2,
				        y[i * 2 + 1] - littleLight[i].getHeight() / 2, alpha60);
			tavla.drawBitmap(littleBox, x[0] - radBox, y[0] - radBox, null);
		}
		else {
			System.out.println(frame + ", " + ((frame / 10) % 2) + ", " + dyingBoxRelated[1] + ", "
			        + (bad[dyingBoxRelated[1]][0] - radBad) + ", "
			        + (bad[dyingBoxRelated[1]][1] - radBad) + ", " + radBad + ", "
			        + littleBad[(frame / 10) % 2]);
			try {
				tavla.drawBitmap(littleBad[(frame / 10) % 2], bad[dyingBoxRelated[1]][0] - radBad,
				        bad[dyingBoxRelated[1]][1] - radBad, null);
			} catch (Exception e) {
				System.out.println(e);
			}
			if (frame - dyingBoxRelated[0] < 9) {
				alpha60.setAlpha(alpha60.getAlpha() - 6);
				for (int i = 0; i < 5; i++)
					tavla.drawBitmap(littleLight[i], x[i * 2 + 1] - littleLight[i].getWidth() / 2,
					        y[i * 2 + 1] - littleLight[i].getHeight() / 2, alpha60);

				tavla.drawBitmap(littleDyingBox[frame % dyingBoxRelated[0]], x[0] - radDyingBox,
				        y[0] - radDyingBox, null);
			}
		}
	}

	private void drawBG() {
		tavla.drawARGB(255, 0, 0, 0);
	}

	protected void movementLog(int loop) {
		if (!dyingBox) {
			for (int i = 9; i > 0; i--) {
				x[i] = x[i - 1];
				y[i] = y[i - 1];
			}
			int temp = 10;
			if (!gaming) temp = 11;
			for (int i = 0; i <= loop; i++) {
				x[0] = x[0] + (x[temp] - x[0]) * speedBox[0];
				y[0] = y[0] + (y[temp] - y[0]) * speedBox[0];
			}
		}
	}

	public GameView(Context context) {
		super(context);
		this.context = context;
		saves = new Saves(context);
		surfaceHolder = getHolder();
		surfaceHolder.setFormat(PixelFormat.TRANSPARENT);

		dyingBoxSound = sp.load(context, dyingBoxSound, 1);

		for (int i = 0; i < dyingSpeedSound.length; i++)
			for (int j = 0; j < dyingSpeedSound[i].length; j++)
				dyingSpeedSound[i][j] = sp.load(context, dyingSpeedSound[i][j], 1);
		for (int i = 0; i < dyingPowerSound.length; i++)
			dyingPowerSound[i] = sp.load(context, dyingPowerSound[i], 1);
		for (int i = 0; i < dyingBadSound.length; i++)
			dyingBadSound[i] = sp.load(context, dyingBadSound[i], 1);
		for (int i = 0; i < badBarking.length; i++)
			badBarking[i] = sp.load(context, badBarking[i], 1);
		for (int i = 0; i < foodPop.length; i++)
			foodPop[i] = sp.load(context, foodPop[i], 1);
		for (int i = 0; i < explo.length; i++)
			explo[i] = sp.load(context, explo[i], 1);

	}

	public void onGameResume() {
		running = true;
		viewLoop = new Thread(this);
		viewLoop.start();
	}

	public void onGamePause() {
		running = false;

	}

	public void setTouch(float coordX, float coordY) {
		if (gaming) {
			x[10] = coordX;
			y[10] = coordY;
		}
		else {
			x[11] = coordX;
			y[11] = coordY;
		}
	}

	public void setTouchUP() {
		if (x[11] > width / 2 - radMenu[0] && x[11] < width / 2 + radMenu[0]
		        && y[11] > height / 2 - pixelDP(100) + menuBounds.top / 2
		        && y[11] < height / 2 - pixelDP(100) - menuBounds.bottom) {
			menuing = false;
			gaming = true;
			movementLog(10);
		}
		else
			if (x[11] > width / 2 - radMenu[0] && x[11] < width / 2 + radMenu[0]
			        && y[11] > height / 2 + menuBounds.top / 2
			        && y[11] < height / 2 - menuBounds.bottom) {
				// menuing = false;
				// optioning = true;
			}
			else
				if (x[11] > width / 2 - radMenu[0] && x[11] < width / 2 + radMenu[0]
				        && y[11] > height / 2 + pixelDP(100) + menuBounds.top / 2
				        && y[11] < height / 2 + pixelDP(100) - menuBounds.bottom) {
					((Activity) context).finish();
				}
	}

	public int pixelDP(int dp) {
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources()
		        .getDisplayMetrics());
		return px;
	}

	public void reset() {
		x[11] = -200;
		y[11] = -200;
		movementLog(10);
		gaming = false;
		menuing = true;
		if (dyingBox) tavlaInit(true);
	}
}
