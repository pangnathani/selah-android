package com.nathan.selah

import androidx.compose.ui.graphics.Color

val SelahBackground = Color(0xFF0F0F17)  // near-black
val SelahSurface    = Color(0xFF1C1C26)  // slightly lighter
val SelahSurfaceGlass = Color(0x661C1C26) // Translucent glass surface
val SelahSurfaceHighlight = Color(0xFF282836) // Lighter surface for hover/press states
val SelahPrimary    = Color(0xFFF3E9D2)  // warm parchment
val SelahAccent     = Color(0xFF8CC7B8)  // muted teal
val SelahAccentTranslucent = Color(0x338CC7B8) // for backgrounds of tinted items
val SelahMuted      = Color(0xFF807D8C)  // soft grey
val SelahDivider    = Color(0xFF2A2A38)  // subtle divider

fun appAccentColor(name: String): Color = when {
    name.contains("Instagram")  -> Color(0xFFE1306C)
    name.contains("TikTok")     -> Color(0xFF69C9D0)
    name.contains("YouTube")    -> Color(0xFFFF0000)
    name.contains("Facebook")   -> Color(0xFF1877F2)
    name.contains("Snapchat")   -> Color(0xFFFFFC00)
    name.contains("Twitter")    -> Color(0xFF1DA1F2)
    name.contains("Reddit")     -> Color(0xFFFF4500)
    name.contains("Pinterest")  -> Color(0xFFE60023)
    name.contains("LinkedIn")   -> Color(0xFF0077B5)
    name.contains("Netflix")    -> Color(0xFFE50914)
    name.contains("Twitch")     -> Color(0xFF9146FF)
    name.contains("Discord")    -> Color(0xFF5865F2)
    name.contains("WhatsApp")   -> Color(0xFF25D366)
    name.contains("Telegram")   -> Color(0xFF229ED9)
    name.contains("Hulu")       -> Color(0xFF1CE783)
    name.contains("Disney")     -> Color(0xFF113CCF)
    name.contains("Roblox")     -> Color(0xFFFF4D00)
    name.contains("Minecraft")  -> Color(0xFF699B30)
    else                        -> SelahAccent
}
