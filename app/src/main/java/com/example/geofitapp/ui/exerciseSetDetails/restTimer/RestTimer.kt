package com.example.geofitapp.ui.exerciseSetDetails.restTimer

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import com.example.geofitapp.R
import com.example.geofitapp.databinding.ActivityExerciseSetDetailsBinding
import java.util.*

class RestTimer(val context: Context, val binding: ActivityExerciseSetDetailsBinding) {

    private var timer: CountDownTimer? = null
    var timerLengthSeconds: Long = 0
    var secondsRemaining: Long = 0
    var timerState = TimerState.NotStarted
    private lateinit var mp: MediaPlayer

    enum class TimerState {
        NotStarted, Stopped, Running
    }

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            Log.i("restTimer", "setting alarm curr time: $nowSeconds")
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }


    fun initTimer(restTime: Int) {
        timerState = PrefUtil.getTimerState(context)
        Log.i("restTimer", "timerState is: $timerState")

        if(timerState == TimerState.Stopped){
            onTimerFinished()
            PrefUtil.setTimerState(TimerState.NotStarted, context)
            timerState = TimerState.NotStarted
            updateCountdownUI()
            return
        }

        if (timerState == TimerState.NotStarted)
            setNewTimerLength(restTime)
        else
            setPreviousTimerLength()

        secondsRemaining = if (timerState == TimerState.Running) {
            Log.i("restTimer", "getting prev secondsRemaining = ${PrefUtil.getSecondsRemaining(context)}")
            PrefUtil.getSecondsRemaining(context)
        }
        else {
            Log.i("restTimer", "secondsRemaining = $timerLengthSeconds")
            timerLengthSeconds
        }

        val alarmSetTime = PrefUtil.getAlarmSetTime(context)
        Log.i("restTimer", "alartmset Time = $alarmSetTime")
        if (alarmSetTime > 0) {
            secondsRemaining -= nowSeconds - alarmSetTime
            Log.i("restTimer", "updated secondsRemaining = $secondsRemaining")
        }

        if (secondsRemaining <= 0){
            Log.i("restTimer", "finishing timer from init()")
            onTimerFinished()
        }

        updateCountdownUI()
        if(secondsRemaining > 0){
            startTimer()
        }
    }

    fun cancelTimer() {
        timer?.cancel()
    }

    fun resetDetails(){
        PrefUtil.setSecondsRemaining(0, context)
        PrefUtil.setPreviousTimerLengthSeconds(0, context)
        PrefUtil.setTimerState(TimerState.Stopped, context)
        timerState = TimerState.NotStarted
        binding.progressCountdown.progress = 0
        secondsRemaining = 0
        timerLengthSeconds = 0
        updateCountdownUI()
    }

    private fun onTimerFinished() {
        resetDetails()
        Log.i("restTimer", "timer finished in OnTimerFinished(), timer length = 0, secondsRemain = 0, timerstate = 0")

        mp = MediaPlayer.create(context, R.raw.timer_expired)
        mp.isLooping = true
        mp.start()
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Rest Time Expired")
            .setMessage("Time for another set!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }.create()

        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setOnDismissListener{
            mp.stop()
            Log.i("restTimer", "mp stopped")
        }
        alertDialog.show()
    }

    private fun startTimer() {
        Log.i("restTimer", "timer started for $secondsRemaining")

        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(restTime: Int) {
        Log.i("restTimer", "new timerLength = $restTime")
        timerLengthSeconds = restTime.toLong()
        binding.progressCountdown.max = restTime
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(context)
        Log.i("restTimer", "previous timerLength = $timerLengthSeconds")
        binding.progressCountdown.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        binding.countdownText.text =
            "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}s"
        binding.progressCountdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

}