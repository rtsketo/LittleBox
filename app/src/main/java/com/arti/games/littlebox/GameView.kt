package com.arti.games.littlebox

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.media.AudioManager
import android.media.SoundPool
import android.util.TypedValue
import android.view.SurfaceView
import java.util.*
import kotlin.math.pow

class GameView(context: Context) : SurfaceView(context), Runnable {
    private lateinit var viewLoop: Thread
    private lateinit var tavla: Canvas
    private val surfaceHolder = holder
    private val saves = Saves(context)

    private val rand = Random()
    private val menuBounds = Rect()
    private val sp = SoundPool(10, AudioManager.STREAM_MUSIC, 0)

    private lateinit var littleBox: Bitmap
    private lateinit var littleStar: Bitmap
    private lateinit var littleFood: Bitmap

    private val littleBad = arrayOfNulls<Bitmap>(2)
    private val littlePower = arrayOfNulls<Bitmap>(6)
    private val littleLight = arrayOfNulls<Bitmap>(5)
    private val littleDyingBox = arrayOfNulls<Bitmap>(9)
    private val littleDyingFood = arrayOfNulls<Bitmap>(5)
    private val littleDyingBad = arrayOfNulls<Bitmap>(11)
    private val littleSpeed = Array(2) { arrayOfNulls<Bitmap>(6) }
    private val littleDyingSpeed = Array(2) { arrayOfNulls<Bitmap>(13) }

    private val x = DoubleArray(12)
    private val y = DoubleArray(12)
    private val powup = DoubleArray(3)
    private val score = DoubleArray(2)
    private val radMenu = DoubleArray(4)
    private val speedBox = DoubleArray(2)

    private val star = Array(50) { DoubleArray(2) }
    private val food = Array(20) { DoubleArray(3) }
    private val bad = Array(10) { DoubleArray(3) }
    private val speed = Array(5) { DoubleArray(4) }


    private var radBox = .0
    private var radBad = .0
    private var radStar = .0
    private var radFood = .0
    private var radSpeed = .0
    private var radPower = .0
    private var radLight = .0
    private var radDyingBox = .0
    private var radDyingBad = .0
    private var dyingPowerSpeed = .0
    private var dyingSpeedSpeed = DoubleArray(5)

    private val alpha60 = Paint()
    private val alpha230 = Paint()
    private val alphaPower = Paint()
    private val textPaint = Paint()
    private val menuScore = Paint()
    private val menuWhiteLarge = Paint()
    private val menuCyanLarge = Paint()
    private var menuText = arrayOfNulls<String>(3)

    private var level = 0
    private var cHeight = 0
    private var cWidth = 0

    private var frame = 0
    private var speedGlobal = 0
    private var badChance = 0
    private var speedBad = 0

    private lateinit var dyingSpeedRelated: IntArray
    private lateinit var dyingFoodRelated: IntArray
    private lateinit var dyingBoxRelated: IntArray
    private lateinit var dyingBadRelated: IntArray
    private var frametime = LongArray(3)
    private var dyingPowerRelated = 0

    private var showFPS = false
    @Volatile internal var running = true
    @Volatile internal var gaming = false
    @Volatile internal var menuing = true
    @Volatile internal var firstTime = true
    @Volatile internal var dyingBox = false
    @Volatile internal var optioning = false
    @Volatile internal var empowered = false
    @Volatile internal var dyingBad = BooleanArray(10)
    @Volatile internal var pursuingBad = BooleanArray(10)

    fun tavlaInit(firstTime: Boolean) {
        this.firstTime = firstTime
        tavlaInit()
    }

