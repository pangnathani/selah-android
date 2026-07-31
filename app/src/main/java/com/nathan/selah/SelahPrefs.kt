package com.nathan.selah

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

// ── Reflection Log Record Data Model ──────────────────────────────────────────
data class ReflectionRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val choiceType: String,
    val choiceLabel: String,
    val journalText: String,
    val category: String,
    val appPackage: String = ""
)

// ── Typo & Category Normalizer ───────────────────────────────────────────────
object CategoryNormalizer {
    fun normalize(choiceType: String, journalText: String): String {
        val text = journalText.lowercase().trim()

        if (text.isNotBlank()) {
            when {
                // Finances & Money (Handles typos like spensing, biuy, shoppin, etc.)
                text.contains("money") || text.contains("spend") || text.contains("spens") ||
                text.contains("buy") || text.contains("biuy") || text.contains("shop") ||
                text.contains("cost") || text.contains("pay") || text.contains("cash") ||
                text.contains("shoe") || text.contains("cloth") || text.contains("price") -> return "Finances & Spending"

                // Stress & Anxiety (Handles typos like stres, anx, worri, etc.)
                text.contains("stres") || text.contains("anx") || text.contains("worry") ||
                text.contains("worri") || text.contains("fear") || text.contains("scared") ||
                text.contains("overwhelm") || text.contains("troubl") || text.contains("sad") ||
                text.contains("depress") || text.contains("panic") -> return "Stress & Anxiety"

                // Academics & School (Handles typos like exm, tst, scholl, etc.)
                text.contains("exam") || text.contains("exm") || text.contains("test") ||
                text.contains("tst") || text.contains("school") || text.contains("scholl") ||
                text.contains("study") || text.contains("homework") || text.contains("grad") ||
                text.contains("class") -> return "Academics & Study"

                // Habit & Distraction (Handles scroll, bored, game, etc.)
                text.contains("habit") || text.contains("bored") || text.contains("scroll") ||
                text.contains("game") || text.contains("play") || text.contains("mindless") -> return "Habit & Scrolling"
            }
        }

        return when (choiceType.uppercase()) {
            "TROUBLED" -> "Stress & Anxiety"
            "HABIT" -> "Habit & Scrolling"
            "SOMETHING_ELSE" -> "Personal Reflection"
            "NOTHING" -> "Quick Pause"
            "SUGGESTED" -> "Suggested Guidance"
            else -> "Mindful Reflection"
        }
    }

    fun getCachedTranslation(context: Context, englishText: String, targetLanguage: String): String? {
        val prefs = context.getSharedPreferences("selah_translations", Context.MODE_PRIVATE)
        return prefs.getString("${englishText}_${targetLanguage}", null)
    }

    fun saveCachedTranslation(context: Context, englishText: String, targetLanguage: String, translatedText: String) {
        val prefs = context.getSharedPreferences("selah_translations", Context.MODE_PRIVATE)
        prefs.edit().putString("${englishText}_${targetLanguage}", translatedText).apply()
    }
}



// ── Shared Preferences Repository ─────────────────────────────────────────────
object SelahPrefs {
    private const val PREFS = "selah_prefs"
    private const val KEY_BLOCKED = "blocked_apps"
    private const val KEY_OPEN_LIMIT = "open_limit"
    private const val KEY_TIME_LIMIT = "time_limit"
    private const val KEY_BACKEND_URL = "backend_url"
    private const val KEY_INSTANT_BLOCK = "instant_block"
    private const val KEY_TOTAL_REFLECTIONS = "total_reflections"
    private const val KEY_STREAK_DAYS = "streak_days"
    private const val KEY_LAST_STREAK_DATE = "last_streak_date"
    private const val KEY_LAST_ARMOR_DATE = "selah_last_armor_date"
    private const val KEY_HISTORY_JSON = "reflection_history_json"
    private const val KEY_APP_LANGUAGE = "app_language"
    private const val KEY_BIBLE_VERSION = "bible_version"
    private const val KEY_SWORD_DATE = "sword_date"
    private const val KEY_BELT_DATE = "belt_date"
    private const val KEY_HELMET_DATE = "helmet_date"

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    fun getBlockedApps(context: Context): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(KEY_BLOCKED, emptySet()) ?: emptySet()

    fun setBlockedApps(context: Context, apps: Set<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putStringSet(KEY_BLOCKED, apps).apply()
    }

    fun getOpenLimit(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_OPEN_LIMIT, 10)

