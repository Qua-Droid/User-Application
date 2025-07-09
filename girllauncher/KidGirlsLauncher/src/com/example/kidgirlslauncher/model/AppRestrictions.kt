package com.example.kidgirlslauncher.model

data class AppRestrictions(
    val categories: Map<String, Boolean>,
    val appSpecific: Map<String, Boolean>
)
