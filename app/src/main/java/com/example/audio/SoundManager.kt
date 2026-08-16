package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

object SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isMuted: Boolean = false

    private fun playPcm(sampleRate: Int = 22050, durationMs: Int, generator: (Int, Int) -> Float) {
        if (isMuted) return
        scope.launch {
            try {
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                if (numSamples <= 0) return@launch
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val sample = generator(i, numSamples).coerceIn(-1.0f, 1.0f)
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                kotlinx.coroutines.delay(durationMs.toLong() + 100)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Repique de racha: el tono SUBE con la cadena de aciertos.
     *
     * Los minijuegos sonaban igual en el acierto uno que en el diez, asi que la
     * racha no se oia — y una racha que no se oye no se siente. Cada eslabon
     * sube un semitono sobre una pentatonica, que es lo que hace que encadenar
     * suene a que algo va bien y no a un pitido repetido.
     */
    fun playComboTick(streak: Int) {
        val step = (streak - 1).coerceIn(0, 11)
        val base = 523.25 // do5
        val freq = base * Math.pow(2.0, step / 12.0)
        playPcm(sampleRate = 22050, durationMs = 90) { i, total ->
            val t = i / 22050.0
            val decay = (1.0 - i.toDouble() / total).toFloat()
            (sin(2 * PI * freq * t).toFloat() * 0.35f +
                sin(2 * PI * freq * 2 * t).toFloat() * 0.12f) * decay * decay
        }
    }

    /** La racha se rompe: caida de tono, corta y seca. */
    fun playComboBreak() {
        playPcm(sampleRate = 22050, durationMs = 220) { i, total ->
            val p = i.toDouble() / total
            val freq = 420.0 * (1.0 - p * 0.55)
            val decay = (1.0 - p).toFloat()
            sin(2 * PI * freq * (i / 22050.0)).toFloat() * decay * 0.4f
        }
    }

    // 1. Sword Physical Attack Slash
    fun playSwordSlash() {
        playPcm(sampleRate = 22050, durationMs = 200) { i, total ->
            val progress = i.toFloat() / total
            val env = (1.0f - progress) * (1.0f - progress)
            val freq = 520.0f * (1.0f - progress * 0.75f)
            val tone = sin(2.0 * PI * freq * i / 22050.0).toFloat()
            val noise = (Random.nextFloat() * 2.0f - 1.0f) * 0.7f
            (tone * 0.35f + noise * 0.65f) * env
        }
    }

    // 2. Magic Spell Cast
    fun playMagicSpell() {
        playPcm(sampleRate = 22050, durationMs = 300) { i, total ->
            val progress = i.toFloat() / total
            val env = sin(progress * PI).toFloat()
            val freq = 350.0f + progress * 900.0f
            val tone1 = sin(2.0 * PI * freq * i / 22050.0).toFloat()
            val tone2 = sin(2.0 * PI * (freq * 1.5f) * i / 22050.0).toFloat()
            (tone1 * 0.6f + tone2 * 0.4f) * env
        }
    }

    // 3. Health Potion / Healing Magic
    fun playHealPotion() {
        playPcm(sampleRate = 22050, durationMs = 380) { i, total ->
            val progress = i.toFloat() / total
            val env = (1.0f - progress)
            val step = (progress * 4).toInt()
            val baseFreq = when (step) {
                0 -> 523.25f // C5
                1 -> 659.25f // E5
                2 -> 783.99f // G5
                else -> 1046.50f // C6
            }
            val tone = sin(2.0 * PI * baseFreq * i / 22050.0).toFloat()
            tone * env * 0.75f
        }
    }

    // 4. Enemy Monster Attack
    fun playEnemyAttack() {
        playPcm(sampleRate = 22050, durationMs = 240) { i, total ->
            val progress = i.toFloat() / total
            val env = (1.0f - progress)
            val freq = 130.0f - progress * 60.0f
            val square = if (sin(2.0 * PI * freq * i / 22050.0) > 0) 0.5f else -0.5f
            val noise = (Random.nextFloat() * 2.0f - 1.0f) * 0.5f
            (square * 0.5f + noise * 0.5f) * env
        }
    }

    // 5. Critical Impact
    fun playCriticalHit() {
        playPcm(sampleRate = 22050, durationMs = 320) { i, total ->
            val progress = i.toFloat() / total
            val env = (1.0f - progress) * (1.0f - progress)
            val bass = sin(2.0 * PI * 85.0 * i / 22050.0).toFloat()
            val metallicClash = (Random.nextFloat() * 2.0f - 1.0f)
            (bass * 0.55f + metallicClash * 0.45f) * env
        }
    }

    // 6. Victory Fanfare
    fun playVictory() {
        playPcm(sampleRate = 22050, durationMs = 650) { i, total ->
            val progress = i.toFloat() / total
            val env = (1.0f - progress * 0.4f)
            val noteStep = (progress * 5).toInt()
            val freq = when (noteStep) {
                0 -> 523.25f // C5
                1 -> 659.25f // E5
                2 -> 783.99f // G5
                3 -> 987.77f // B5
                else -> 1046.50f // C6
            }
            val tone = sin(2.0 * PI * freq * i / 22050.0).toFloat()
            tone * env * 0.8f
        }
    }

    // 7. Defeat / Player Death
    fun playDefeat() {
        playPcm(sampleRate = 22050, durationMs = 550) { i, total ->
            val progress = i.toFloat() / total
            val env = (1.0f - progress)
            val freq = 240.0f - progress * 120.0f
            val tone = sin(2.0 * PI * freq * i / 22050.0).toFloat()
            tone * env * 0.75f
        }
    }

    // 8. UI Button Click / Tab Switch
    fun playButtonClick() {
        playPcm(sampleRate = 22050, durationMs = 45) { i, total ->
            val progress = i.toFloat() / total
            val env = (1.0f - progress)
            val tone = sin(2.0 * PI * 850.0 * i / 22050.0).toFloat()
            tone * env * 0.35f
        }
    }
}
