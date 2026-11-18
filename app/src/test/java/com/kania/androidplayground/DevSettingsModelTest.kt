package com.kania.androidplayground

import com.kania.androidplayground.ui.devsettings.DevSettingsState
import com.kania.androidplayground.ui.devsettings.ToggleResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DevSettingsModelTest {

    @Test
    fun defaultState_isCorrect() {
        val state = DevSettingsState()
        assertFalse(state.isDevSettingsEnabled)
        assertFalse(state.isAdbEnabled)
        assertFalse(state.hasPermission)
        assertEquals("", state.grantAdbCommand)
    }

    @Test
    fun customState_isCorrect() {
        val command = "adb shell pm grant com.test android.permission.WRITE_SECURE_SETTINGS"
        val state = DevSettingsState(
            isDevSettingsEnabled = true,
            isAdbEnabled = true,
            hasPermission = true,
            grantAdbCommand = command
        )
        assertTrue(state.isDevSettingsEnabled)
        assertTrue(state.isAdbEnabled)
        assertTrue(state.hasPermission)
        assertEquals(command, state.grantAdbCommand)
    }

    @Test
    fun toggleResult_holdsValues() {
        val successResult = ToggleResult(true, "성공")
        assertTrue(successResult.success)
        assertEquals("성공", successResult.message)

        val failResult = ToggleResult(false, "실패")
        assertFalse(failResult.success)
        assertEquals("실패", failResult.message)
    }
}
