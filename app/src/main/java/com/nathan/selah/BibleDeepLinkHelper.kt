package com.nathan.selah

import android.content.Context
import android.content.Intent
import android.net.Uri

object BibleDeepLinkHelper {
    private val osisMap = mapOf(
        "Genesis" to "GEN", "Exodus" to "EXO", "Leviticus" to "LEV", "Numbers" to "NUM", "Deuteronomy" to "DEU",
        "Joshua" to "JOS", "Judges" to "JDG", "Ruth" to "RUT", "1 Samuel" to "1SA", "2 Samuel" to "2SA",
        "1 Kings" to "1KI", "2 Kings" to "2KI", "1 Chronicles" to "1CH", "2 Chronicles" to "2CH", "Ezra" to "EZR",
        "Nehemiah" to "NEH", "Esther" to "EST", "Job" to "JOB", "Psalm" to "PSA", "Psalms" to "PSA", "Proverbs" to "PRO",
        "Ecclesiastes" to "ECC", "Song of Solomon" to "SNG", "Isaiah" to "ISA", "Jeremiah" to "JER", "Lamentations" to "LAM",
        "Ezekiel" to "EZK", "Daniel" to "DAN", "Hosea" to "HOS", "Joel" to "JOL", "Amos" to "AMO", "Obadiah" to "OBA",
        "Jonah" to "JON", "Micah" to "MIC", "Nahum" to "NAM", "Habakkuk" to "HAB", "Zephaniah" to "ZEP", "Haggai" to "HAG",
        "Zechariah" to "ZEC", "Malachi" to "MAL",
        "Matthew" to "MAT", "Mark" to "MRK", "Luke" to "LUK", "John" to "JHN", "Acts" to "ACT", "Romans" to "ROM",
        "1 Corinthians" to "1CO", "2 Corinthians" to "2CO", "Galatians" to "GAL", "Ephesians" to "EPH", "Philippians" to "PHP",
        "Colossians" to "COL", "1 Thessalonians" to "1TH", "2 Thessalonians" to "2TH", "1 Timothy" to "1TI", "2 Timothy" to "2TI",
        "Titus" to "TIT", "Philemon" to "PHM", "Hebrews" to "HEB", "James" to "JAS", "1 Peter" to "1PE", "2 Peter" to "2PE",
        "1 John" to "1JN", "2 John" to "2JN", "3 John" to "3JN", "Jude" to "JUD", "Revelation" to "REV"
    )

    fun openVerse(context: Context, reference: String) {
        val osis = convertToOsis(reference)
        
        // If we successfully parsed it, try to deep link
        if (osis != null) {
            val deepLinkUri = Uri.parse("youversion://bible?reference=$osis")
            val intent = Intent(Intent.ACTION_VIEW, deepLinkUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            try {
                // Check if YouVersion is installed
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // Fallback to web link if app isn't installed or scheme isn't supported
                val webUri = Uri.parse("https://www.bible.com/bible/111/$osis")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try { context.startActivity(webIntent) } catch (e2: Exception) {}
                return
            }
        }
        
        // If parsing fails, fallback to search URL
        val searchUri = Uri.parse("https://www.bible.com/search/bible?q=${Uri.encode(reference)}")
        val searchIntent = Intent(Intent.ACTION_VIEW, searchUri)
        searchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try { context.startActivity(searchIntent) } catch (e: Exception) {}
    }

    private fun convertToOsis(reference: String): String? {
        // Regex to match "Book Chapter:Verse" (e.g., "1 Thessalonians 5:16-18")
        val regex = Regex("^(.+?)\\s+(\\d+):(.+)$")
        val match = regex.find(reference.trim()) ?: return null
        
        val bookName = match.groupValues[1].trim()
        val chapter = match.groupValues[2].trim()
        val verse = match.groupValues[3].trim()
        
        val osisBook = osisMap[bookName] ?: return null
        
        return "$osisBook.$chapter.$verse"
    }
}
