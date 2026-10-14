package com.lumi.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Programme les vrais réveils/rappels du système à partir de l'état envoyé par l'interface. */
object Scheduler {
    private fun pi(c: Context, code: Int, title: String, label: String, music: String): PendingIntent {
        val i = Intent(c, AlarmReceiver::class.java).setAction("lumi.ring")
            .putExtra("title", title).putExtra("label", label).putExtra("music", music)
        return PendingIntent.getBroadcast(c, code, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun one(c: Context, at: Long, code: Int, title: String, label: String, music: String) {
        val am = c.getSystemService(AlarmManager::class.java)
        val show = PendingIntent.getActivity(c, 0, Intent(c, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        try {
            am.setAlarmClock(AlarmManager.AlarmClockInfo(at, show), pi(c, code, title, label, music))
        } catch (e: SecurityException) { }
    }

    fun all(c: Context) {
        val sp = c.getSharedPreferences("lumi", 0)
        val am = c.getSystemService(AlarmManager::class.java)
        sp.getStringSet("codes", emptySet())!!.forEach { am.cancel(pi(c, it.toInt(), "", "", "")) }
        val codes = mutableSetOf<String>()
        val st = try { JSONObject(sp.getString("state", "{}")!!) } catch (e: Exception) { JSONObject() }
        val now = System.currentTimeMillis()

        val a = st.optJSONArray("a")
        for (i in 0 until (a?.length() ?: 0)) {
            val x = a!!.getJSONObject(i)
            if (!x.optBoolean("on")) continue
            val hm = x.optString("t").split(":")
            if (hm.size < 2) continue
            val date = x.optString("date")
            val cal = Calendar.getInstance()
            if (date.length == 10) {
                val p = date.split("-")
                cal.set(p[0].toInt(), p[1].toInt() - 1, p[2].toInt())
            }
            cal.set(Calendar.HOUR_OF_DAY, hm[0].toInt()); cal.set(Calendar.MINUTE, hm[1].toInt())
            cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            if (cal.timeInMillis <= now) {
                if (date.length == 10) continue          // réveil d'un jour précis, déjà passé
                cal.add(Calendar.DAY_OF_YEAR, 1)         // réveil quotidien : demain
            }
            val label = x.optString("l").ifBlank { "C'est l'heure de se lever doucement" }
            one(c, cal.timeInMillis, 1000 + i, "Bonjour ☀️", label, x.optString("m"))
            codes.add((1000 + i).toString())
        }

        val r = st.optJSONArray("r")
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
        for (i in 0 until (r?.length() ?: 0)) {
            val x = r!!.getJSONObject(i)
            if (x.optBoolean("x")) continue
            val t = try { fmt.parse(x.optString("d"))?.time } catch (e: Exception) { null } ?: continue
            if (t <= now) continue
            one(c, t, 2000 + i, "Petit rappel 🔔", x.optString("t"), "Pluie de lune")
            codes.add((2000 + i).toString())
        }
        sp.edit().putStringSet("codes", codes).apply()
    }
}