    private fun tavlaInit() {
        if (firstTime) {

            level = 0
            frame = 1
            speedGlobal = 3
            badChance = 14
            speedBad = 190
            score[0] = 0.0
            speedBox[1] = 1.0
            speedBox[0] = .25

            dyingPowerSpeed = 0.1
            for (i in 0..4) dyingSpeedSpeed[i] = 0.1

            dyingPowerRelated = 0
            dyingSpeedRelated = IntArray(5)
            dyingFoodRelated = IntArray(20)
            dyingBoxRelated = IntArray(3)
            dyingBadRelated = IntArray(10)

            dyingBox = false

            alpha60.alpha = 60
            alpha230.alpha = 230
            alphaPower.alpha = 255

            textPaint.color = Color.WHITE
            textPaint.textSize = pixelDP(16)

            menuScore.color = Color.WHITE
            menuCyanLarge.color = Color.CYAN
            menuWhiteLarge.color = Color.WHITE
            menuScore.textSize = pixelDP(50)
            menuCyanLarge.textSize = pixelDP(50)
            menuWhiteLarge.textSize = pixelDP(50)

            menuText[0] = "Start"
            menuText[1] = "Options"
            menuText[2] = "Exit"

            for (i in menuText.indices)
                radMenu[i] = menuWhiteLarge.measureText(menuText[i]) / 2.0

            menuWhiteLarge.getTextBounds("SOE", 0, 1, menuBounds)

            cWidth = tavla.width
            cHeight = tavla.height

            for (i in littleDyingBox.indices)
                littleDyingBox[i] = createBitmap(dyingBoxFrame[i])
            for (i in littleDyingBad.indices)
                littleDyingBad[i] = createBitmap(dyingBadFrame[i])

            for (i in dyingBad.indices) dyingBad[i] = false
            for (i in pursuingBad.indices) pursuingBad[i] = false

            littleStar = createBitmap(R.drawable.littlestar)
            littleFood = createBitmap(R.drawable.spheregrey)
            littleBox = createBitmap(R.drawable.littlebox)
            littleBad[0] = createBitmap(R.drawable.bad01)
            littleBad[1] = createBitmap(R.drawable.bad02)

            for (i in 0..12) {
                if (i < 11)
                    littleDyingSpeed[1][i] = createBitmap(speedUPDying[i])
                littleDyingSpeed[0][i] = createBitmap(speedDownDying[i])
            }

            for (i in 0..4)
                littleDyingFood[i] = createBitmap(foodConsume[i])

            for (i in 0..5) littlePower[i] = createBitmap(power[i])

            for (i in 0..5) {
                littleSpeed[1][i] = createBitmap(speedUP[i])
                littleSpeed[0][i] = createBitmap(speedDown[i])
            }

            radBox = littleBox.height / 2.0
            radFood = littleFood.height / 2.0
            radBad = littleBad[0]!!.height / 2.0
            radPower = littlePower[0]!!.height / 2.0
            radSpeed = littleSpeed[0][0]!!.height / 2.0
            radDyingBad = littleDyingBad[0]!!.height / 2.0
            radDyingBox = littleDyingBox[0]!!.height / 2.0
            radLight = createBitmap(R.drawable.light).height / 4.0

            powup[0] = 13371337.0
            for (i in speed.indices) speed[i][0] = 13371337.0
            for (i in food.indices) food[i][0] = 13371337.0
            for (i in bad.indices) bad[i][0] = 13371337.0
            for (i in 0..4)
                littleLight[i] = Bitmap
                        .createScaledBitmap(createBitmap(R.drawable.light),
                                (radLight - i * radLight / 5).toInt() * 2,
                                (radLight - i * radLight / 5).toInt() * 2, false)

            for (i in star.indices) {
                star[i][0] = rand.nextInt(width * 2).toDouble()
                star[i][1] = rand.nextInt(height + 1).toDouble()
            }

            for (i in 0..11) {
                x[i] = (width / 2).toDouble()
                y[i] = (height * 5 / 6).toDouble()
            }

            sp.play(dyingBoxSound, 0f, 0f, 0, 0, 2f)
            for (i in dyingSpeedSound.indices)
                for (j in 0 until dyingSpeedSound[i].size)
                    sp.play(dyingSpeedSound[i][j], 0f, 0f, 0, 0, 2f)
            for (i in dyingPowerSound.indices)
                sp.play(dyingPowerSound[i], 0f, 0f, 0, 0, 2f)
            for (i in dyingBadSound.indices)
                sp.play(dyingBadSound[i], 0f, 0f, 0, 0, 2f)
            for (i in badBarking.indices)
                sp.play(badBarking[i], 0f, 0f, 0, 0, 2f)
            for (i in foodPop.indices)
                sp.play(foodPop[i], 0f, 0f, 0, 0, 2f)
            for (i in explo.indices)
                sp.play(explo[i], 0f, 0f, 0, 0, 2f)

            firstTime = false
        }
    }

    override fun run() {
        while (running) {
            if (surfaceHolder.surface.isValid)
                try {
                    tavla = surfaceHolder.lockCanvas()

                    if (menuing) {
                        tavlaInit()
                        movementLog(0)

                        drawBG()
                        drawLittleStar()
                        drawLittleBox()
                        drawMainMenu()
                    } else if (gaming) {
                        tavlaInit()
                        movementLog(0)

                        drawBG()
                        drawLittleStar()
                        drawLittleFood()
                        drawLittlePower()
                        drawLittleSpeed()
                        drawLittleBox()
                        drawLittleBad()

                        showFPS()
                        showSpeed()
                        showLevel()
                        showScore()

                        levelUP()
                        drawEnding()
                    } else if (optioning) {
                    }

                    surfaceHolder.unlockCanvasAndPost(tavla)
                    frame++
                    Thread.sleep(30)
                } catch (e: InterruptedException) {
                    println(e)
                }

        }
    }

