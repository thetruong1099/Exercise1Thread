package com.example.exercise1thread

import android.graphics.Color
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var handler: Handler
    private lateinit var runnableRevert: Runnable
    private lateinit var runnableIncrease: Runnable
    private lateinit var runnableIncreaseFast: Runnable
    private lateinit var runnableReduce: Runnable
    private lateinit var runnableReduceFast: Runnable
    private lateinit var runnableREvertNumberBuff1: Runnable

    private var number = 0
    private var numberBuff = 0
    private var numberBuff1 = 0
    private var status: Boolean = false

    private lateinit var gestureDetector: GestureDetector
    private var y2: Float = 0.0f
    private var y1: Float = 0.0f

    companion object {
        const val MIN_DISTANCE = 150
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var handlerThread = HandlerThread("MyHandlerThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        gestureDetector = GestureDetector(this, this)

        btnIncrease.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                btnIncrease.setBackgroundColor(Color.GREEN)
                handler.post(runnableIncrease)
            } else if (event.action == MotionEvent.ACTION_UP) {
                btnIncrease.setBackgroundColor(Color.parseColor("#FF03DAC5"))
                handler.removeCallbacks(runnableIncreaseFast)
                handler.postDelayed(runnableRevert, 1000)
            }
            false
        }

        btnIncrease.setOnLongClickListener {
            handler.postDelayed(runnableIncreaseFast, 500)
            true
        }

        btnReduce.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                btnReduce.setBackgroundColor(Color.GREEN)
                handler.post(runnableReduce)
            } else if (event.action == MotionEvent.ACTION_UP) {
                btnReduce.setBackgroundColor(Color.parseColor("#FF03DAC5"))
                handler.removeCallbacks(runnableReduceFast)
                handler.postDelayed(runnableRevert, 1000)
            }
            false
        }

        btnReduce.setOnLongClickListener {
            handler.postDelayed(runnableReduceFast, 500)
            true
        }

        runnableRevert = Runnable {
            when {
                number > 0 -> {
                    number--
                    changeColor()
                    this.runOnUiThread { tvNumber.text = number.toString() }
                    handler.postDelayed(runnableRevert, 100)
                }
                number < 0 -> {
                    number++
                    changeColor()
                    this.runOnUiThread { tvNumber.text = number.toString() }
                    handler.postDelayed(runnableRevert, 100)
                }
                else -> {
                    handler.removeCallbacks(runnableRevert)
                }
            }
        }

        runnableIncrease = Runnable {
            handler.removeCallbacks(runnableRevert)
            number++
            changeColor()
            this.runOnUiThread { tvNumber.text = number.toString() }
            handler.removeCallbacks(runnableIncrease)
        }

        runnableIncreaseFast = Runnable {
            handler.removeCallbacks(runnableRevert)
            number++
            this.runOnUiThread { tvNumber.text = number.toString() }
            handler.postDelayed(runnableIncreaseFast, 50)
        }

        runnableReduce = Runnable {
            handler.removeCallbacks(runnableRevert)
            number--
            changeColor()
            this.runOnUiThread { tvNumber.text = number.toString() }

            handler.removeCallbacks(runnableReduce)
        }

        runnableReduceFast = Runnable {
            handler.removeCallbacks(runnableRevert)
            number--
            changeColor()
            this.runOnUiThread { tvNumber.text = number.toString() }
            handler.postDelayed(runnableReduceFast, 50)
        }

        runnableREvertNumberBuff1 = Runnable {
            when {
                numberBuff1 > 0 -> {
                    numberBuff1--
                    changeColor()
                    this.runOnUiThread { tvNumber.text = numberBuff1.toString() }
                    handler.postDelayed(runnableREvertNumberBuff1, 100)
                }
                numberBuff1 < 0 -> {
                    numberBuff1++
                    changeColor()
                    this.runOnUiThread { tvNumber.text = numberBuff1.toString() }
                    handler.postDelayed(runnableREvertNumberBuff1, 100)
                }
                else -> {
                    handler.removeCallbacks(runnableREvertNumberBuff1)
                }
            }
        }
    }

    private fun changeColor() {
        if (Math.abs(numberBuff - number) > 100) {
            val ramdom = Random()
            tvNumber.setTextColor(
                Color.rgb(
                    ramdom.nextInt(256),
                    ramdom.nextInt(256),
                    ramdom.nextInt(256)
                )
            )
            numberBuff = number
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        when (event?.action) {
            0 -> {
                y1 = event.y
            }

            1 -> {
                if (status) {
                    numberBuff1 = number
                    handler.removeCallbacks(runnableIncreaseFast)
                    handler.removeCallbacks(runnableReduceFast)
                    handler.postDelayed(runnableRevert, 1000)
                    status = false
                } else {
                    handler.removeCallbacks(runnableIncreaseFast)
                    handler.removeCallbacks(runnableReduceFast)
                    handler.postDelayed(runnableREvertNumberBuff1, 1000)
                }

            }

            2 -> {
                y2 = event.y
                val valueY: Float = y2 - y1
                if (Math.abs(valueY) > MIN_DISTANCE) {
                    if (valueY < 0) {
                        handler.postDelayed(runnableIncreaseFast, 1)
                    } else {
                        handler.postDelayed(runnableReduceFast, 1)
                    }
                }
                status = true
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return true
    }
}