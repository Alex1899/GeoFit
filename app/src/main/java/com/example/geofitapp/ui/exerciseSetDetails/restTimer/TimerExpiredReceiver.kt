package com.example.geofitapp.ui.exerciseSetDetails.restTimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)

        PrefUtil.setTimerState(RestTimer.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}