    private fun drawEnding() {
        if (dyingBox) {
            if (menuScore.alpha < 255)
                menuScore.alpha = menuScore.alpha + 1
            else
                menuScore.alpha = 0
            tavla.drawText("Game Over!", width / 2 - menuScore.measureText("Game Over!") / 2,
                    height / 2f, menuScore)
            tavla.drawText("Score: " + score[0].toInt(),
                    width / 2 - menuScore.measureText("Score: " + score[0].toInt()) / 2, height / 2 + pixelDP(70), menuScore)
            if (frame >= dyingBoxRelated[0] + 166) reset()
        }
    }

    private fun drawMainMenu() {
        val scoreText = "Score: " + saves.read("Score")
        tavla.drawText(scoreText, width / 2 - textPaint.measureText(scoreText) / 2, height / 2 + pixelDP(200), textPaint)

        // tavla.drawRect(width/2-radMenu[0], height/2-
        // pixelDP(100)+ menuBounds.top, width/2+radMenu[0],
        // height/2- pixelDP(100)-menuBounds.bottom, menuPaint);

        if (x[0] > width / 2 - radMenu[0] && x[0] < width / 2 + radMenu[0]
                && y[0] > height / 2 - pixelDP(100) + menuBounds.top / 2
                && y[0] < height / 2 - pixelDP(100) - menuBounds.bottom)
            tavla.drawText(
                    menuText[0]!!, width / 2 - radMenu[0].toFloat(),
                    height / 2 - pixelDP(100), menuCyanLarge)
        else
            tavla.drawText(menuText[0]!!, width / 2 - radMenu[0].toFloat(),
                    height / 2 - pixelDP(100), menuWhiteLarge)

        if (x[0] > width / 2 - radMenu[0] && x[0] < width / 2 + radMenu[0]
                && y[0] > height / 2 + menuBounds.top / 2 && y[0] < height / 2 - menuBounds.bottom) {
            tavla.drawText(menuText[1]!!, width / 2 - radMenu[1].toFloat(), height / 2f, menuCyanLarge)
            tavla.drawText("(unavailable)", width / 2f,
                    height / 2 + pixelDP(20), textPaint)
        } else
            tavla.drawText(menuText[1]!!, width / 2 - radMenu[1].toFloat(), height / 2f, menuWhiteLarge)

        if (x[0] > width / 2 - radMenu[0] && x[0] < width / 2 + radMenu[0]
                && y[0] > height / 2 + pixelDP(100) + menuBounds.top / 2
                && y[0] < height / 2 + pixelDP(100) - menuBounds.bottom)
            tavla.drawText(
                    menuText[2]!!, width / 2 - radMenu[2].toFloat(),
                    height / 2 + pixelDP(100), menuCyanLarge)
        else
            tavla.drawText(menuText[2]!!, width / 2 - radMenu[2].toFloat(),
                    height / 2 + pixelDP(100), menuWhiteLarge)

    }

    private fun speedReduction() {
        val speedReduction = object : Thread() {
            override fun run() {
                try {
                    speedBox[0] = speedBox[0] / 16
                    sleep(100)
                    speedBox[0] = speedBox[0] * 2
                    sleep(50)
                    speedBox[0] = speedBox[0] * 2
                    sleep(35)
                    speedBox[0] = speedBox[0] * 2
                    sleep(15)
                    speedBox[0] = speedBox[0] * 2
                } catch (e: Exception) {
                }

            }
        }
        speedReduction.priority = 1
        speedReduction.start()
    }

    private fun levelUP() {
        if (frame % 1000 == 0) {
            level++
            if (speedGlobal < 10) {
                speedGlobal += 2
                if (speedBad > 101) speedBad -= 30
            }
            if (level > 2 && badChance > 6) badChance -= 4
            if (level > 5) badChance = 1
        }
    }

    private fun collide(x: Double, y: Double, rad: Double): Double {
        if ((powup[0] - x).pow(2.0) + (powup[1] - y).pow(2.0)
                < (radPower + rad).pow(2.0)) return 13371337.0

        for (i in food.indices)
            if (food[i][0] != 13371337.0 && (food[i][0] - x).pow(2.0) +
                    (food[i][1] - y).pow(2.0) < (radFood + rad).pow(2.0)) return 13371337.0

        for (i in speed.indices)
            if (speed[i][0] != 13371337.0 && (speed[i][0] - x).pow(2.0) +
                    (speed[i][1] - y).pow(2.0) < (radSpeed + rad).pow(2.0))
                    return 13371337.0

        for (i in bad.indices) if (bad[i][0] != 13371337.0 &&
                (bad[i][0] - x).pow(2.0) + (bad[i][1] - y).pow(2.0)
                < (radBad + rad).pow(2.0)) return 13371337.0

        return x
    }

