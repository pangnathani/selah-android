package com.nathan.selah

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import kotlinx.coroutines.delay

import android.content.Intent
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Done

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.ui.res.stringResource
import com.nathan.selah.R
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.nathan.selah.theme.SelahTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SlideInUpAnimated(delayMs: Int = 0, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(dampingRatio = 0.65f, stiffness = 250f)
        ) + fadeIn(tween(300))
    ) {
        content()
    }
}

// ── Navigation Tabs ───────────────────────────────────────────────────────────
private enum class HomeTab(val labelResId: Int) {
    HOME(R.string.str_home_tab),
    GUARD(R.string.str_guard_tab),
    ARMOR(R.string.str_armor_tab),
    JOURNAL(R.string.str_data_tab),
    SETTINGS(R.string.str_settings_tab)
}

private enum class TimeFilter(val titleResId: Int, val count: Int?) {
    PAST_5(R.string.str_past_5, 5),
    PAST_10(R.string.str_past_10, 10),
    ALL_TIME(R.string.str_all_time, null)
}

class MainActivity : androidx.appcompat.app.AppCompatActivity() {

    private var isServiceEnabled = mutableStateOf(false)
    private var blockedApps = mutableStateOf(setOf<String>())
    private var totalReflections = mutableStateOf(0)
    private var streakDays = mutableStateOf(0)

    override fun onResume() {
        super.onResume()
        SelahPrefs.checkAndResetArmor(this)
        isServiceEnabled.value = checkAccessibilityService()
        blockedApps.value = SelahPrefs.getBlockedApps(this)
        totalReflections.value = SelahPrefs.getTotalReflections(this)
        streakDays.value = SelahPrefs.getStreakDays(this)
    }

    private fun checkAccessibilityService(): Boolean {
        val enabled = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        return enabled.contains(packageName)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val savedLang = SelahPrefs.getLanguage(this)
        val localeTag = when (savedLang) {
            "Spanish" -> "es"
            "Korean" -> "ko"
            "Chinese" -> "zh"
            "Portuguese" -> "pt"
            "French" -> "fr"
            else -> "en"
        }
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags(localeTag))

        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(SelahBackground.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(SelahBackground.toArgb())
        )
        setContent {
            SelahTheme {
                AnimatedContent(
                    targetState = isServiceEnabled.value,
                    transitionSpec = { fadeIn(tween(700)) togetherWith fadeOut(tween(500)) },
                    label = "main"
                ) { enabled ->
                    if (enabled) {
                        HomeScreen(
                            initialBlocked   = blockedApps.value,
                            reflectionsCount = totalReflections.value,
                            currentStreak    = streakDays.value,
                            onSaveBlocked    = { selected ->
                                SelahPrefs.setBlockedApps(this@MainActivity, selected)
                                blockedApps.value = selected
                            }
                        )
                    } else {
                        SetupScreen(onEnable = {
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        })
                    }
                }
            }
        }
    }
}

// ── Home Screen Container with Navigation Bar ─────────────────────────────────

@Composable
private fun HomeScreen(
    initialBlocked: Set<String>,
    reflectionsCount: Int,
    currentStreak: Int,
    onSaveBlocked: (Set<String>) -> Unit
) {
    var activeTab by remember { mutableStateOf(HomeTab.HOME) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SelahBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(250)) },
                    label = "tab"
                ) { tab ->
                    when (tab) {
                        HomeTab.HOME     -> LandingHomeTab(initialBlocked, reflectionsCount, currentStreak, onNavigateToGuard = { activeTab = HomeTab.GUARD })
                        HomeTab.GUARD    -> AppGuardTab(initialBlocked, onSaveBlocked)
                        HomeTab.ARMOR    -> ArmorTabUI(LocalContext.current)
                        HomeTab.JOURNAL  -> QuietTimeTab(reflectionsCount, currentStreak)
                        HomeTab.SETTINGS -> SettingsTab()
                    }
                }
            }

            // Glassmorphic Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
                    .background(SelahSurface)
                    .border(0.5.dp, SelahDivider, RoundedCornerShape(0.dp)),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeTab.values().forEach { tab ->
                    val selected = activeTab == tab
                    val color = if (selected) SelahAccent else SelahMuted

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .bouncyClick { activeTab = tab },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        when (tab) {
                            HomeTab.HOME     -> IconHome(tint = color, size = 20.dp)
                            HomeTab.GUARD    -> IconShield(tint = color, size = 20.dp)
                            HomeTab.ARMOR    -> androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Filled.Security, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                            HomeTab.JOURNAL  -> IconBook(tint = color, size = 20.dp)
                            HomeTab.SETTINGS -> IconSettings(tint = color, size = 20.dp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(tab.labelResId),
                            fontSize = 11.sp,
                            color = color,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Light
                        )
                    }
                }
            }
        }
    }
}

