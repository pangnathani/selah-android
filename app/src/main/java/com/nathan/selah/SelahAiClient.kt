package com.nathan.selah

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object SelahAiClient {

    private const val GLOO_AI_STUDIO_URL = "https://api.gloo.us/v1/chat/completions"

    suspend fun getReflection(
        context: Context,
        selectionType: String,
        journalText: String,
        userBackendUrl: String
    ): Triple<String, String, String> {
        val language = SelahPrefs.getLanguage(context)
        val bibleVersion = SelahPrefs.getBibleVersion(context)

        return withContext(Dispatchers.IO) {
            // Step 1: Custom Gloo AI + YouVersion Backend
            if (userBackendUrl.isNotBlank() && !userBackendUrl.contains("10.0.2.2")) {
                try {
                    val customResult = callGlooYouVersionBackend(userBackendUrl, selectionType, journalText, language, bibleVersion)
                    if (customResult != null) return@withContext customResult
                } catch (e: Exception) {
                    Log.w("SelahAI", "Gloo AI Backend call failed: ${e.message}")
                }
            }

            // Step 2: Direct Gloo AI Studio API with Emotionally Intelligent Personalization
            try {
                val glooAiResult = callGlooAiStudioDirect(selectionType, journalText, language, bibleVersion)
                if (glooAiResult != null) return@withContext glooAiResult
            } catch (e: Exception) {
                Log.w("SelahAI", "Direct Gloo AI Studio call failed: ${e.message}")
            }

            // Step 3: Natural Human Fallback Engine
            getFallback(selectionType, journalText, language, bibleVersion)
        }
    }

    private suspend fun callGlooYouVersionBackend(urlStr: String, selectionType: String, journalText: String, language: String, bibleVersion: String): Triple<String, String, String>? {
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val reqJson = JSONObject().apply {
            put("selection_type", selectionType)
            put("journal_text", journalText)
        }

        conn.outputStream.use { os ->
            val input = reqJson.toString().toByteArray(Charsets.UTF_8)
            os.write(input, 0, input.size)
        }

        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
            val json = JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
            
            val mappedVersion = if (DailyVersesDB.versesByVersion.containsKey(bibleVersion)) bibleVersion else "NIV"
            val verseList = DailyVersesDB.versesByVersion[mappedVersion]!!
            val (selectedVerse, selectedReference) = verseList.random()
            
            var reflection = json.optString("reflection", "")
            
            if (language != "English") {
                reflection = translateText(reflection, language)
            }
            
            return Triple(selectedReference, selectedVerse, reflection)
        }
        return null
    }

        private suspend fun callGlooAiStudioDirect(selectionType: String, journalText: String, language: String, bibleVersion: String): Triple<String, String, String>? {
        // Replace with your Gloo AI Studio credentials
        val clientId = com.nathan.selah.BuildConfig.GLOO_CLIENT_ID
        val clientSecret = com.nathan.selah.BuildConfig.GLOO_CLIENT_SECRET
        
        var accessToken = ""
        try {
            val creds = "$clientId:$clientSecret"
            val basicAuth = android.util.Base64.encodeToString(creds.toByteArray(), android.util.Base64.NO_WRAP)
            val tokenUrl = URL("https://platform.ai.gloo.com/oauth2/token")
            val tokenConn = tokenUrl.openConnection() as HttpURLConnection
            tokenConn.requestMethod = "POST"
            tokenConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            tokenConn.setRequestProperty("Authorization", "Basic $basicAuth")
            tokenConn.doOutput = true
            
            val postData = "grant_type=client_credentials&scope=api/access".toByteArray(Charsets.UTF_8)
            tokenConn.outputStream.use { os -> os.write(postData) }
            
            if (tokenConn.responseCode == 200) {
                val resp = JSONObject(tokenConn.inputStream.bufferedReader().use { it.readText() })
                accessToken = resp.optString("access_token")
            } else {
                android.util.Log.e("SelahAI", "Gloo Auth failed: ${tokenConn.responseCode}")
                return null
            }
        } catch (e: Exception) {
            android.util.Log.e("SelahAI", "Gloo Auth Exception", e)
            return null
        }
        
        if (accessToken.isEmpty()) return null

        val seed = java.util.UUID.randomUUID().toString()
        var prompt = ""
        
        val diversityNote = "\\n\\nIMPORTANT (seed: $seed):\\n" +
            "• Pick from the ENTIRE Bible.\\n" +
            "• Avoid repeated verses like John 3:16, Jeremiah 29:11, Philippians 4:6.\\n" +
            "Use the $bibleVersion translation. Write ALL JSON values in English. We will translate the reflection later.\\n" +
            "Return EXACTLY this JSON object:\\n" +
            "{\"reference\": \"Book Chapter:Verse\", \"verse_text\": \"Exact word-for-word verse...\", \"reflection\": \"...\"}"
            
        if (selectionType == "troubled_or_stressed" || selectionType == "something_else") {
            val entry = if (journalText.isNotBlank()) journalText else "(nothing written)"
            prompt = "You are a deeply empathetic digital wellbeing guide backed by Scripture.\\n" +
                "Read the user's journal entry: \"$entry\".\\n" +
                "Choose a verse that speaks precisely to THAT specific worry or emotion.\\n" +
                "Write the reflection (12–18 words) as a warm, specific response addressing what the user told you — never generic.\\n" + diversityNote
        } else if (selectionType == "mindless_habit") {
            prompt = "The user is stuck in a dopamine loop of mindless scrolling.\\n" +
                "Choose a verse about purposeful living, renewing the mind, or diligence.\\n" +
                "Write the reflection (12–18 words) as a gentle challenge to put the phone down and live intentionally.\\n" + diversityNote
        } else {
            prompt = "The user opened a blocked app but said there's nothing wrong.\\n" +
                "SURPRISE them with a fascinating, poetic, or unusual verse (Psalms, Proverbs, Prophets).\\n" +
                "Write the reflection (12–18 words) as a beautiful, unexpected blessing.\\n" + diversityNote
        }

        val jsonBody = JSONObject().apply {
            put("auto_routing", true)
            put("temperature", 0.95)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", prompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", "Give me the verse now.")
                })
            })
        }.toString()

        val url = URL("https://platform.ai.gloo.com/ai/v2/chat/completions")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $accessToken")
        conn.connectTimeout = 8000
        conn.readTimeout = 12000
        conn.doOutput = true

        OutputStreamWriter(conn.outputStream).use { it.write(jsonBody); it.flush() }

        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
            val raw = conn.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(raw)
            val choices = root.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val message = choices.getJSONObject(0).optJSONObject("message")
                val content = message?.optString("content") ?: ""
                if (content.isNotBlank()) {
                    var cleaned = content.trim()
                    try {
                        var jsonStr = cleaned
                        val startIdx = cleaned.indexOf("{")
                        val endIdx = cleaned.lastIndexOf("}")
                        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
                            jsonStr = cleaned.substring(startIdx, endIdx + 1)
                        }
                        val json = JSONObject(jsonStr)
                        var reference = json.optString("reference", "Proverbs 3:5-6")
                        var verseText = json.optString("verse_text", "Trust in the Lord with all your heart, and do not lean on your own understanding.")
                        var reflection = json.optString("reflection", "Taking a moment for yourself is a great step. Let's refocus on what truly matters.")
                        
                        if (language != "English") {
                            reflection = translateText(reflection, language)
                        }
                        
                        return Triple(reference, verseText, reflection)
                    } catch (e: Exception) {
                        android.util.Log.e("SelahAI", "JSON Parse Error on Gloo AI Response", e)
                    }
                }
            }
        }
        return null
    }

    private suspend fun getFallback(selectionType: String, journalText: String, language: String, bibleVersion: String): Triple<String, String, String> {
        val mappedVersion = if (DailyVersesDB.versesByVersion.containsKey(bibleVersion)) bibleVersion else "NIV"
        val verseList = DailyVersesDB.versesByVersion[mappedVersion]!!
        val (selectedVerse, selectedReference) = verseList.random()

        var reflection = "Taking a moment for yourself is a great step. Let's refocus on what truly matters."
        if (language != "English") {
            reflection = translateText(reflection, language)
        }
        return Triple(selectedReference, selectedVerse, reflection)
    }

    suspend fun translateText(text: String, targetLanguage: String): String {
        val langCode = when (targetLanguage) {
            "Spanish" -> "es"
            "Korean" -> "ko"
            "Chinese" -> "zh-CN"
            "Portuguese" -> "pt"
            "French" -> "fr"
            else -> return text
        }
        
        return withContext(Dispatchers.IO) {
            try {
                val encodedText = java.net.URLEncoder.encode(text, "UTF-8")
                val url = URL("https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=$langCode&dt=t&q=$encodedText")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                conn.connectTimeout = 4000
                conn.readTimeout = 6000

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val raw = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(raw)
                    val resultParts = jsonArray.getJSONArray(0)
                    val sb = StringBuilder()
                    for (i in 0 until resultParts.length()) {
                        sb.append(resultParts.getJSONArray(i).getString(0))
                    }
                    return@withContext sb.toString().trim()
                } else {
                    android.util.Log.e("SelahAI", "Google Translate API failed with code: ${conn.responseCode}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SelahAI", "Translation failed", e)
            }
            text // fallback to original text
        }
    }
}

