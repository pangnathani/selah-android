package com.nathan.selah

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.media.AudioManager
import android.media.AudioFocusRequest
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class SelahAccessibilityService : AccessibilityService() {

    private var activeBlockedPackage = ""
    private var isHardTimerActive = false
    private val mainHandler = Handler(Looper.getMainLooper())
    private var hardTimerRunnable: Runnable? = null
    private var lastShieldTriggerTime = 0L
    private var lastShieldTriggerPkg = ""
    private var lastOpenTime = 0L
    private var lastOpenPkg = ""

    private val ignoredSystemPackages = setOf(
        "com.android.systemui",
        "android",
        "com.google.android.inputmethod.latin",
        "com.sec.android.inputmethod",
        "com.samsung.android.honeyboard"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return
        
        // Ignore our own app & system UI / keyboards
        if (packageName == this.packageName || ignoredSystemPackages.contains(packageName)) return

        val blocked = SelahPrefs.getBlockedApps(this)

        if (blocked.contains(packageName)) {
            val isNewLaunch = (activeBlockedPackage != packageName)
            activeBlockedPackage = packageName

            if (isNewLaunch) {
                Log.d("Selah", "New launch of blocked app: $packageName")
                cancelHardTimer()

                val instantBlock = SelahPrefs.isInstantBlockEnabled(this)
                val openLimit = SelahPrefs.getOpenLimit(this).coerceAtLeast(1)
                
                val now = System.currentTimeMillis()
                val isDebouncedOpen = (packageName == lastOpenPkg && (now - lastOpenTime) < 1000L)
                val opens = if (isDebouncedOpen) {
                    SelahPrefs.getAppOpenCount(this, packageName)
                } else {
                    lastOpenPkg = packageName
                    lastOpenTime = now
                    SelahPrefs.incrementAppOpen(this, packageName)
                }

                val shouldBlockOnOpen = instantBlock || (openLimit == 1) || (opens % openLimit == 0)

                if (shouldBlockOnOpen && !SelahPrefs.isAppBypassed(this, packageName)) {
                    triggerShield(packageName)
                    return
                }
            }

            // Guarantee hardware timer is running continuously while in this app
            if (!isHardTimerActive) {
                startHardTimer(packageName)
            }

        } else {
            // User left the blocked app (went to Home launcher or another app)
            if (activeBlockedPackage.isNotEmpty()) {
                Log.d("Selah", "Left blocked app $activeBlockedPackage -> now in $packageName")
                // Clear temporary bypass so re-opening the app immediately blocks on open 1!
                SelahPrefs.setBypassUntil(this, activeBlockedPackage, 0L)
                activeBlockedPackage = ""
                cancelHardTimer()
            }
        }
    }

    private fun startHardTimer(packageName: String) {
        cancelHardTimer()
        val timeLimitMins = SelahPrefs.getTimeLimit(this).coerceAtLeast(1)
        val delayMs = timeLimitMins * 60 * 1000L
        isHardTimerActive = true
        Log.d("Selah", "Hard timer started for $packageName: $delayMs ms ($timeLimitMins min)")

        hardTimerRunnable = Runnable {
            isHardTimerActive = false
            if (activeBlockedPackage == packageName) {
                val bypassRemaining = SelahPrefs.getBypassRemaining(this, packageName)
                if (bypassRemaining > 0) {
                    Log.d("Selah", "Hard timer fired but app is bypassed for $bypassRemaining ms more. Delaying block.")
                    isHardTimerActive = true
                    mainHandler.postDelayed(hardTimerRunnable!!, bypassRemaining)
                } else {
                    Log.d("Selah", "HARD TIMER EXPIRED! Intercepting $packageName now.")
                    triggerShield(packageName)
                }
            }
        }
        mainHandler.postDelayed(hardTimerRunnable!!, delayMs)
    }

    private fun cancelHardTimer() {
        hardTimerRunnable?.let { mainHandler.removeCallbacks(it) }
        hardTimerRunnable = null
        isHardTimerActive = false
    }

    private fun triggerShield(packageName: String) {
        val now = System.currentTimeMillis()
        if (now - lastShieldTriggerTime < 2000L && lastShieldTriggerPkg == packageName) {
            Log.d("Selah", "Debouncing shield trigger for $packageName")
            return
        }
        lastShieldTriggerTime = now
        lastShieldTriggerPkg = packageName
        cancelHardTimer()

        val intent = Intent(this, ShieldActivity::class.java).apply {
            putExtra("BLOCKED_PACKAGE", packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        // Pause any playing media (like YouTube) to prevent Picture-in-Picture mode
        val audioManager = getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val focusRequest = android.media.AudioFocusRequest.Builder(android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE).build()
            audioManager.requestAudioFocus(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(null, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
        }

        startActivity(intent)
    }

    override fun onInterrupt() {}
}