    private fun showSpeed() {
        val speedText = "Speed: x" + speedBox[1]
        tavla.drawText(speedText, 25f, 25f, textPaint)
    }

    private fun showLevel() {
        val levelText = "Level: $level"
        tavla.drawText(levelText, (width - textPaint.measureText(levelText)) / 2, 25f, textPaint)
    }

    private fun showScore() {
        score[1] = score[1] + (score[0] - score[1]) * 0.25.toDouble()
        val scoreText = "Score: " + Math.round(score[1])
        tavla.drawText(scoreText, width - textPaint.measureText(scoreText) - 25f, 25f, textPaint)
    }

    private fun showFPS() {
        if (showFPS) {
            frametime[1] = frametime[0]
            frametime[0] = android.os.SystemClock.elapsedRealtime()
            frametime[2] = frametime[0] - frametime[1]

            val fpsText = "FPS: " + (1000.toDouble() / frametime[2].toDouble()).toInt()
            tavla.drawText(fpsText, (width - textPaint.measureText(fpsText)) / 2,
                    height - 50f, textPaint)
        }
    }

    private fun drawLittleFood() {
        var create = true
        for (i in food.indices)
            if (food[i][0] != 13371337.0) {
                if (Math.pow((food[i][0] - x[0]), 2.0) + (food[i][1] - y[0]).pow(2.0) < (radFood + radBox).pow(2.0))
                    consumeFood(i)

                if (dyingFoodRelated[i] != 0)
                    consumingFood(i)
                else
                    keepCalmLittleFood(i)
            } else if (frame % (7 - (speedGlobal - 3) / 10) == 0 && create) {
                if (rand.nextInt(3) < 1) createFood(i)
                create = false
            }
    }

    private fun keepCalmLittleFood(i: Int) {
        if (food[i][1] < height + radFood) {
            tavla.drawBitmap(littleFood,
                    (food[i][0] - radFood).toFloat(),
                    (food[i][1] - radFood).toFloat(), null)

            if (!dyingBox) {
                if (Math.pow((food[i][0] - x[0]).toDouble(), 2.0) + Math.pow((food[i][1] - y[0]).toDouble(), 2.0) < Math.pow(
                                (radFood + radBox * 6).toDouble(), 2.0)) {

                    food[i][0] = food[i][0] + (x[0] - food[i][0]) * 1f / (Math.abs(x[0] - food[i][0]) + 1)
                    food[i][1] = food[i][1] + (y[0] - food[i][1]) * 1f / (Math.abs(y[0] - food[i][1]) + 1)
                }
                food[i][1] += food[i][2]
            }
        } else
            food[i][0] = 13371337.0
    }

    private fun consumingFood(i: Int) {
        if (frame - dyingFoodRelated[i] < 5) {
            tavla.drawBitmap(littleDyingFood[(frame - dyingFoodRelated[i]) % 5]!!,
                    (food[i][0] - radFood).toFloat(), (food[i][1] - radFood).toFloat(), null)
            food[i][1] += food[i][2]
        } else {
            food[i][0] = 13371337.0
            dyingFoodRelated[i] = 0
        }
    }

    private fun findSign(f: Float): Float {
        if (f < 0)
            return -1f
        else if (f == 0f) return 0f
        return 1f
    }

    private fun devideByZero(num: Float): Float {
        return if (num == 0f) 0f else 1 / num
    }

    private fun consumeFood(i: Int) {
        if (dyingFoodRelated[i] == 0) {
            score(5)
            dyingFoodRelated[i] = frame
            sp.play(foodPop[frame % foodPop.size], 1f, 1f, 0, 0, 1f)
        }
    }

    private fun createFood(i: Int) {
        val createFood = object : Thread() {
            override fun run() {
                food[i][1] = 0 - radFood
                food[i][0] = collide(rand.nextInt((width - radFood.toInt() * 2) * 100).toDouble() / 100 + radFood, food[i][1], radFood)
                food[i][2] = (rand.nextInt(3) + speedGlobal).toDouble()
                if (rand.nextInt(10) < 2)
                    for (j in food.indices)
                        if (food[j][0] == 13371337.0 && rand.nextInt(3) < 1) {
                            createFood(j)
                            break
                        }
            }
        }
        createFood.priority = 1
        createFood.start()
    }

