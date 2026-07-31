package com.nathan.selah

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nathan.selah.theme.SelahTheme
import kotlinx.coroutines.delay

internal enum class Step { DIAGNOSTIC, JOURNAL, LOADING, VERSE }
internal enum class Choice { SUGGESTED, TROUBLED, HABIT, SOMETHING_ELSE, NOTHING }

fun getAppName(context: android.content.Context, packageName: String?): String {
    if (packageName == null) return "App"
    return try {
        val pm = context.packageManager
        val info = pm.getApplicationInfo(packageName, 0)
        pm.getApplicationLabel(info).toString()
    } catch (e: Exception) {
        "App"
    }
}
class ShieldActivity : androidx.appcompat.app.AppCompatActivity() {
    private var continuedToApp = false

    private var blockedAppPkg: String? = null


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

        blockedAppPkg = intent.getStringExtra("BLOCKED_APP")

        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(android.graphics.Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.BLACK)
        )
        setContent {
            SelahTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .systemBarsPadding()
                ) {
                    ReflectionFlow()
                }
            }
        }
    }

    // ── Main Flow ─────────────────────────────────────────────────────────────

    @Composable
    fun ReflectionFlow() {
        val context  = LocalContext.current
        var step     by remember { mutableStateOf(Step.DIAGNOSTIC) }
        var choice   by remember { mutableStateOf<Choice?>(null) }
        var journal  by remember { mutableStateOf("") }
        var verse    by remember { mutableStateOf<Triple<String, String, String>?>(null) }

        val suggestedInfo = remember { SelahPrefs.getSuggestedCategory(context) }

        LaunchedEffect(step) {
            if (step == Step.LOADING) {
                val effectiveChoice = choice ?: Choice.NOTHING
                val effectiveJournal = if (effectiveChoice == Choice.SUGGESTED && journal.isBlank()) suggestedInfo.second else journal
                verse = fetchReflection(effectiveChoice, effectiveJournal)
                step  = Step.VERSE
            }
        }

        AnimatedContent(
            targetState = step,
            transitionSpec = { fadeIn(tween(700)) togetherWith fadeOut(tween(500)) },
            label = "step"
        ) { current ->
            when (current) {
                Step.DIAGNOSTIC -> DiagnosticStep(
                    suggestedLabel = suggestedInfo.first,
                    onPick = { picked ->
                        choice = picked
                        step = if (picked == Choice.TROUBLED || picked == Choice.SOMETHING_ELSE || picked == Choice.SUGGESTED)
                            Step.JOURNAL else Step.LOADING
                    }
                )
                Step.JOURNAL -> JournalStep(
                    choice = choice ?: Choice.TROUBLED,
                    suggestedCategory = suggestedInfo.second,
                    onSubmit = { text -> journal = text; step = Step.LOADING }
                )
                Step.LOADING -> LoadingStep()
                Step.VERSE   -> VerseStep(
                    verseData = verse,
                    appName = getAppName(context, blockedAppPkg),
                    onBible = {
                        val choiceName = choice?.name ?: "MINDFUL"
                        val choiceTitle = when (choice) {
                            Choice.SUGGESTED -> suggestedInfo.first
                            Choice.TROUBLED -> "Troubled or Stressed"
                            Choice.HABIT -> "Mindless Habit"
                            Choice.SOMETHING_ELSE -> "Something Else"
                            Choice.NOTHING -> "Nothing — Just Opened"
                            else -> "Mindful Choice"
                        }
                        SelahPrefs.recordReflection(this@ShieldActivity, choiceName, choiceTitle, journal, blockedAppPkg)
                        
                        blockedAppPkg?.let { pkg ->
                            SelahPrefs.setBypassUntil(this@ShieldActivity, pkg, System.currentTimeMillis() + 30_000L)
                        }
                        
                        BibleDeepLinkHelper.openVerse(this@ShieldActivity, verse?.first ?: "")
                        finish()
                    },
                    onContinue = {
                        val choiceName = choice?.name ?: "MINDFUL"
                        val choiceTitle = when (choice) {
                            Choice.SUGGESTED -> suggestedInfo.first
                            Choice.TROUBLED -> "Troubled or Stressed"
                            Choice.HABIT -> "Mindless Habit"
                            Choice.SOMETHING_ELSE -> "Something Else"
                            Choice.NOTHING -> "Nothing — Just Opened"
                            else -> "Mindful Choice"
                        }
                        SelahPrefs.recordReflection(this@ShieldActivity, choiceName, choiceTitle, journal, blockedAppPkg, wasMindful = false)

                        blockedAppPkg?.let { pkg ->
                            val timeLimitMins = SelahPrefs.getTimeLimit(this@ShieldActivity).coerceAtLeast(1)
                            val bypassDuration = timeLimitMins * 60 * 1000L
                            SelahPrefs.setBypassUntil(this@ShieldActivity, pkg, System.currentTimeMillis() + bypassDuration)
                            continuedToApp = true
                        }
                        finish()
                    },
                    onClose = {
                        val choiceName = choice?.name ?: "MINDFUL"
                        val choiceTitle = when (choice) {
                            Choice.SUGGESTED -> suggestedInfo.first
                            Choice.TROUBLED -> "Troubled or Stressed"
                            Choice.HABIT -> "Mindless Habit"
                            Choice.SOMETHING_ELSE -> "Something Else"
                            Choice.NOTHING -> "Nothing — Just Opened"
                            else -> "Mindful Choice"
                        }
                        SelahPrefs.recordReflection(this@ShieldActivity, choiceName, choiceTitle, journal, blockedAppPkg)
                        
                        blockedAppPkg?.let { pkg ->
                            SelahPrefs.setBypassUntil(this@ShieldActivity, pkg, System.currentTimeMillis() + 30_000L)
                        }
                        
                        val home = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(home)
                        finish()
                    }
                )
            }
        }
    }

    // ── Step 1: Diagnostic ────────────────────────────────────────────────────

    @Composable
    internal fun DiagnosticStep(suggestedLabel: String, onPick: (Choice) -> Unit) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconPause(tint = SelahAccent, size = 36.dp)
            Spacer(Modifier.height(18.dp))

            Text(
                text = "What brought you here?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                color = SelahPrimary,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.str_take_a_breath),
                fontSize = 13.sp,
                color = SelahMuted,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(Modifier.height(36.dp))

            // Dynamic Suggested Option
            OptionRow(stringResource(R.string.str_suggested_label, suggestedLabel), Choice.SUGGESTED, onPick, isSuggested = true)
            Spacer(Modifier.height(12.dp))

            OptionRow(stringResource(R.string.str_im_troubled), Choice.TROUBLED, onPick)
            Spacer(Modifier.height(12.dp))
            OptionRow(stringResource(R.string.str_mindless_habit), Choice.HABIT, onPick)
            Spacer(Modifier.height(12.dp))
            OptionRow(stringResource(R.string.str_something_else_mind), Choice.SOMETHING_ELSE, onPick)
            Spacer(Modifier.height(12.dp))
            OptionRow(stringResource(R.string.str_nothing_just_opened), Choice.NOTHING, onPick)

            } // Close Column
        }
    }

    @Composable
    private fun OptionRow(label: String, choice: Choice, onPick: (Choice) -> Unit, isSuggested: Boolean = false) {
        BouncyButton(
            onClick = { onPick(choice) },
            containerColor = if (isSuggested) SelahAccent.copy(alpha = 0.15f) else SelahSurface,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(0.5.dp, if (isSuggested) SelahAccent else SelahDivider),
            modifier = Modifier.fillMaxWidth().bouncy().height(56.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = if (isSuggested) SelahAccent else SelahPrimary,
                    fontSize = 16.sp,
                    fontWeight = if (isSuggested) FontWeight.Medium else FontWeight.Light
                )
                Text("→", color = SelahAccent, fontSize = 18.sp)
            }
        }
    }

    // ── Step 2: Journal Input ─────────────────────────────────────────────────

    @Composable
    private fun JournalStep(choice: Choice, suggestedCategory: String, onSubmit: (String) -> Unit) {
        var text by remember { mutableStateOf("") }
        val prompt = when (choice) {
            Choice.SUGGESTED -> stringResource(R.string.str_reflecting_on, suggestedCategory)
            Choice.TROUBLED -> stringResource(R.string.str_whats_on_your_heart)
            else -> stringResource(R.string.str_what_hoping_to_find)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = prompt,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                color = SelahPrimary,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SelahSurface)
                    .border(0.5.dp, SelahDivider, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = TextStyle(color = SelahPrimary, fontSize = 16.sp, fontWeight = FontWeight.Light),
                    cursorBrush = SolidColor(SelahAccent),
                    decorationBox = { inner ->
                        if (text.isEmpty()) {
                            Text(stringResource(R.string.str_express_thoughts), color = SelahMuted, fontSize = 15.sp, fontWeight = FontWeight.Light)
                        }
                        inner()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(28.dp))

            BouncyButton(
                onClick = { onSubmit(text) },
                enabled = text.isNotBlank() || choice == Choice.SUGGESTED,
                containerColor = SelahAccent, disabledContainerColor = SelahSurface,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().bouncy().height(54.dp)
            ) {
                Text(
                    stringResource(R.string.str_seek_guidance),
                    color = if (text.isNotBlank() || choice == Choice.SUGGESTED) SelahBackground else SelahMuted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1.2f))
        }
    }

    // ── Step 3: Breathing Loading Anchor ──────────────────────────────────────

    @Composable
    private fun LoadingStep() {
        val transition = rememberInfiniteTransition(label = "breathe")
        val scale by transition.animateFloat(
            initialValue = 0.94f,
            targetValue  = 1.08f,
            animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse),
            label = "scale"
        )
        val alpha by transition.animateFloat(
            initialValue = 0.4f,
            targetValue  = 1.0f,
            animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse),
            label = "alpha"
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(SelahAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp * scale)
                        .clip(CircleShape)
                        .background(SelahAccent.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconPause(tint = SelahAccent, size = 26.dp)
                }
            }

            Spacer(Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.str_pause_and_breathe),
                fontSize = 11.sp,
                color = SelahAccent.copy(alpha = alpha),
                letterSpacing = 3.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.str_connecting_ai),
                fontSize = 14.sp,
                color = SelahMuted,
                fontWeight = FontWeight.Light
            )
        }
    }

    // ── Step 4: Verse & Choice Screen ─────────────────────────────────────────

    @Composable
    private fun VerseStep(
        verseData: Triple<String, String, String>?,
        appName: String,
        onBible: () -> Unit,
        onContinue: () -> Unit,
        onClose: () -> Unit
    ) {
        var secondsLeft  by remember { mutableStateOf(5) }
        var showChoices  by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }
            showChoices = true
        }

        val contentAlpha by animateFloatAsState(
            targetValue = if (verseData != null) 1f else 0f,
            animationSpec = tween(600), label = "alpha"
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.8f))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(SelahAccent.copy(alpha = 0.1f))
                    .border(0.5.dp, SelahAccent.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SelahAccent))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.str_gloo_ai_grounded), fontSize = 9.sp, color = SelahAccent, letterSpacing = 1.8.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(20.dp))

            val ref  = verseData?.first  ?: "PSALM 46:10"
            val text = (verseData?.second ?: "Be still, and know that I am God.").trim('"', '“', '”')
            val refl = verseData?.third  ?: ""

            Text(
                text = ref.uppercase(),
                fontSize = 11.sp,
                color = SelahMuted,
                letterSpacing = 2.5.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(contentAlpha)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = text,
                fontSize = 21.sp,
                color = SelahPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Serif,
                lineHeight = 33.sp,
                modifier = Modifier.alpha(contentAlpha)
            )

            if (refl.isNotBlank()) {
                Spacer(Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = SelahSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, SelahAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().alpha(contentAlpha)
                ) {
                    Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = refl,
                            fontSize = 14.sp,
                            color = SelahAccent,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                visible = showChoices,
                enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { it / 3 }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 36.dp)
                ) {
                    BouncyButton(
                        onClick = onBible,
                        containerColor = SelahAccent,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().bouncy().height(56.dp)
                    ) {
                        Text(stringResource(R.string.str_go_to_bible), color = SelahBackground, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    }

                    BouncyButton(
                        onClick = onContinue,
                        contentColor = SelahPrimary, containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        border = BorderStroke(0.5.dp, SelahDivider),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().bouncy().height(56.dp)
                    ) {
                        Text(stringResource(R.string.str_continue_to_app_name, appName), fontSize = 17.sp, fontWeight = FontWeight.Light)
                    }

                    BouncyButton(
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth().bouncy().height(48.dp)
                    ) {
                        Text(stringResource(R.string.str_close_app), color = SelahMuted, fontSize = 17.sp)
                    }
                }
            }

            AnimatedVisibility(visible = !showChoices) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 40.dp)) {
                    CountdownDots()
                }
            }
        }
    }

    @Composable
    private fun CountdownDots() {
        val anim = rememberInfiniteTransition(label = "dots")
        val a by anim.animateFloat(
            0.3f, 1f,
            infiniteRepeatable(tween(600), RepeatMode.Reverse),
            label = "a"
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { i ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(SelahMuted.copy(alpha = if (i == 0) a else if (i == 1) (a * 0.7f) else (a * 0.4f)))
                )
            }
        }
    }

    // ── Network Call ─────────────────────────────────────────────────────────

    private suspend fun fetchReflection(choice: Choice, journal: String): Triple<String, String, String> {
        val type = when (choice) {
            Choice.SUGGESTED     -> "suggested_pattern"
            Choice.TROUBLED       -> "troubled_or_stressed"
            Choice.HABIT          -> "mindless_habit"
            Choice.SOMETHING_ELSE -> "something_else"
            Choice.NOTHING        -> "nothing"
        }
        val backendUrl = SelahPrefs.getBackendUrl(this@ShieldActivity)
        return SelahAiClient.getReflection(this@ShieldActivity, type, journal, backendUrl)
    }
    override fun onDestroy() {
        super.onDestroy()
        if (!continuedToApp && isFinishing) {
            SelahPrefs.equipHelmet(this)
        }
    }
}