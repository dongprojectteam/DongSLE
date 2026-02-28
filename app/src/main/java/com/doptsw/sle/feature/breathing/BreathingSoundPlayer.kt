package com.doptsw.sle.feature.breathing

import android.media.AudioManager
import android.media.ToneGenerator

class BreathingSoundPlayer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)

    fun play(soundType: BreathingSoundType) {
        val tone = when (soundType) {
            BreathingSoundType.EXHALE_EDGE -> ToneGenerator.TONE_PROP_BEEP
            BreathingSoundType.HOLD_EXHALE_EDGE -> ToneGenerator.TONE_PROP_ACK
            BreathingSoundType.INHALE_EDGE -> ToneGenerator.TONE_PROP_PROMPT
            BreathingSoundType.HOLD_INHALE_EDGE -> ToneGenerator.TONE_PROP_BEEP2
        }
        toneGenerator.startTone(tone, 120)
    }

    fun release() {
        toneGenerator.release()
    }
}