    private fun drawLittleSpeed() {
        var create = true
        for (i in speed.indices)
            if (speed[i][0] != 13371337.0) {
                if ((speed[i][0] - x[0]).pow(2.0) +
                        (speed[i][1] - y[0]).pow(2.0)
                        < (radSpeed + radBox).pow(2.0))
                    consumeSpeed(i)

                if (dyingSpeedRelated[i] != 0)
                    consumingSpeed(i)
                else if (speed[i][1] < height + radSpeed) {
                    tavla.drawBitmap(littleSpeed[speed[i][3].toInt()][frame / 3 % 6]!!,
                            (speed[i][0] - radSpeed).toFloat(),
                            (speed[i][1] - radSpeed).toFloat(), null)
                    if (!dyingBox) speed[i][1] += speed[i][2]
                } else
                    speed[i][0] = 13371337.0
            } else if (frame % (319 - (speedGlobal - 3) / 10) == 0 && create) {
                if (rand.nextInt(3) < 2) createSpeed(i)
                create = false
            }
    }

    private fun consumingSpeed(i: Int) {
        if (frame - dyingSpeedRelated[i] < determineSpeed(i) * 3) {
            speed[i][0] = speed[i][0] + (x[0] - speed[i][0]) * dyingSpeedSpeed[i]
            speed[i][1] = speed[i][1] + (y[0] - speed[i][1]) * dyingSpeedSpeed[i]

            tavla.drawBitmap(littleDyingSpeed[speed[i][3].toInt()]
                    [(frame - dyingSpeedRelated[i]) / 3 % determineSpeed(i)]!!,
                    (speed[i][0] - radSpeed).toFloat(),
                    (speed[i][1] - radSpeed).toFloat(), null)

            if (dyingSpeedSpeed[i] < 1) dyingSpeedSpeed[i] += 0.1
        } else {
            speed[i][0] = 13371337.0
            dyingSpeedRelated[i] = 0
            dyingSpeedSpeed[i] = 0.1
        }
    }

    private fun determineSpeed(i: Int): Int {
        return if (speed[i][3] == .0)
            13
        else
            11
    }

    private fun createSpeed(i: Int) {
        val createSpeed = object : Thread() {
            override fun run() {
                speed[i][1] = 0 - radSpeed
                speed[i][0] = collide(rand.nextInt((width - radSpeed.toInt() * 2) * 100).toDouble() / 100 + radSpeed, speed[i][1], radSpeed)
                speed[i][2] = (rand.nextInt(3) + speedGlobal).toDouble()
                speed[i][3] = rand.nextInt(2).toDouble()
                if (rand.nextInt(10) < 2)
                    for (j in speed.indices)
                        if (speed[j][0] == 13371337.0 && rand.nextInt(3) < 2) {
                            createSpeed(j)
                            break
                        }
            }
        }
        createSpeed.priority = 1
        createSpeed.start()
    }

    private fun consumeSpeed(i: Int) {
        if (dyingSpeedRelated[i] == 0) {
            dyingSpeedRelated[i] = frame
            score(30)
            sp.play(dyingSpeedSound[speed[i][3].toInt()][frame % dyingSpeedSound[speed[i][3].toInt()].size], 1f, 1f, 0, 0, 1f)
            if (speed[i][3] == .0) {
                if (speedBox[0] > 0.25)
                    speedBox[1]--
                else
                    speedBox[1] = speedBox[1] / 2
                speedBox[0] = speedBox[0] / 1.5
            } else if (speedBox[0] < 1.8) {
                if (speedBox[0] > 0.25)
                    speedBox[1]++
                else
                    speedBox[1] = speedBox[1] * 2
                speedBox[0] = speedBox[0] * 1.5
            }
        }
    }

    private fun drawLittlePower() {
        if (powup[0] != 13371337.0) {
            if ((powup[0] - x[0]).pow(2.0) +
                    (powup[1] - y[0]).pow(2.0) < (radPower + radBox).pow(2.0))
                consumePower()

            if (dyingPowerRelated == 1)
                consumingPower()
            else if (powup[1] < height + radPower) {
                tavla.drawBitmap(littlePower[frame / 4 % 6]!!,
                        (powup[0] - radPower).toFloat(),
                        (powup[1] - radPower).toFloat(), null)
                if (!dyingBox) powup[1] += powup[2]
            } else
                powup[0] = 13371337.0
        } else if (frame % (907 - (speedGlobal - 3) / 10) == 0) {
            if (rand.nextInt(3) < 1) createPower()
        }

    }

