package com.lumi.app

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        val title = i.getStringExtra("title") ?: "Lumi"
        val label = i.getStringExtra("label") ?: ""
        val music = i.getStringExtra("music") ?: ""
        val nm = c.getSystemService(NotificationManager::class.java)
        val ch = NotificationChannel("ring", "Réveils et rappels", NotificationManager.IMPORTANCE_HIGH)
        ch.setSound(null, null); ch.enableVibration(true); ch.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        nm.createNotificationChannel(ch)

        val ring = Intent(c, RingActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .putExtra("title", title).putExtra("label", label).putExtra("music", music)
        val p = PendingIntent.getActivity(c, 1, ring, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val n = Notification.Builder(c, "ring")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title).setContentText(label)
            .setCategory(Notification.CATEGORY_ALARM)
            .setFullScreenIntent(p, true).setContentIntent(p)
            .setAutoCancel(true).build()
        nm.notify(42, n)
        Scheduler.all(c) // programme la prochaine occurrence
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) { Scheduler.all(c) }
}
