package com.nathan.selah

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

data class BlockableApp(
    val name: String,
    val packageName: String
)

object AppLibrary {
    fun getInstalledApps(context: Context): List<BlockableApp> {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        val list = mutableListOf<BlockableApp>()
        val seen = mutableSetOf<String>()

        for (info in resolveInfos) {
            val pkg = info.activityInfo.packageName
            if (pkg == context.packageName) continue // ignore Selah itself
            if (seen.contains(pkg)) continue
            seen.add(pkg)

            val name = info.loadLabel(pm).toString()
            list.add(BlockableApp(name, pkg))
        }

        // Fallback default list if no installed apps found (e.g. unit tests / emulator quirks)
        if (list.isEmpty()) {
            return listOf(
                BlockableApp("Brawl Stars", "com.supercell.brawlstars"),
                BlockableApp("Instagram",   "com.instagram.android"),
                BlockableApp("TikTok",      "com.zhiliaoapp.musically"),
                BlockableApp("YouTube",     "com.google.android.youtube"),
                BlockableApp("Roblox",      "com.roblox.client"),
                BlockableApp("Minecraft",   "com.mojang.minecraftpe"),
                BlockableApp("Facebook",    "com.facebook.katana"),
                BlockableApp("Snapchat",    "com.snapchat.android"),
                BlockableApp("Twitter / X", "com.twitter.android"),
                BlockableApp("Reddit",      "com.reddit.frontpage")
            )
        }

        return list.sortedBy { it.name.lowercase() }
    }
}