    private fun consumingPower() {
        val thread = object : Thread() {
            override fun run() {

                if (alphaPower.alpha > 0) {
                    alphaPower.alpha = alphaPower.alpha - 5

                    powup[0] = powup[0] + (x[0] - powup[0]) * dyingPowerSpeed
                    powup[1] = powup[1] + (y[0] - powup[1]) * dyingPowerSpeed

                    tavla.drawBitmap(littlePower[frame / 4 % 6]!!,
                            (powup[0] - radPower).toFloat(),
                            (powup[1] - radPower).toFloat(), alphaPower)

                    if (dyingPowerSpeed < 1) dyingPowerSpeed += 0.1
                } else {
                    if (empowered)
                        score(20)
                    else {
                        empowered = true
                        littleBox = BitmapFactory.decodeResource(context.resources,
                                R.drawable.littleboxe)
                        sp.play(dyingPowerSound[frame % dyingPowerSound.size], 1f, 1f, 0, 0, 1f)
                    }
                    dyingPowerRelated = 0
                    dyingPowerSpeed = 0.1
                    powup[0] = 13371337.0
                    alphaPower.alpha = 255
                }

            }
        }
        thread.priority = 1
        thread.start()
    }

    private fun createPower() {
        val createPower = object : Thread() {
            override fun run() {
                powup[1] = 0 - radPower
                powup[0] = collide(rand.nextInt((width - radPower.toInt() * 2) * 100).toDouble() / 100 + radPower, powup[1], radPower)
                powup[2] = (rand.nextInt(3) + speedGlobal).toDouble()

            }
        }
        createPower.priority = 1
        createPower.start()
    }

    private fun drawLittleBad() {
        var create = true
        for (i in bad.indices)
            if (bad[i][0] != 13371337.0) {
                if (!dyingBad[i]) {
                    if ((bad[i][0] - x[0]).pow(2.0) +
                            (bad[i][1] - y[0]).pow(2.0) <
                            (radBad + radBox).pow(2.0))
                        interactBad(i)
                    else if (bad[i][1] < height + radBad) {
                        if (dyingBoxRelated[1] != i) {
                            if ((bad[i][0] - x[0]).pow(2.0) +
                                    (bad[i][1] - y[0]).pow(2.0) <
                                    (radBad * 4 + radBox).pow(2.0)
                                    && bad[i][1] - radBad < y[0]
                                    && !dyingBox)
                                pursueLittleBox(i)
                            else
                                keepCalmAndCarryOn(i)
                        }
                    } else
                        bad[i][0] = 13371337.0
                } else
                    badIsDying(i)

            } else if (frame % badChance == 0 && create) {
                if (rand.nextInt(3) < 1) createBad(i)
                create = false
            }
    }

    private fun keepCalmAndCarryOn(i: Int) {
        if (!dyingBox) bad[i][1] += bad[i][2]
        tavla.drawBitmap(littleBad[1]!!,
                (bad[i][0] - radBad).toFloat(),
                (bad[i][1] - radBad).toFloat(), null)
    }

    private fun pursueLittleBox(i: Int) {
        bad[i][0] = bad[i][0] + (x[0] - bad[i][0]) * (bad[i][2] / speedBad)
        bad[i][1] = bad[i][1] + +(y[0] - bad[i][1]) * (bad[i][2] / speedBad)
        tavla.drawBitmap(littleBad[frame / 5 % 2]!!,
                (bad[i][0] - radBad).toFloat(),
                (bad[i][1] - radBad).toFloat(), null)
        if (!pursuingBad[i]) {
            val badBark = object : Thread() {
                override fun run() {
                    while ((bad[i][0] - x[0]).pow(2.0) + (bad[i][1] - y[0]).pow(2.0)
                            < (radBad * 4 + radBox).pow(2.0) && bad[i][1] - radBad < y[0]
                            && !dyingBox && running)
                        try {
                            sp.play(badBarking[(frame + i) % 10], 0.5f,
                                    0.5f, 1, 0,1f)

                            sleep(300)
                        } catch (e: InterruptedException) {
                        }

                    pursuingBad[i] = false
                }
            }
            pursuingBad[i] = true
            badBark.priority = 2
            badBark.start()
        }

    }

    private fun badIsDying(i: Int) {
        if (frame - dyingBadRelated[i] - 6 < littleDyingBad.size) {
            if (!dyingBox) bad[i][1] += bad[i][2]
            if (frame - dyingBadRelated[i] < 6)
                tavla.drawBitmap(littleDyingBad[0]!!,
                        (bad[i][0] - radDyingBad).toFloat(),
                        (bad[i][1] - radDyingBad).toFloat(), null)
            else
                tavla.drawBitmap(littleDyingBad[frame % (dyingBadRelated[i] + 6)]!!,
                        (bad[i][0] - radDyingBad).toFloat(),
                        (bad[i][1] - radDyingBad).toFloat(), null)
        } else {
            bad[i][0] = 1337133.0
            dyingBad[i] = false
        }
    }