// ── Tab 1: Default Landing Home Page ──────────────────────────────────────────

@Composable
private fun LandingHomeTab(
    blockedApps: Set<String>,
    totalReflections: Int,
    streakDays: Int,
    onNavigateToGuard: () -> Unit
) {
    val context = LocalContext.current
    val language = SelahPrefs.getLanguage(context)
    val openLimit      = remember { SelahPrefs.getOpenLimit(context) }
    val timeLimit      = remember { SelahPrefs.getTimeLimit(context) }
    val isShieldActive = blockedApps.isNotEmpty()

    SlideInUpAnimated(delayMs = 0) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Welcome Header
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Selah", fontSize = 40.sp, fontWeight = FontWeight.Light, color = SelahPrimary, fontFamily = FontFamily.Serif)
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.str_pause_reflect_purpose), fontSize = 10.sp, color = SelahMuted, letterSpacing = 2.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Active Shield Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SelahSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(0.5.dp, if (isShieldActive) SelahAccent else SelahDivider),
                modifier = Modifier.fillMaxWidth().bouncyClick(onClick = onNavigateToGuard)
            ) {
                Row(
                    modifier = Modifier.padding(22.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconShield(tint = if (isShieldActive) SelahAccent else SelahPrimary, size = 18.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(if (isShieldActive) stringResource(R.string.str_shield_active) else stringResource(R.string.str_shield_inactive),
                                fontSize = 17.sp, color = if (isShieldActive) SelahAccent else SelahPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (isShieldActive) stringResource(R.string.str_shield_stats, blockedApps.size, openLimit, timeLimit)
                            else stringResource(R.string.str_tap_to_select_apps),
                            fontSize = 13.sp, color = SelahMuted, fontWeight = FontWeight.Light
                        )
                    }
                    Text("→", fontSize = 22.sp, color = SelahAccent)
                }
            }
        }

        // Statistics Grid
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                HomeStatBox(stringResource(R.string.str_streak), stringResource(R.string.str_x_days, streakDays), stringResource(R.string.str_consecutive_days), Modifier.weight(1f)) { IconFlame(tint = SelahAccent, size = 20.dp) }
                HomeStatBox(stringResource(R.string.str_pauses), "$totalReflections", stringResource(R.string.str_reflections_taken), Modifier.weight(1f)) { IconPause(tint = SelahAccent, size = 20.dp) }
            }
        }
        // Verse of the Day Anchor Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SelahSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(0.5.dp, SelahDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
                val bibleVersion = SelahPrefs.getBibleVersion(context)
                val dailyVerse = DailyVersesDB.getDailyVerse(bibleVersion, dayOfYear)
                var hasSword by remember { mutableStateOf(SelahPrefs.hasSword(context)) }

                Column(modifier = Modifier.padding(24.dp)) {
                    Text(stringResource(R.string.str_daily_anchor), fontSize = 10.sp, color = SelahAccent, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        dailyVerse.first,
                        fontSize = 22.sp, color = SelahPrimary, fontFamily = FontFamily.Serif, lineHeight = 32.sp, fontWeight = FontWeight.Light
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("— ${dailyVerse.second} ($bibleVersion)", fontSize = 11.sp, color = SelahMuted, letterSpacing = 1.5.sp)
                    
                    if (!hasSword) {
                        Spacer(Modifier.height(16.dp))
                        BouncyButton(
                            onClick = {
                                SelahPrefs.equipSword(context)
                                hasSword = true
                            },
                            containerColor = SelahBackground,
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(0.5.dp, SelahDivider),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.str_meditate_on_this_equip_sword_), color = SelahPrimary, fontSize = 14.sp)
                        }
                    } else {
                        Spacer(Modifier.height(16.dp))
                        Text(stringResource(R.string.str_sword_of_the_spirit_equipped), fontSize = 12.sp, color = SelahAccent, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Quick Action Button
        item {
            BouncyButton(
                onClick = onNavigateToGuard,
                containerColor = SelahAccent,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().bouncy().height(54.dp)
            ) {
                Text(stringResource(R.string.str_manage_app_guards_), color = SelahBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
    }
}

@Composable
private fun HomeStatBox(label: String, value: String, subtitle: String, modifier: Modifier = Modifier, icon: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SelahSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(0.5.dp, SelahDivider),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, color = SelahPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 11.sp, color = SelahMuted, fontWeight = FontWeight.Light, textAlign = TextAlign.Center)
        }
    }
}

