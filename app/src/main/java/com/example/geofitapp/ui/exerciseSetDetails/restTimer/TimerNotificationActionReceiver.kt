package com.example.geofitapp.ui.exerciseSetDetails.restTimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){
            TimerConstants.ACTION_STOP -> {
                RestTimer.removeAlarm(context)
                PrefUtil.setTimerState(RestTimer.TimerState.Stopped, context)
                NotificationUtil.hideTimerNotification(context)
            }

        }
    }
}