    private fun interactBad(i: Int) {
        if (bad[i][1] - radBad * .6 >= y[1]) {
            if (intersectLineCircle(x[1], y[1], x[0], y[0], bad[i][0], bad[i][1], radBad)) {
                speedReduction()
                dieLittleBad(i)
            }
        } else if (empowered) {
            empowered = false

            val depower = object : Thread() {
                override fun run() {
                    speedReduction()
                    dieLittleBad(i)
                    littleBox = BitmapFactory.decodeResource(context.resources,
                            R.drawable.littlebox)
                }
            }
            depower.priority = 1
            depower.start()
        } else
            dieLittleBox(i)

        // TODO

    }

    private fun intersectLineCircle(x2: Double, y2: Double, x1: Double, y1: Double, cx: Double, cy: Double,
                                    rad: Double): Boolean {
        val l = (y2 - y1) / (x2 - x1)
        val d = ((2f * l * y1 - 2 * cx - 2.0 * x1 * l.pow(2.0) - (2f * l * cy)).pow(2.0)
                - (4.0 * (l.pow(2.0) + 1) * ((l * x1 - y1).pow(2.0) + cx.pow(2.0)
                + ((2f * l * x1 - 2 * y1 + 1) * cy) - rad.pow(2.0))))
        return d >= 0
    }

    private fun dieLittleBox(i: Int) {
        if (!dyingBox) {
            dyingBoxRelated[0] = frame
            dyingBoxRelated[1] = i

            if (saves.read("Score") < score[0]) saves.write("Score", score[0].toInt())
            sp.play(dyingBoxSound, 1f, 1f, 1, 0, 1f)

            dyingBox = true
        }
    }

    private fun dieLittleBad(i: Int) {
        if (!dyingBad[i]) {
            dyingBad[i] = true
            dyingBadRelated[i] = frame
            score(50)
            val badDeath = object : Thread() {
                override fun run() {
                    try {
                        sp.play(dyingBadSound[frame % dyingBadSound.size], 1f, 1f, 0, 0, 1f)

                        sleep(300)
                        sp.play(explo[frame % explo.size], 1f, 1f, 0, 0, 1f)
                    } catch (e: Exception) {
                    }
                }
            }
            badDeath.priority = 1
            badDeath.start()
        }
    }

    private fun score(points: Int) {
        score[0] += (points * (level + 1)).toDouble()
    }

    private fun createBad(i: Int) {
        val createBad = object : Thread() {
            override fun run() {
                bad[i][1] = 0 - radBad
                bad[i][0] = collide(rand.nextInt((width - radBad.toInt() * 2) * 100).toDouble() / 100 + radBad, bad[i][1], radBad)
                bad[i][2] = (rand.nextInt(3) + speedGlobal).toDouble()
                if (rand.nextInt(10) < 2)
                    for (j in bad.indices)
                        if (bad[j][0] == 1337133.0 && rand.nextInt(3) < 1) {
                            createBad(j)
                            break
                        }

            }
        }
        createBad.priority = 1
        createBad.start()
    }

    private fun drawLittleStar() {
        var create = true
        for (i in star.indices)
            if (star[i][0] != 13371337.0) {
                if (star[i][1] < height + radStar) {
                    tavla.drawBitmap(littleStar,
                            (star[i][0] - radStar).toFloat(),
                            (star[i][1] - radStar).toFloat(), alpha230)
                    if (!dyingBox) star[i][1] += speedGlobal.toDouble()
                } else
                    star[i][0] = 13371337.0
            } else if (!dyingBox && frame % (12 - (speedGlobal - 3) / 10) == 0 && create) {
                    if (rand.nextInt(3) < 2) createStar(i)
                    create = false
                }
    }

    private fun createStar(i: Int) {
        val createStar = object : Thread() {
            override fun run() {
                star[i][0] = rand.nextInt(width * 100).toDouble() / 100
                star[i][1] = 0 - radStar
                if (rand.nextInt(10) < 1)
                    for (j in star.indices)
                        if (star[j][0] == 13371337.0 && rand.nextInt(3) < 2) {
                            createStar(j)
                            break
                        }
            }
        }
        createStar.priority = 1
        createStar.start()
    }

    private fun consumePower() {
        dyingPowerRelated = 1
    }

