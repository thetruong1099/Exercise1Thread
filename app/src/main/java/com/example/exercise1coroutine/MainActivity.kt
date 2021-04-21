package com.example.exercise1coroutine

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private var number = 0
    private var numberBuff = 0
    private var numberBuff1 = 0
    private var status: Boolean = false

    private lateinit var jobRevertNumber: Job
    private lateinit var jobInCreaseFastNumber: Job
    private lateinit var jobReduceFastNumber: Job
    private lateinit var jobRevertNumberBuffer1: Job

    private lateinit var gestureDetector: GestureDetector
    private var y2: Float = 0.0f
    private var y1: Float = 0.0f

    companion object {
        const val MIN_DISTANCE = 150
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jobRevertNumber = Job()
        jobInCreaseFastNumber = Job()
        jobReduceFastNumber = Job()
        jobRevertNumberBuffer1 = Job()

        gestureDetector = GestureDetector(this, this)

        btnIncrease.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                btnIncrease.setBackgroundColor(Color.parseColor("#FF03DAC5"))
                jobInCreaseFastNumber.cancel()
                jobRevertNumber.cancel()
                revertNumberToZero()
            }
            if (event.action == MotionEvent.ACTION_DOWN) {
                btnIncrease.setBackgroundColor(Color.GREEN)
                numberIncrease()
            }
            false
        }

        btnIncrease.setOnLongClickListener {
            numberIncreaseFast()
            true
        }

        btnReduce.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                btnReduce.setBackgroundColor(Color.parseColor("#FF03DAC5"))
                jobReduceFastNumber.cancel()
                jobRevertNumber.cancel()
                revertNumberToZero()
            }
            if (event.action == MotionEvent.ACTION_DOWN) {
                btnReduce.setBackgroundColor(Color.GREEN)
                numberReduce()
            }

            false
        }

        btnReduce.setOnLongClickListener {
            numberReduceFast()
            true
        }

    }

    private fun numberReduceFast() {
        jobReduceFastNumber = GlobalScope.launch {
            while (true) {
                number--
                delay(100)
                updateUI(number)
            }
        }
    }

    private fun numberReduce() {
        GlobalScope.launch {
            number--
            updateUI(number)
        }
    }

    private fun numberIncreaseFast() {
        jobInCreaseFastNumber = GlobalScope.launch {
            while (true) {
                number++
                delay(100)
                updateUI(number)
            }
        }
    }


    private fun numberIncrease() {
        GlobalScope.launch {
            number++
            updateUI(number)
        }
    }


    private fun revertNumberToZero() {
        jobRevertNumber = GlobalScope.launch {
            delay(1000)
            when {
                number > 0 -> {
                    while (number > 0) {
                        number--
                        delay(100)
                        updateUI(number)
                    }
                }
                number < 0 -> {
                    while (number < 0) {
                        number++
                        delay(100)
                        updateUI(number)
                    }
                }
                else -> {
                }
            }
        }
    }

    private suspend fun updateUI(iNumber:Int) {
        withContext(Dispatchers.Main) {
            changeColor()
            tvNumber.text = iNumber.toString()
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
                    jobRevertNumber.cancel()
                    revertNumberToZero()
                    status = false
                } else {
                    jobRevertNumberBuffer1.cancel()
                    revertNumberBufferToZero()
                }

            }

            2 -> {
                y2 = event.y
                val valueY: Float = y2 - y1
                if (Math.abs(valueY) > MIN_DISTANCE) {
                    if (valueY < 0) {
                        jobInCreaseFastNumber.cancel()
                        numberIncrease()

                    } else {
                        numberReduce()
                    }
                }
                status = true
            }
        }


        return super.onTouchEvent(event)
    }

    private fun revertNumberBufferToZero() {
        jobRevertNumberBuffer1 = GlobalScope.launch {
            delay(1000)
            when {
                numberBuff1 > 0 -> {
                    while (numberBuff1 > 0) {
                        numberBuff1--
                        delay(100)
                        updateUI(numberBuff1)
                    }
                }
                numberBuff1 < 0 -> {
                    while (numberBuff1 < 0) {
                        numberBuff1++
                        delay(100)
                        updateUI(numberBuff1)
                    }
                }
                else -> {
                }
            }
        }
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