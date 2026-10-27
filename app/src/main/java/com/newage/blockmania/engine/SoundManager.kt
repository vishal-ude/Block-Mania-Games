package com.newage.blockmania.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/**
 * Premium, ultra-low-latency game audio engine using Android's native SoundPool.
 * Generates custom, pristine 16-bit PCM acoustic waveforms locally (zero copyrighted assets,
 * zero network delay, zero garbage collection churn on playback).
 */
class SoundManager(context: Context? = null) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val sampleRate = 22050
    var isEnabled: Boolean = true
    var volume: Float = 1.0f

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    // Sound IDs loaded into SoundPool
    private var soundPickUp: Int = 0
    private var soundPlace: Int = 0
    private var soundInvalid: Int = 0
    private var soundLineClear: Int = 0
    private var soundCombo: Int = 0
    private var soundMilestone: Int = 0
    private var soundNewBest: Int = 0
    private var soundClick: Int = 0
    private var soundGameOver: Int = 0
    private var soundChallengeComplete: Int = 0

    init {
        if (context != null) {
            initSounds(context)
        }
    }

    private fun initSounds(context: Context) {
        scope.launch {
            try {
                val soundDir = File(context.cacheDir, "bm_audio").apply { mkdirs() }

                // 1. Block pickup: short, soft tactile sound (520Hz -> 780Hz, 38ms)
                val fPickUp = File(soundDir, "pickup.wav")
                writeWav(fPickUp, generateBubbleChirp(520.0, 780.0, 0.038))
                soundPickUp = soundPool.load(fPickUp.absolutePath, 1)

                // 2. Block placement: satisfying, clean snap with tactile transient (260Hz + 850Hz snap, 55ms)
                val fPlace = File(soundDir, "place.wav")
                writeWav(fPlace, generateSnapThud(260.0, 160.0, 0.055))
                soundPlace = soundPool.load(fPlace.absolutePath, 1)

                // 3. Invalid placement: subtle, non-annoying dual-tone bump (140Hz -> 100Hz, 70ms)
                val fInvalid = File(soundDir, "invalid.wav")
                writeWav(fInvalid, generateMuffledThud(140.0, 100.0, 0.070))
                soundInvalid = soundPool.load(fInvalid.absolutePath, 1)

                // 4. Single-line clear: bright, rewarding bell chord (C5, E5, G5, C6 - 180ms)
                val fLineClear = File(soundDir, "line_clear.wav")
                writeWav(fLineClear, generateChimeChord(listOf(523.25, 659.25, 783.99, 1046.50), 0.18))
                soundLineClear = soundPool.load(fLineClear.absolutePath, 1)

                // 5. Multiple-line clear: rich layered combo arpeggio with high shimmer (523Hz - 1318Hz, 240ms)
                val fCombo = File(soundDir, "combo.wav")
                writeWav(fCombo, generateLayeredCombo(listOf(523.25, 659.25, 783.99, 1046.50, 1318.51, 1567.98), 0.24))
                soundCombo = soundPool.load(fCombo.absolutePath, 1)

                // 6. Score milestone: celebratory cue fanfare (280ms)
                val fMilestone = File(soundDir, "milestone.wav")
                writeWav(fMilestone, generateFanfare(listOf(587.33, 739.99, 880.00, 1174.66), 0.28))
                soundMilestone = soundPool.load(fMilestone.absolutePath, 1)

                // 7. New personal best: dazzling achievement arpeggio (350ms)
                val fNewBest = File(soundDir, "new_best.wav")
                writeWav(fNewBest, generateAchievementFanfare(0.35))
                soundNewBest = soundPool.load(fNewBest.absolutePath, 1)

                // 8. Button interaction: subtle UI click (12ms clean tick)
                val fClick = File(soundDir, "click.wav")
                writeWav(fClick, generateUiClick(0.015))
                soundClick = soundPool.load(fClick.absolutePath, 1)

                // 9. Game over: gentle, polished ending cue (380ms)
                val fGameOver = File(soundDir, "game_over.wav")
                writeWav(fGameOver, generateGameOverCue(0.38))
                soundGameOver = soundPool.load(fGameOver.absolutePath, 1)

                // 10. Daily challenge completion: positive golden reward sound (320ms)
                val fChallenge = File(soundDir, "challenge_complete.wav")
                writeWav(fChallenge, generateRewardSound(0.32))
                soundChallengeComplete = soundPool.load(fChallenge.absolutePath, 1)
            } catch (_: Exception) {
                // Fallback graceful degradation
            }
        }
    }

    fun playPickUp() {
        play(soundPickUp, 0.70f * volume)
    }

    fun playPlace() {
        play(soundPlace, 0.90f * volume)
    }

    fun playInvalid() {
        play(soundInvalid, 0.50f * volume)
    }

    fun playLineClear() {
        play(soundLineClear, 1.0f * volume)
    }

    fun playCombo(streak: Int = 1) {
        // Modulate pitch slightly based on streak for escalating excitement!
        val pitch = (1.0f + (streak.coerceIn(1, 8) - 1) * 0.05f).coerceIn(1.0f, 1.4f)
        play(soundCombo, 1.0f * volume, pitch = pitch)
    }

    fun playScoreMilestone() {
        play(soundMilestone, 1.0f * volume)
    }

    fun playNewBest() {
        play(soundNewBest, 1.0f * volume)
    }

    fun playButtonClick() {
        play(soundClick, 0.40f * volume)
    }

    fun playGameOver() {
        play(soundGameOver, 0.85f * volume)
    }

    fun playDailyChallengeComplete() {
        play(soundChallengeComplete, 1.0f * volume)
    }

    private fun play(soundId: Int, vol: Float, pitch: Float = 1.0f) {
        if (!isEnabled || soundId == 0) return
        try {
            soundPool.play(soundId, vol, vol, 1, 0, pitch)
        } catch (_: Exception) {
            // Non-blocking fallback
        }
    }

    fun release() {
        try {
            soundPool.release()
        } catch (_: Exception) {}
    }

    // ================= WAVEFORM SYNTHESIS & WAV FILE WRITER =================

    private fun writeWav(file: File, pcmData: ShortArray) {
        val totalAudioLen = pcmData.size * 2
        val totalDataLen = totalAudioLen + 36
        val channels = 1
        val byteRate = sampleRate * channels * 2

        val header = ByteArray(44)
        val buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)

        buffer.put("RIFF".toByteArray())
        buffer.putInt(totalDataLen)
        buffer.put("WAVE".toByteArray())
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16) // Subchunk1Size
        buffer.putShort(1.toShort()) // PCM format
        buffer.putShort(channels.toShort())
        buffer.putInt(sampleRate)
        buffer.putInt(byteRate)
        buffer.putShort((channels * 2).toShort()) // Block align
        buffer.putShort(16.toShort()) // Bits per sample
        buffer.put("data".toByteArray())
        buffer.putInt(totalAudioLen)

        FileOutputStream(file).use { fos ->
            fos.write(header)
            val byteBuffer = ByteBuffer.allocate(totalAudioLen).order(ByteOrder.LITTLE_ENDIAN)
            for (sample in pcmData) {
                byteBuffer.putShort(sample)
            }
            fos.write(byteBuffer.array())
        }
    }

    private fun generateBubbleChirp(startFreq: Double, endFreq: Double, durationSec: Double): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / totalSamples
            val freq = startFreq + (endFreq - startFreq) * progress
            val env = exp(-progress * 4.5) * (1.0 - progress)
            val sample = sin(2.0 * PI * freq * t) * env
            buffer[i] = (sample * 24000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateSnapThud(startFreq: Double, endFreq: Double, durationSec: Double): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / totalSamples
            val freq = startFreq + (endFreq - startFreq) * progress
            val click = if (i < 80) sin(2.0 * PI * 1800.0 * t) * 0.4 else 0.0
            val env = exp(-progress * 7.0)
            val sample = (sin(2.0 * PI * freq * t) * 0.8 + click) * env
            buffer[i] = (sample * 25000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateMuffledThud(startFreq: Double, endFreq: Double, durationSec: Double): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / totalSamples
            val freq = startFreq + (endFreq - startFreq) * progress
            val env = exp(-progress * 5.5) * (1.0 - progress)
            val sample = sin(2.0 * PI * freq * t) * env
            buffer[i] = (sample * 18000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateChimeChord(notes: List<Double>, durationSec: Double): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / totalSamples
            var sum = 0.0
            for ((idx, freq) in notes.withIndex()) {
                val delay = idx * 0.02
                if (t >= delay) {
                    val noteT = t - delay
                    val noteProgress = (i - delay * sampleRate) / totalSamples
                    val env = exp(-noteProgress * 4.0)
                    sum += (sin(2.0 * PI * freq * noteT) + 0.25 * sin(4.0 * PI * freq * noteT)) * env
                }
            }
            val sample = (sum / notes.size.toDouble())
            buffer[i] = (sample * 26000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateLayeredCombo(notes: List<Double>, durationSec: Double): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            var sum = 0.0
            for ((idx, freq) in notes.withIndex()) {
                val noteDelay = idx * 0.028
                if (t >= noteDelay) {
                    val noteT = t - noteDelay
                    val env = exp(-noteT * 7.0)
                    sum += (sin(2.0 * PI * freq * noteT) + 0.3 * sin(4.0 * PI * freq * noteT)) * env
                }
            }
            val sample = (sum / (notes.size * 0.65))
            buffer[i] = (sample * 25000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateFanfare(notes: List<Double>, durationSec: Double): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        val noteLength = durationSec / notes.size
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val noteIndex = (t / noteLength).toInt().coerceIn(0, notes.size - 1)
            val freq = notes[noteIndex]
            val noteProgress = (t % noteLength) / noteLength
            val env = (1.0 - noteProgress * 0.2) * exp(-noteProgress * 2.5)
            val sample = (sin(2.0 * PI * freq * t) + 0.35 * sin(4.0 * PI * freq * t)) * env
            buffer[i] = (sample * 26000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateAchievementFanfare(durationSec: Double): ShortArray {
        val notes = listOf(523.25, 659.25, 783.99, 1046.50, 1318.51, 1567.98)
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            var sum = 0.0
            for ((idx, freq) in notes.withIndex()) {
                val delay = idx * 0.04
                if (t >= delay) {
                    val noteT = t - delay
                    val env = exp(-noteT * 5.0)
                    sum += (sin(2.0 * PI * freq * noteT) + 0.25 * sin(4.0 * PI * freq * noteT)) * env
                }
            }
            val sample = (sum / 3.0)
            buffer[i] = (sample * 24000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateUiClick(durationSec: Double): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / totalSamples
            val env = exp(-progress * 12.0)
            val sample = sin(2.0 * PI * 850.0 * t) * env
            buffer[i] = (sample * 16000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateGameOverCue(durationSec: Double): ShortArray {
        val notes = listOf(659.25, 587.33, 523.25, 440.00, 392.00)
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        val step = durationSec / notes.size
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val idx = (t / step).toInt().coerceIn(0, notes.size - 1)
            val freq = notes[idx]
            val noteT = t - idx * step
            val env = exp(-noteT * 4.0)
            val sample = (sin(2.0 * PI * freq * noteT) + 0.2 * cos(2.0 * PI * (freq * 0.5) * noteT)) * env
            buffer[i] = (sample * 22000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateRewardSound(durationSec: Double): ShortArray {
        val notes = listOf(440.0, 554.37, 659.25, 880.0)
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            var sum = 0.0
            for ((idx, freq) in notes.withIndex()) {
                val delay = idx * 0.05
                if (t >= delay) {
                    val noteT = t - delay
                    val env = exp(-noteT * 4.5)
                    sum += sin(2.0 * PI * freq * noteT) * env
                }
            }
            val sample = sum / 2.2
            buffer[i] = (sample * 25000.0).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }
}