// ── Tab 2: App Guard ──────────────────────────────────────────────────────────

@Composable
private fun AppGuardTab(
    initialBlocked: Set<String>,
    onSave: (Set<String>) -> Unit
) {
    val context = LocalContext.current
    val language = SelahPrefs.getLanguage(context)
    var searchText by remember { mutableStateOf("") }
    var selected by remember(initialBlocked) { mutableStateOf(initialBlocked) }
    var saveSuccess by remember { mutableStateOf(false) }

    val allInstalledApps = remember(context) { AppLibrary.getInstalledApps(context) }

    val visibleApps = remember(searchText, allInstalledApps) {
        if (searchText.isBlank()) allInstalledApps
        else allInstalledApps.filter { it.name.contains(searchText, true) || it.packageName.contains(searchText, true) }
    }

    val isActive = selected.isNotEmpty()

    SlideInUpAnimated(delayMs = 0) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = isActive,
                enter = expandVertically(tween(400)) + fadeIn(tween(400)),
                exit  = shrinkVertically(tween(300)) + fadeOut(tween(300))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SelahAccent.copy(alpha = 0.1f))
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconShield(tint = SelahAccent, size = 15.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.str_shield_active_count, selected.size, if (selected.size == 1) "" else "s"),
                            fontSize = 12.sp, color = SelahAccent, fontWeight = FontWeight.Medium
                        )
                    }
                    HorizontalDivider(color = SelahAccent.copy(alpha = 0.2f), thickness = 0.5.dp)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.str_guard_apps), fontSize = 28.sp, fontWeight = FontWeight.Light,
                    color = SelahPrimary, fontFamily = FontFamily.Serif)
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.str_select_app_installed), fontSize = 10.sp,
                    color = SelahMuted, letterSpacing = 1.5.sp, fontWeight = FontWeight.Medium)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SelahSurface)
                    .border(0.5.dp, SelahDivider, RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconSearch(tint = SelahMuted, size = 15.dp)
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    singleLine = true,
                    textStyle = TextStyle(color = SelahPrimary, fontSize = 15.sp, fontWeight = FontWeight.Light),
                    decorationBox = { inner ->
                        Box {
                            if (searchText.isEmpty()) {
                                Text(stringResource(R.string.str_search_all_apps), color = SelahMuted, fontSize = 14.sp, fontWeight = FontWeight.Light)
                            }
                            inner()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = if (selected.isEmpty()) stringResource(R.string.str_tap_apps_to_select, visibleApps.size)
                       else stringResource(R.string.str_apps_selected, selected.size),
                fontSize = 12.sp,
                color = if (selected.isEmpty()) SelahMuted else SelahAccent,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(visibleApps, key = { it.packageName }) { app ->
                    AppRow(
                        app = app,
                        isSelected = selected.contains(app.packageName),
                        onToggle = {
                            selected = if (selected.contains(app.packageName))
                                selected - app.packageName
                            else
                                selected + app.packageName
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 76.dp),
                        color = SelahDivider.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, SelahBackground, SelahBackground)))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(visible = saveSuccess) {
                    Text(stringResource(R.string.str_shield_updated_check), fontSize = 13.sp, color = SelahAccent, fontWeight = FontWeight.Light)
                }
                BouncyButton(
                    onClick = {
                        onSave(selected)
                        saveSuccess = true
                    },
                    containerColor = SelahAccent,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().bouncy().height(52.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconShield(tint = SelahBackground, size = 16.dp)
                        Text(
                            text = if (isActive) stringResource(R.string.str_update_app_shield) else stringResource(R.string.str_activate_blocking),
                            color = SelahBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
    }
}

// ── Tab 3: Quiet Time & Data Analytics (Rich Insights Feed) ──────────────────

@Composable
private fun QuietTimeTab(totalReflections: Int, streakDays: Int) {
    val context = LocalContext.current
    val language = SelahPrefs.getLanguage(context)
    var selectedFilter by remember { mutableStateOf(TimeFilter.ALL_TIME) }
    var showLogDialog by remember { mutableStateOf(false) }
    val history = remember(totalReflections) { SelahPrefs.getReflectionHistory(context) }

    val filteredHistory = remember(selectedFilter, history) {
        when (selectedFilter.count) {
            null -> history
            else -> history.take(selectedFilter.count!!)
        }
    }

    val totalCount = filteredHistory.size
    val categoryCounts = remember(filteredHistory) {
        filteredHistory.groupingBy { it.category }.eachCount()
    }
    val noneYetText = stringResource(R.string.str_none_yet)
    val topCategory = remember(categoryCounts) {
        categoryCounts.maxByOrNull { it.value }?.key ?: noneYetText
    }

    if (showLogDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showLogDialog = false }) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = SelahBackground),
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.str_reflection_logs), fontSize = 22.sp, color = SelahPrimary, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium)
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(SelahSurface).bouncyClick { showLogDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", color = SelahMuted, fontSize = 16.sp)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredHistory, key = { it.id }) { record ->
                            ReflectionRecordCard(record)
                        }
                        if (filteredHistory.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text(stringResource(R.string.str_no_reflections_logged), color = SelahMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    SlideInUpAnimated(delayMs = 0) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Tab Header
        item {
            SlideInUpAnimated(0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.str_quiet_time_title), fontSize = 28.sp, fontWeight = FontWeight.Light, color = SelahPrimary, fontFamily = FontFamily.Serif)
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.str_quiet_time_sub), fontSize = 10.sp, color = SelahMuted, letterSpacing = 1.5.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Daily Scripture Anchor Card
        item {
            SlideInUpAnimated(100) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SelahSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, SelahDivider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
                    val bibleVersion = SelahPrefs.getBibleVersion(context)
                    val dailyVerse = DailyVersesDB.getDailyVerse(bibleVersion, dayOfYear)

                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(stringResource(R.string.str_daily_anchor), fontSize = 10.sp, color = SelahAccent, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            dailyVerse.first,
                            fontSize = 20.sp, color = SelahPrimary, fontFamily = FontFamily.Serif, lineHeight = 30.sp, fontWeight = FontWeight.Light
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("— ${dailyVerse.second} ($bibleVersion)", fontSize = 11.sp, color = SelahMuted, letterSpacing = 1.5.sp)
                    }
                }
            }
        }

        // High Level Metrics
        item {
            SlideInUpAnimated(200) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatCard(stringResource(R.string.str_streak), stringResource(R.string.str_x_days, streakDays), stringResource(R.string.str_consecutive_days), Modifier.weight(1f)) { IconFlame(tint = SelahAccent, size = 20.dp) }
                    StatCard(stringResource(R.string.str_pauses), "$totalReflections", stringResource(R.string.str_reflections_taken), Modifier.weight(1f)) { IconPause(tint = SelahAccent, size = 20.dp) }
                }
            }
        }

        // Filter Controls
        item {
            SlideInUpAnimated(300) {
                Column {
                    Text(stringResource(R.string.str_time_horizon), fontSize = 10.sp, color = SelahAccent, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TimeFilter.values().forEach { filter ->
                            val isSelected = selectedFilter == filter
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (isSelected) SelahAccent else SelahSurface)
                                    .border(0.5.dp, if (isSelected) SelahAccent else SelahDivider, RoundedCornerShape(24.dp))
                                    .bouncyClick { selectedFilter = filter },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(filter.titleResId),
                                    fontSize = 13.sp,
                                    color = if (isSelected) SelahBackground else SelahPrimary,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Light
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Clustering Breakdown
        item {
            SlideInUpAnimated(400) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SelahSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, SelahDivider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.str_top_patterns), fontSize = 10.sp, color = SelahAccent, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                            Text(stringResource(R.string.str_primary_pattern, getLocalizedText(topCategory, language, context)), fontSize = 12.sp, color = SelahPrimary, fontWeight = FontWeight.Medium)
                        }

                        if (categoryCounts.isEmpty()) {
                            Text(stringResource(R.string.str_no_reflections_yet), fontSize = 13.sp, color = SelahMuted, fontWeight = FontWeight.Light)
                        } else {
                            categoryCounts.entries.sortedByDescending { it.value }.forEach { (catName, count) ->
                                val pct = if (totalCount > 0) count.toFloat() / totalCount else 0f
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(getLocalizedText(catName, language, context), fontSize = 13.sp, color = SelahPrimary, fontWeight = FontWeight.Light)
                                        Text("$count (${(pct * 100).toInt()}%)", fontSize = 12.sp, color = SelahMuted)
                                    }
                                    LinearProgressIndicator(
                                        progress = { pct },
                                        modifier = Modifier.fillMaxWidth().bouncy().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = SelahAccent,
                                        trackColor = SelahDivider
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Reflection Log Button
        item {
            SlideInUpAnimated(500) {
                BouncyButton(
                    onClick = { showLogDialog = true },
                    containerColor = SelahSurface,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, SelahDivider),
                    modifier = Modifier.fillMaxWidth().bouncy().height(60.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.str_view_log) + " (${filteredHistory.size})", color = SelahPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        IconPause(tint = SelahAccent, size = 16.dp) // Just an icon
                    }
                }
            }
        }

        item {
            SlideInUpAnimated(600) {
                var hasBelt by remember { mutableStateOf(SelahPrefs.hasBelt(context)) }
                BouncyButton(
                    onClick = { 
                        SelahPrefs.equipBelt(context)
                        hasBelt = true
                    },
                    modifier = Modifier.fillMaxWidth().bouncy().height(60.dp),
                    shape = RoundedCornerShape(24.dp),
                    containerColor = if (hasBelt) SelahSurface else SelahAccent,
                    border = BorderStroke(1.dp, if (hasBelt) SelahDivider else SelahAccent),
                    enabled = !hasBelt
                ) {
                    Text(
                        if (hasBelt) stringResource(R.string.str_belt_of_truth_equipped) else stringResource(R.string.str_acknowledge_habits_equip_belt_of_truth_),
                        color = if (hasBelt) SelahAccent else SelahBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

}
@Composable
private fun ReflectionRecordCard(record: ReflectionRecord) {
    val language = SelahPrefs.getLanguage(LocalContext.current)
    val dateStr = remember(record.timestamp) {
        val date = Date(record.timestamp)
        val now = Calendar.getInstance()
        val recordCal = Calendar.getInstance().apply { time = date }

        if (now.get(Calendar.DAY_OF_YEAR) == recordCal.get(Calendar.DAY_OF_YEAR) &&
            now.get(Calendar.YEAR) == recordCal.get(Calendar.YEAR)) {
            "Today at " + SimpleDateFormat("h:mm a", Locale.US).format(date)
        } else {
            SimpleDateFormat("MMM d, h:mm a", Locale.US).format(date)
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SelahSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(0.5.dp, SelahDivider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(SelahAccent.copy(alpha = 0.12f))
                        .border(0.5.dp, SelahAccent.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(getLocalizedText(record.category, language, LocalContext.current), fontSize = 11.sp, color = SelahAccent, fontWeight = FontWeight.SemiBold)
                }

                Text(dateStr, fontSize = 11.sp, color = SelahMuted, fontWeight = FontWeight.Light)
            }

            Text(getLocalizedText(record.choiceLabel, language, LocalContext.current), fontSize = 15.sp, color = SelahPrimary, fontWeight = FontWeight.Medium)

            if (record.journalText.isNotBlank()) {
                Text(
                    text = "“${record.journalText}”",
                    fontSize = 13.sp,
                    color = SelahPrimary.copy(alpha = 0.85f),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, sub: String, modifier: Modifier = Modifier, icon: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SelahSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(0.5.dp, SelahDivider),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(Modifier.width(8.dp))
                Text(title, fontSize = 12.sp, color = SelahMuted, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 26.sp, color = SelahPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(sub, fontSize = 11.sp, color = SelahMuted, fontWeight = FontWeight.Light)
        }
    }
}

// ── Tab 4: Settings & Limits ──────────────────────────────────────────────────


@Composable
fun ArmorTabUI(context: Context) {
    var trigger by remember { mutableIntStateOf(0) }
    
    val armorCount = remember(trigger) { SelahPrefs.getArmorCount(context) }
    val hasShield = remember(trigger) { SelahPrefs.hasShield(context) }
    val hasSword = remember(trigger) { SelahPrefs.hasSword(context) }
    val hasBelt = remember(trigger) { SelahPrefs.hasBelt(context) }
    val hasBreastplate = remember(trigger) { SelahPrefs.hasBreastplate(context) }
    val hasHelmet = remember(trigger) { SelahPrefs.hasHelmet(context) }
    val hasShoes = remember(trigger) { SelahPrefs.hasShoes(context) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(SelahBackground, Color(0xFF000000)))),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(stringResource(R.string.str_daily_armor), fontSize = 10.sp, color = SelahAccent, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.str_equipped_count, armorCount), fontSize = 32.sp, color = SelahPrimary, fontFamily = FontFamily.Serif)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.str_armor_subtitle), fontSize = 14.sp, color = SelahMuted, lineHeight = 20.sp)
            }
        }
        
        item {
            ArmorPieceCard(
                name = stringResource(R.string.str_shield_of_faith),
                description = stringResource(R.string.str_shield_desc),
                isEquipped = hasShield,
                icon = Icons.Default.Security
            )
        }
        item {
            ArmorPieceCard(
                name = stringResource(R.string.str_breastplate_of_righteousness),
                description = stringResource(R.string.str_breastplate_desc),
                isEquipped = hasBreastplate,
                icon = Icons.Default.Favorite
            )
        }
        item {
            ArmorPieceCard(
                name = stringResource(R.string.str_shoes_of_peace),
                description = stringResource(R.string.str_shoes_desc),
                isEquipped = hasShoes,
                icon = Icons.Default.DirectionsWalk
            )
        }
        item {
            ArmorPieceCard(
                name = stringResource(R.string.str_helmet_of_salvation),
                description = stringResource(R.string.str_helmet_desc),
                isEquipped = hasHelmet,
                icon = Icons.Default.Face
            )
        }
        item {
            ArmorPieceCard(
                name = stringResource(R.string.str_sword_of_spirit),
                description = stringResource(R.string.str_sword_desc),
                isEquipped = hasSword,
                icon = Icons.Default.Build
            )
        }
        item {
            ArmorPieceCard(
                name = stringResource(R.string.str_belt_of_truth),
                description = stringResource(R.string.str_belt_desc),
                isEquipped = hasBelt,
                icon = Icons.Default.Done
            )
        }
    }
}

@Composable
fun ArmorPieceCard(name: String, description: String, isEquipped: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SelahSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(0.5.dp, SelahDivider),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isEquipped) SelahAccent else SelahMuted,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(name, fontSize = 16.sp, color = if (isEquipped) SelahPrimary else SelahMuted, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(description, fontSize = 12.sp, color = SelahMuted, lineHeight = 16.sp)
                if (isEquipped) {
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.str_equipped_upper), fontSize = 10.sp, color = SelahAccent, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SettingsTab() {
    val context = LocalContext.current
    var showUnblockAllDialog by remember { mutableStateOf(false) }
    var openLimit    by remember { mutableStateOf(SelahPrefs.getOpenLimit(context)) }
    var timeLimit    by remember { mutableStateOf(SelahPrefs.getTimeLimit(context)) }
    var instantBlock by remember { mutableStateOf(SelahPrefs.isInstantBlockEnabled(context)) }
    var language by remember { mutableStateOf(SelahPrefs.getLanguage(context)) }
    var bibleVersion by remember { mutableStateOf(SelahPrefs.getBibleVersion(context)) }

    SlideInUpAnimated(delayMs = 0) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            SlideInUpAnimated(0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.str_settings_tab), fontSize = 28.sp, fontWeight = FontWeight.Light, color = SelahPrimary, fontFamily = FontFamily.Serif)
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.str_limits_server_config), fontSize = 10.sp, color = SelahMuted, letterSpacing = 1.5.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Instant Shield Switch
        item {
            SlideInUpAnimated(100) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SelahSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, SelahDivider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(22.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.str_instant_shield_mode), fontSize = 16.sp, color = SelahPrimary, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text(stringResource(R.string.str_immediately_block_all_guarded_apps_on_every_open), fontSize = 12.sp, color = SelahMuted)
                        }
                        Switch(
                            checked = instantBlock,
                            onCheckedChange = {
                                instantBlock = it
                                SelahPrefs.setInstantBlockEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = SelahBackground, checkedTrackColor = SelahAccent)
                        )
                    }
                }
            }
        }

        // Language Selector
        item {
            SlideInUpAnimated(150) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SelahSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, SelahDivider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    val languages = listOf("English", "Spanish", "Korean", "Chinese", "Portuguese", "French")
                    Column(modifier = Modifier.padding(22.dp).fillMaxWidth()) {
                        Text(stringResource(R.string.str_ai_reflection_language), fontSize = 16.sp, color = SelahPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(stringResource(R.string.str_language_for_ai_generated_reflections), fontSize = 12.sp, color = SelahMuted)
                        Spacer(Modifier.height(12.dp))
                        Box {
                            BouncyButton(
                                onClick = { expanded = true },
                                containerColor = SelahBackground,
                                border = BorderStroke(0.5.dp, SelahDivider),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(language, color = SelahPrimary)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(SelahSurface)
                            ) {
                                languages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text(lang, color = SelahPrimary) },
                                        onClick = {
                                            language = lang
                                            SelahPrefs.setLanguage(context, lang)
                                            val localeTag = when (lang) {
                                                "Spanish" -> "es"
                                                "Korean" -> "ko"
                                                "Chinese" -> "zh"
                                                "Portuguese" -> "pt"
                                                "French" -> "fr"
                                                else -> "en"
                                            }
                                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTag))
                                            expanded = false
                                            (context as? androidx.appcompat.app.AppCompatActivity)?.recreate()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bible Version Selector
        item {
            SlideInUpAnimated(200) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SelahSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, SelahDivider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    val versions = when (language) {
                        "Korean" -> listOf("NKRV", "KRV")
                        "Spanish" -> listOf("NVI", "RVR1960")
                        "Chinese" -> listOf("CUV")
                        "French" -> listOf("LSG", "BFC")
                        "Portuguese" -> listOf("NVI-PT", "ARC")
                        else -> listOf("NIV", "ESV", "KJV", "NLT", "MSG", "CSB", "NKJV")
                    }
                    Column(modifier = Modifier.padding(22.dp).fillMaxWidth()) {
                        Text(stringResource(R.string.str_bible_translation), fontSize = 16.sp, color = SelahPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(stringResource(R.string.str_preferred_translation_for_daily_verses), fontSize = 12.sp, color = SelahMuted)
                        Spacer(Modifier.height(12.dp))
                        Box {
                            BouncyButton(
                                onClick = { expanded = true },
                                containerColor = SelahBackground,
                                border = BorderStroke(0.5.dp, SelahDivider),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(bibleVersion, color = SelahPrimary)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(SelahSurface)
                            ) {
                                versions.forEach { version ->
                                    DropdownMenuItem(
                                        text = { Text(version, color = SelahPrimary) },
                                        onClick = {
                                            bibleVersion = version
                                            SelahPrefs.setBibleVersion(context, version)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Open Limit Threshold
        item {
            SlideInUpAnimated(200) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SelahSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, SelahDivider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(stringResource(R.string.str_repeating_limit).uppercase(), fontSize = 10.sp, color = SelahAccent, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.str_repeating_desc, openLimit, openLimit*2, openLimit*3), fontSize = 15.sp, color = SelahPrimary)
                        Spacer(Modifier.height(10.dp))
                        Slider(
                            value = openLimit.toFloat(),
                            onValueChange = {
                                openLimit = it.toInt()
                                SelahPrefs.setOpenLimit(context, openLimit)
                            },
                            valueRange = 1f..30f,
                            steps = 29,
                            colors = SliderDefaults.colors(thumbColor = SelahAccent, activeTrackColor = SelahAccent)
                        )
                    }
                }
            }
        }

        // Time Limit Threshold
        item {
            SlideInUpAnimated(300) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SelahSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, SelahDivider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(stringResource(R.string.str_continuous_limit).uppercase(), fontSize = 10.sp, color = SelahAccent, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.str_continuous_desc, timeLimit), fontSize = 15.sp, color = SelahPrimary)
                        Spacer(Modifier.height(10.dp))
                        Slider(
                            value = timeLimit.toFloat(),
                            onValueChange = {
                                timeLimit = it.toInt()
                                SelahPrefs.setTimeLimit(context, timeLimit)
                            },
                            valueRange = 1f..45f,
                            steps = 43,
                            colors = SliderDefaults.colors(thumbColor = SelahAccent, activeTrackColor = SelahAccent)
                        )
                    }
                }
            }
        }
    }
}

}
@Composable
private fun SetupScreen(onEnable: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(
        initialValue = 0.94f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "scale"
    )
    val glow by pulse.animateFloat(
        initialValue = 0.08f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SelahBackground)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 36.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(90.dp).clip(CircleShape).background(SelahAccent.copy(alpha = glow)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(60.dp * scale).clip(CircleShape).background(SelahAccent.copy(alpha = glow * 2)),
                    contentAlignment = Alignment.Center
                ) {
                    IconShield(tint = SelahAccent, size = 30.dp)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text("Selah", fontSize = 40.sp, fontWeight = FontWeight.Light, color = SelahPrimary, fontFamily = FontFamily.Serif)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.str_digital_wellbeing), fontSize = 10.sp, color = SelahMuted, letterSpacing = 2.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(52.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SelahSurface)
                    .border(0.5.dp, SelahDivider, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.str_one_step_to_begin), fontSize = 10.sp, color = SelahAccent, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        stringResource(R.string.str_enable_accessibility_desc),
                        fontSize = 15.sp, color = SelahPrimary.copy(alpha = 0.85f), fontWeight = FontWeight.Light, lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            BouncyButton(
                onClick = onEnable,
                containerColor = SelahAccent,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().bouncy().height(56.dp)
            ) {
                Text(stringResource(R.string.str_enable_accessibility), color = SelahBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(stringResource(R.string.str_find_selah), fontSize = 13.sp, color = SelahMuted, textAlign = TextAlign.Center)
        }
    }
}

