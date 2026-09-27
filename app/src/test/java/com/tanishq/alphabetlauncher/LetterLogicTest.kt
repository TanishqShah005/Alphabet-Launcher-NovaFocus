package com.tanishq.alphabetlauncher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LetterLogicTest {

    @Test
    fun caseInsensitiveFirstLetterGrouping() {
        val labels = listOf("Gmail", "google", "Maps", "1Password")
        val gApps = labels.filter {
            it.firstOrNull()?.uppercaseChar() == 'G'
        }
        assertEquals(listOf("Gmail", "google"), gApps)
    }

    @Test
    fun emptyLetterProducesNoApps() {
        val labels = listOf("Gmail", "Maps", "Chrome")
        val apps = labels.filter {
            it.firstOrNull()?.uppercaseChar() == 'Z'
        }
        assertTrue(apps.isEmpty())
    }

    @Test
    fun letterPositionMapping() {
        fun map(y: Float, top: Float, height: Float): Char {
            val normalized = ((y - top) / height).coerceIn(0f, 0.99999f)
            return ('A'.code + (normalized * 26).toInt()).toChar()
        }

        assertEquals('A', map(0f, 0f, 2600f))
        assertEquals('M', map(1250f, 0f, 2600f))
        assertEquals('Z', map(2599f, 0f, 2600f))
    }
}