    fun setOpenLimit(context: Context, limit: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_OPEN_LIMIT, limit).apply()
    }

    fun getTimeLimit(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_TIME_LIMIT, 1)

    fun setTimeLimit(context: Context, limit: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_TIME_LIMIT, limit).apply()
    }

    fun getBackendUrl(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_BACKEND_URL, "http://10.0.2.2:8000") ?: "http://10.0.2.2:8000"

    fun setBackendUrl(context: Context, url: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_BACKEND_URL, url).apply()
    }

    fun isInstantBlockEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_INSTANT_BLOCK, false)

    fun setInstantBlockEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_INSTANT_BLOCK, enabled).apply()
    }


    fun getLanguage(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_APP_LANGUAGE, "English") ?: "English"

    fun setLanguage(context: Context, language: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_APP_LANGUAGE, language).apply()
    }

    fun getBibleVersion(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_BIBLE_VERSION, "NIV") ?: "NIV"

    fun setBibleVersion(context: Context, version: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_BIBLE_VERSION, version).apply()
    }

    fun getTotalReflections(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_TOTAL_REFLECTIONS, 0)

    // ── Mindful Choice & Reflection History Logging ──────────────────────────

    fun recordReflection(
        context: Context,
        choiceType: String,
        choiceLabel: String,
        journalText: String,
        appPackageName: String?,
        wasMindful: Boolean = true
    ) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()
        val lastDate = prefs.getString(KEY_LAST_STREAK_DATE, "") ?: ""
        var currentStreak = prefs.getInt(KEY_STREAK_DAYS, 0)

        val newStreak = if (wasMindful) {
            when (lastDate) {
                today -> currentStreak
                yesterday -> currentStreak + 1
                else -> 1
            }
        } else {
            currentStreak
        }

        val currentReflections = prefs.getInt(KEY_TOTAL_REFLECTIONS, 0) + 1

        // Normalize category with typo handling
        val category = CategoryNormalizer.normalize(choiceType, journalText)

        // Create Reflection Record
        val record = ReflectionRecord(
            choiceType = choiceType,
            choiceLabel = choiceLabel,
            journalText = journalText,
            category = category,
            appPackage = appPackageName ?: ""
        )

        // Append to JSON history array
        val history = getReflectionHistory(context).toMutableList()
        history.add(0, record) // Newest first

        val jsonArray = JSONArray()
        history.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("timestamp", item.timestamp)
                put("choiceType", item.choiceType)
                put("choiceLabel", item.choiceLabel)
                put("journalText", item.journalText)
                put("category", item.category)
                put("appPackage", item.appPackage)
            }
            jsonArray.put(obj)
        }

        val edit = prefs.edit()
            .putInt(KEY_TOTAL_REFLECTIONS, currentReflections)
            .putString(KEY_HISTORY_JSON, jsonArray.toString())
            
        if (wasMindful) {
            edit.putInt(KEY_STREAK_DAYS, newStreak)
                .putString(KEY_LAST_STREAK_DATE, today)
        }
        edit.commit()
    }

    fun recordMindfulChoice(context: Context, appPackageName: String?) {
        // Fallback backward compatibility call
        recordReflection(context, "MINDFUL", "Mindful Decision", "", appPackageName)
    }

    
    fun checkAndResetArmor(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val lastDate = prefs.getString(KEY_LAST_ARMOR_DATE, "")
        if (lastDate != today) {
            prefs.edit().apply {
                putString(KEY_BELT_DATE, "")
                putString(KEY_SWORD_DATE, "")
                putInt("selah_armor_breastplate", 0)
                putInt("selah_armor_shield", 0)
                putString(KEY_HELMET_DATE, "")
                putString(KEY_LAST_ARMOR_DATE, today)
                apply()
            }
        }
    }

    fun getStreakDays(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val lastDate = prefs.getString(KEY_LAST_STREAK_DATE, "") ?: ""
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()

        if (lastDate.isNotEmpty() && lastDate != today && lastDate != yesterday) {
            prefs.edit().putInt(KEY_STREAK_DAYS, 0).apply()
            return 0
        }

        return prefs.getInt(KEY_STREAK_DAYS, 0)
    }

    fun getReflectionHistory(context: Context): List<ReflectionRecord> {
        val rawJson = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_HISTORY_JSON, "[]") ?: "[]"
        val list = mutableListOf<ReflectionRecord>()
        try {
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ReflectionRecord(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        choiceType = obj.optString("choiceType", "MINDFUL"),
                        choiceLabel = obj.optString("choiceLabel", "Mindful Pause"),
                        journalText = obj.optString("journalText", ""),
                        category = obj.optString("category", "Mindful Reflection"),
                        appPackage = obj.optString("appPackage", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    // ── Dynamic Suggestion Generator ─────────────────────────────────────────

    fun getSuggestedCategory(context: Context): Pair<String, String> {
        val history = getReflectionHistory(context)
        if (history.isEmpty()) {
            return Pair(context.getString(R.string.str_moment_of_stillness), "Mindful Reflection")
        }

        // Count category frequency
        val counts = mutableMapOf<String, Int>()
        history.forEach { record ->
            val cat = record.category
            counts[cat] = (counts[cat] ?: 0) + 1
        }

        val topCategory = counts.maxByOrNull { it.value }?.key ?: "Mindful Reflection"
        
        val localizedName = when (topCategory) {
            "Stress & Anxiety" -> context.getString(R.string.str_troubled_stressed)
            "Habit & Scrolling" -> context.getString(R.string.str_mindless_habit)
            "Quick Pause" -> context.getString(R.string.str_nothing_just_opened)
            "Personal Reflection" -> context.getString(R.string.str_something_else_mind)
            "Finances & Spending" -> "Finances"
            "Academics & Study" -> "Academics"
            "Suggested Guidance" -> "Guidance"
            else -> context.getString(R.string.str_moment_of_stillness)
        }
        
        // We will no longer prefix it with "Suggested:" because str_suggested_label adds the prefix/emoji in strings.xml
        return Pair(localizedName, topCategory)
    }

    // ── App Open Counters ───────────────────────────────────────────────────

    fun incrementAppOpen(context: Context, packageName: String): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getInt("opens_$packageName", 0) + 1
        prefs.edit().putInt("opens_$packageName", current).apply()
        return current
    }


    // ── Armor of God ────────────────────────────────────────────────────────

    fun equipSword(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_SWORD_DATE, getTodayDateString()).apply()
    }

    fun hasSword(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_SWORD_DATE, "") == getTodayDateString()

    fun equipBelt(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_BELT_DATE, getTodayDateString()).apply()
    }

    fun hasBelt(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_BELT_DATE, "") == getTodayDateString()

    fun equipHelmet(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_HELMET_DATE, getTodayDateString()).apply()
    }

    fun hasHelmet(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_HELMET_DATE, "") == getTodayDateString()

    // Breastplate is implicitly if they wrote a journal today
    fun hasBreastplate(context: Context): Boolean {
        val history = getReflectionHistory(context)
        val todayStart = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.timeInMillis
        return history.any { it.timestamp > todayStart && it.journalText.isNotBlank() }
    }

    // Shield is implicitly if they did a mindful choice today
    fun hasShield(context: Context): Boolean {
        val history = getReflectionHistory(context)
        val todayStart = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.timeInMillis
        return history.any { it.timestamp > todayStart }
    }

    // Shoes is implicitly if streak >= 2
    fun hasShoes(context: Context): Boolean = getStreakDays(context) >= 2

    fun getArmorCount(context: Context): Int {
        var count = 0
        if (hasSword(context)) count++
        if (hasBelt(context)) count++
        if (hasHelmet(context)) count++
        if (hasBreastplate(context)) count++
        if (hasShield(context)) count++
        if (hasShoes(context)) count++
        return count
    }

    fun getAppOpenCount(context: Context, packageName: String): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("opens_$packageName", 0)
    }

    // ── Bypass Window ────────────────────────────────────────────────────────

    fun setBypassUntil(context: Context, packageName: String, untilMillis: Long) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putLong("bypass_until_$packageName", untilMillis).apply()
    }

        fun isAppBypassed(context: Context, packageName: String): Boolean {
        return getBypassRemaining(context, packageName) > 0
    }

    fun getBypassRemaining(context: Context, packageName: String): Long {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val bypassUntil = prefs.getLong("bypass_until_$packageName", 0L)
        return bypassUntil - System.currentTimeMillis()
    }

    fun getCachedTranslation(context: Context, englishText: String, targetLanguage: String): String? {
        val prefs = context.getSharedPreferences("selah_translations", Context.MODE_PRIVATE)
        return prefs.getString("${englishText}_${targetLanguage}", null)
    }

    fun saveCachedTranslation(context: Context, englishText: String, targetLanguage: String, translatedText: String) {
        val prefs = context.getSharedPreferences("selah_translations", Context.MODE_PRIVATE)
        prefs.edit().putString("${englishText}_${targetLanguage}", translatedText).apply()
    }
}