// ── App Row Component ─────────────────────────────────────────────────────────

@Composable
private fun AppRow(app: BlockableApp, isSelected: Boolean, onToggle: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) SelahAccent.copy(alpha = 0.07f) else Color.Transparent,
        animationSpec = tween(150), label = "bg"
    )
    val appColor = appAccentColor(app.name)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .bouncyClick(onClick = onToggle)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(24.dp)).background(appColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(app.name.first().toString(), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = appColor)
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(app.name, fontSize = 16.sp, fontWeight = FontWeight.Light, color = SelahPrimary)
            Text(app.packageName, fontSize = 11.sp, color = SelahMuted, fontWeight = FontWeight.Light)
        }

        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (isSelected) SelahAccent else Color.Transparent)
                .border(1.5.dp, if (isSelected) SelahAccent else SelahDivider, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                IconCheck(tint = SelahBackground, size = 14.dp)
            }
        }
    }
}


@Composable
fun getLocalizedText(text: String, language: String, context: android.content.Context): String {
    if (language == "English") return text
    var translated by androidx.compose.runtime.remember(text, language) { androidx.compose.runtime.mutableStateOf(SelahPrefs.getCachedTranslation(context, text, language) ?: text) }
    
    androidx.compose.runtime.LaunchedEffect(text, language) {
        if (SelahPrefs.getCachedTranslation(context, text, language) == null) {
            val res = SelahAiClient.translateText(text, language)
            SelahPrefs.saveCachedTranslation(context, text, language, res)
            translated = res
        }
    }
    return translated
}
