package com.lumi.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionService
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import android.speech.RecognitionService
import android.speech.SpeechRecognizer

/** Permet de choisir Lumi comme « assistant numérique » : l'appui long sur le bouton d'accueil l'ouvre en mode écoute. */
class LumiInteractionService : VoiceInteractionService()

class LumiSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle?): VoiceInteractionSession = LumiSession(this)
}

class LumiSession(ctx: Context) : VoiceInteractionSession(ctx) {
    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        val i = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .putExtra("listen", true)
        try { startAssistantActivity(i) } catch (e: Exception) { context.startActivity(i) }
        hide()
    }
}

/** Obligatoire pour déclarer un assistant ; la vraie reconnaissance vocale est faite par Android dans MainActivity. */
class LumiRecognitionService : RecognitionService() {
    override fun onStartListening(i: Intent?, cb: Callback?) { cb?.error(SpeechRecognizer.ERROR_CLIENT) }
    override fun onCancel(cb: Callback?) {}
    override fun onStopListening(cb: Callback?) {}
}
