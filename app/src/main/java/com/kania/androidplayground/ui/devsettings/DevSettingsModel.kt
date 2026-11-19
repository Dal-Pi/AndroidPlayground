package com.kania.androidplayground.ui.devsettings

data class DevSettingsState(
    val isDevSettingsEnabled: Boolean = false,
    val isAdbEnabled: Boolean = false,
    val hasPermission: Boolean = false,
    val grantAdbCommand: String = "",
    val revokeAdbCommand: String = ""
)

data class ToggleResult(
    val success: Boolean,
    val message: String
)
