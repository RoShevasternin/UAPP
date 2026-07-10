package com.rbxrush.rushrbx.game.manager.util

import com.badlogic.gdx.audio.Sound
import com.rbxrush.rushrbx.game.manager.AudioManager
import com.rbxrush.rushrbx.game.manager.SoundManager

class SoundUtil {

    val CLICK     = AdvancedSound(SoundManager.EnumSound.CLICK.data.sound, 1.0f)
    val CHECK_BOX = AdvancedSound(SoundManager.EnumSound.CHECK_BOX.data.sound, 0.65f)
    val REWARD    = AdvancedSound(SoundManager.EnumSound.REWARD.data.sound, 0.05f)

    // 0..100
    var volumeLevel = AudioManager.volumeLevelPercent

    var isPause = (volumeLevel <= 0f)

    fun play(advancedSound: AdvancedSound, playCoff: Float = 1f) {
        if (isPause.not()) {
            advancedSound.apply {
                sound.play(((volumeLevel / 100f) * coff) * playCoff)
            }
        }
    }

    data class AdvancedSound(val sound: Sound, val coff: Float)
}