    private fun drawLittleBox() {
        if (!dyingBox) {
            for (i in 0..4)
                tavla.drawBitmap(littleLight[i]!!,
                        (x[i * 2 + 1] - littleLight[i]!!.width / 2).toFloat(),
                        (y[i * 2 + 1] - littleLight[i]!!.height / 2).toFloat(), alpha60)
            tavla.drawBitmap(littleBox, (x[0] - radBox).toFloat(), (y[0] - radBox).toFloat(), null)
        } else {
            println(frame.toString() + ", " + frame / 10 % 2 + ", " + dyingBoxRelated[1] + ", "
                    + (bad[dyingBoxRelated[1]][0] - radBad) + ", "
                    + (bad[dyingBoxRelated[1]][1] - radBad) + ", " + radBad + ", "
                    + littleBad[frame / 10 % 2])
            try {
                tavla.drawBitmap(littleBad[frame / 10 % 2]!!,
                        (bad[dyingBoxRelated[1]][0] - radBad).toFloat(),
                        (bad[dyingBoxRelated[1]][1] - radBad).toFloat(), null)
            } catch (e: Exception) {
                println(e)
            }

            if (frame - dyingBoxRelated[0] < 9) {
                alpha60.alpha = alpha60.alpha - 6
                for (i in 0..4)
                    tavla.drawBitmap(littleLight[i]!!,
                            (x[i * 2 + 1] - littleLight[i]!!.getWidth() / 2).toFloat(),
                            (y[i * 2 + 1] - littleLight[i]!!.getHeight() / 2).toFloat(), alpha60)

                tavla.drawBitmap(littleDyingBox[frame % dyingBoxRelated[0]]!!,
                        (x[0] - radDyingBox).toFloat(),
                        (y[0] - radDyingBox).toFloat(), null)
            }
        }
    }

    private fun drawBG() {
        tavla.drawARGB(255, 0, 0, 0)
    }

    protected fun movementLog(loop: Int) {
        if (!dyingBox) {
            for (i in 9 downTo 1) {
                x[i] = x[i - 1]
                y[i] = y[i - 1]
            }
            var temp = 10
            if (!gaming) temp = 11
            for (i in 0..loop) {
                x[0] = x[0] + (x[temp] - x[0]) * speedBox[0]
                y[0] = y[0] + (y[temp] - y[0]) * speedBox[0]
            }
        }
    }

    init {
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT)

        dyingBoxSound = sp.load(context, dyingBoxSound, 1)

        for (i in dyingSpeedSound.indices)
            for (j in 0 until dyingSpeedSound[i].size)
                dyingSpeedSound[i][j] = sp.load(context, dyingSpeedSound[i][j], 1)
        for (i in dyingPowerSound.indices)
            dyingPowerSound[i] = sp.load(context, dyingPowerSound[i], 1)
        for (i in dyingBadSound.indices)
            dyingBadSound[i] = sp.load(context, dyingBadSound[i], 1)
        for (i in badBarking.indices)
            badBarking[i] = sp.load(context, badBarking[i], 1)
        for (i in foodPop.indices)
            foodPop[i] = sp.load(context, foodPop[i], 1)
        for (i in explo.indices)
            explo[i] = sp.load(context, explo[i], 1)

    }

    fun onGameResume() {
        running = true
        viewLoop = Thread(this)
        viewLoop.start()
    }

    fun onGamePause() {
        running = false

    }

    fun setTouch(coordX: Float, coordY: Float) {
        if (gaming) {
            x[10] = coordX.toDouble()
            y[10] = coordY.toDouble()
        } else {
            x[11] = coordX.toDouble()
            y[11] = coordY.toDouble()
        }
    }

    fun setTouchUP() {
        if (x[11] > width / 2 - radMenu[0] && x[11] < width / 2 + radMenu[0]
                && y[11] > height / 2 - pixelDP(100) + menuBounds.top / 2
                && y[11] < height / 2 - pixelDP(100) - menuBounds.bottom) {
            menuing = false
            gaming = true
            movementLog(10)
        } else if (x[11] > width / 2 - radMenu[0] && x[11] < width / 2 + radMenu[0]
                && y[11] > height / 2 + menuBounds.top / 2
                && y[11] < height / 2 - menuBounds.bottom) {
            // menuing = false;
            // optioning = true;
        } else if (x[11] > width / 2 - radMenu[0] && x[11] < width / 2 + radMenu[0]
                && y[11] > height / 2 + pixelDP(100) + menuBounds.top / 2
                && y[11] < height / 2 + pixelDP(100) - menuBounds.bottom) {
            (context as Activity).finish()
        }
    }

    fun pixelDP(dp: Int): Float {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(), resources
                .displayMetrics)
    }

    fun createBitmap(resource: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources,resource)
    }

    fun reset() {
        x[11] = -200.0
        y[11] = -200.0
        movementLog(10)
        gaming = false
        menuing = true
        if (dyingBox) tavlaInit(true)
    }
}
