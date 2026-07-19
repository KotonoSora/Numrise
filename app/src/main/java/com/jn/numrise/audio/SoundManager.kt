package com.jn.numrise.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.jn.numrise.R

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val sounds = mutableMapOf<String, Int>()
    var isEnabled: Boolean = true

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        sounds["tap"] = soundPool.load(context, R.raw.tap, 1)
        sounds["error"] = soundPool.load(context, R.raw.error, 1)
        sounds["win"] = soundPool.load(context, R.raw.win, 1)
        sounds["lose"] = soundPool.load(context, R.raw.lose, 1)
    }

    fun play(soundName: String) {
        if (!isEnabled) return
        val soundId = sounds[soundName] ?: return
        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
