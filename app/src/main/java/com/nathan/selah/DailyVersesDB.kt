package com.nathan.selah

object DailyVersesDB {
    // 7 verses for a week, mapped by Version
    val versesByVersion = mapOf(
        "NIV" to listOf(
            Pair("“He says, ‘Be still, and know that I am God; I will be exalted among the nations, I will be exalted in the earth.’”", "PSALM 46:10"),
            Pair("“The Lord is my shepherd, I lack nothing.”", "PSALM 23:1"),
            Pair("“Cast all your anxiety on him because he cares for you.”", "1 PETER 5:7"),
            Pair("“I can do all this through him who gives me strength.”", "PHILIPPIANS 4:13"),
            Pair("“Trust in the Lord with all your heart and lean not on your own understanding.”", "PROVERBS 3:5"),
            Pair("“Come to me, all you who are weary and burdened, and I will give you rest.”", "MATTHEW 11:28"),
            Pair("“For the Spirit God gave us does not make us timid, but gives us power, love and self-discipline.”", "2 TIMOTHY 1:7")
        ),
        "ESV" to listOf(
            Pair("“‘Be still, and know that I am God. I will be exalted among the nations, I will be exalted in the earth!’”", "PSALM 46:10"),
            Pair("“The Lord is my shepherd; I shall not want.”", "PSALM 23:1"),
            Pair("“Casting all your anxieties on him, because he cares for you.”", "1 PETER 5:7"),
            Pair("“I can do all things through him who strengthens me.”", "PHILIPPIANS 4:13"),
            Pair("“Trust in the Lord with all your heart, and do not lean on your own understanding.”", "PROVERBS 3:5"),
            Pair("“Come to me, all who labor and are heavy laden, and I will give you rest.”", "MATTHEW 11:28"),
            Pair("“For God gave us a spirit not of fear but of power and love and self-control.”", "2 TIMOTHY 1:7")
        ),
        "KJV" to listOf(
            Pair("“Be still, and know that I am God: I will be exalted among the heathen, I will be exalted in the earth.”", "PSALM 46:10"),
            Pair("“The Lord is my shepherd; I shall not want.”", "PSALM 23:1"),
            Pair("“Casting all your care upon him; for he careth for you.”", "1 PETER 5:7"),
            Pair("“I can do all things through Christ which strengtheneth me.”", "PHILIPPIANS 4:13"),
            Pair("“Trust in the Lord with all thine heart; and lean not unto thine own understanding.”", "PROVERBS 3:5"),
            Pair("“Come unto me, all ye that labour and are heavy laden, and I will give you rest.”", "MATTHEW 11:28"),
            Pair("“For God hath not given us the spirit of fear; but of power, and of love, and of a sound mind.”", "2 TIMOTHY 1:7")
        ),
        "NLT" to listOf(
            Pair("“‘Be still, and know that I am God! I will be honored by every nation. I will be honored throughout the world.’”", "PSALM 46:10"),
            Pair("“The Lord is my shepherd; I have all that I need.”", "PSALM 23:1"),
            Pair("“Give all your worries and cares to God, for he cares about you.”", "1 PETER 5:7"),
            Pair("“For I can do everything through Christ, who gives me strength.”", "PHILIPPIANS 4:13"),
            Pair("“Trust in the Lord with all your heart; do not depend on your own understanding.”", "PROVERBS 3:5"),
            Pair("“Then Jesus said, ‘Come to me, all of you who are weary and carry heavy burdens, and I will give you rest.’”", "MATTHEW 11:28"),
            Pair("“For God has not given us a spirit of fear and timidity, but of power, love, and self-discipline.”", "2 TIMOTHY 1:7")
        ),
        "MSG" to listOf(
            Pair("“Step out of the traffic! Take a long, loving look at me, your High God, above politics, above everything.”", "PSALM 46:10"),
            Pair("“God, my shepherd! I don't need a thing.”", "PSALM 23:1"),
            Pair("“Live carefree before God; he is most careful with you.”", "1 PETER 5:7"),
            Pair("“Whatever I have, wherever I am, I can make it through anything in the One who makes me who I am.”", "PHILIPPIANS 4:13"),
            Pair("“Trust God from the bottom of your heart; don't try to figure out everything on your own.”", "PROVERBS 3:5"),
            Pair("“Are you tired? Worn out? Burned out on religion? Come to me. Get away with me and you'll recover your life. I'll show you how to take a real rest.”", "MATTHEW 11:28"),
            Pair("“God doesn't want us to be shy with his gifts, but bold and loving and sensible.”", "2 TIMOTHY 1:7")
        ),
        "CSB" to listOf(
            Pair("“‘Stop your fighting, and know that I am God, exalted among the nations, exalted on the earth.’”", "PSALM 46:10"),
            Pair("“The Lord is my shepherd; I have what I need.”", "PSALM 23:1"),
            Pair("“Casting all your cares on him, because he cares about you.”", "1 PETER 5:7"),
            Pair("“I am able to do all things through him who strengthens me.”", "PHILIPPIANS 4:13"),
            Pair("“Trust in the Lord with all your heart, and do not rely on your own understanding.”", "PROVERBS 3:5"),
            Pair("“‘Come to me, all of you who are weary and burdened, and I will give you rest.’”", "MATTHEW 11:28"),
            Pair("“For God has not given us a spirit of fear, but one of power, love, and sound judgment.”", "2 TIMOTHY 1:7")
        ),
                "NVI" to listOf(
            Pair("“Quédense quietos, reconozcan que yo soy Dios. ¡Yo seré exaltado entre las naciones! ¡Yo seré enaltecido en la tierra!”", "SALMOS 46:10"),
            Pair("“El Señor es mi pastor, nada me falta.”", "SALMOS 23:1"),
            Pair("“Depositen en él toda ansiedad, porque él cuida de ustedes.”", "1 PEDRO 5:7"),
            Pair("“Todo lo puedo en Cristo que me fortalece.”", "FILIPENSES 4:13"),
            Pair("“Confía en el Señor de todo corazón, y no en tu propia inteligencia.”", "PROVERBIOS 3:5"),
            Pair("“Vengan a mí todos ustedes que están cansados y agobiados, y yo les daré descanso.”", "MATEO 11:28"),
            Pair("“Pues Dios no nos ha dado un espíritu de timidez, sino de poder, de amor y de dominio propio.”", "2 TIMOTEO 1:7")
        ),
        "RVR1960" to listOf(
            Pair("“Estad quietos, y conoced que yo soy Dios; Seré exaltado entre las naciones; enaltecido seré en la tierra.”", "SALMOS 46:10"),
            Pair("“Jehová es mi pastor; nada me faltará.”", "SALMOS 23:1"),
            Pair("“echando toda vuestra ansiedad sobre él, porque él tiene cuidado de vosotros.”", "1 PEDRO 5:7"),
            Pair("“Todo lo puedo en Cristo que me fortalece.”", "FILIPENSES 4:13"),
            Pair("“Fíate de Jehová de todo tu corazón, Y no te apoyes en tu propia prudencia.”", "PROVERBIOS 3:5"),
            Pair("“Venid a mí todos los que estáis trabajados y cargados, y yo os haré descansar.”", "MATEO 11:28"),
            Pair("“Porque no nos ha dado Dios espíritu de cobardía, sino de poder, de amor y de dominio propio.”", "2 TIMOTEO 1:7")
        ),
        "KRV" to listOf(
            Pair("“이르시기를 너희는 가만히 있어 내가 하나님 됨을 알지어다 내가 열방과 세계 중에서 높임을 받으리라 하시도다”", "시편 46:10"),
            Pair("“여호와는 나의 목자시니 내가 부족함이 없으리로다”", "시편 23:1"),
            Pair("“너희 염려를 다 주께 맡겨 버리라 이는 저가 너희를 권고하심이니라”", "베드로전서 5:7"),
            Pair("“내게 능력 주시는 자 안에서 내가 모든 것을 할 수 있느니라”", "빌립보서 4:13"),
            Pair("“너는 마음을 다하여 여호와를 의뢰하고 네 명철을 의지하지 말라”", "잠언 3:5"),
            Pair("“수고하고 무거운 짐진 자들아 다 내게로 오라 내가 너희를 쉬게 하리라”", "마태복음 11:28"),
            Pair("“하나님이 우리에게 주신 것은 두려워하는 마음이 아니요 오직 능력과 사랑과 근신하는 마음이니”", "디모데후서 1:7")
        ),
        "NKRV" to listOf(
            Pair("“이르시기를 너희는 가만히 있어 내가 하나님 됨을 알지어다 내가 열방과 세계 중에서 높임을 받으리라 하시도다”", "시편 46:10"),
            Pair("“여호와는 나의 목자시니 내가 부족함이 없으리로다”", "시편 23:1"),
            Pair("“너희 염려를 다 주께 맡겨 버리라 이는 저가 너희를 권고하심이니라”", "베드로전서 5:7"),
            Pair("“내게 능력 주시는 자 안에서 내가 모든 것을 할 수 있느니라”", "빌립보서 4:13"),
            Pair("“너는 마음을 다하여 여호와를 의뢰하고 네 명철을 의지하지 말라”", "잠언 3:5"),
            Pair("“수고하고 무거운 짐진 자들아 다 내게로 오라 내가 너희를 쉬게 하리라”", "마태복음 11:28"),
            Pair("“하나님이 우리에게 주신 것은 두려워하는 마음이 아니요 오직 능력과 사랑과 근신하는 마음이니”", "디모데후서 1:7")
        ),
        "CUV" to listOf(
            Pair("“你们要休息，要知道我是神！我必在外邦中被尊崇，在遍地上也被尊崇。”", "诗篇 46:10"),
            Pair("“耶和华是我的牧者，我必不至缺乏。”", "诗篇 23:1"),
            Pair("“你们要将一切的忧虑卸给神，因为他顾念你们。”", "彼得前书 5:7"),
            Pair("“我靠着那加给我力量的，凡事都能做。”", "腓立比书 4:13"),
            Pair("“你要专心仰赖耶和华，不可倚靠自己的聪明。”", "箴言 3:5"),
            Pair("“凡劳苦担重担的人可以到我这里来，我就使你们得安息。”", "马太福音 11:28"),
            Pair("“因为神赐给我们，不是胆怯的心，乃是刚强、仁爱、谨守的心。”", "提摩太后书 1:7")
        ),
                "LSG" to listOf(
            Pair("“Arrêtez, et sachez que je suis Dieu: Je domine sur les nations, je domine sur la terre.”", "PSAUMES 46:10"),
            Pair("“L'Éternel est mon berger: je ne manquerai de rien.”", "PSAUMES 23:1"),
            Pair("“et déchargez-vous sur lui de tous vos soucis, car lui-même prend soin de vous.”", "1 PIERRE 5:7"),
            Pair("“Je puis tout par celui qui me fortifie.”", "PHILIPPIENS 4:13"),
            Pair("“Confie-toi en l'Éternel de tout ton cœur, Et ne t'appuie pas sur ta sagesse;”", "PROVERBES 3:5"),
            Pair("“Venez à moi, vous tous qui êtes fatigués et chargés, et je vous donnerai du repos.”", "MATTHIEU 11:28"),
            Pair("“Car ce n'est pas un esprit de timidité que Dieu nous a donné, mais un esprit de force, d'amour et de sagesse.”", "2 TIMOTHÉE 1:7")
        ),
        "BFC" to listOf(
            Pair("“Arrêtez, et sachez que je suis Dieu: Je domine sur les nations, je domine sur la terre.”", "PSAUMES 46:10"),
            Pair("“L'Éternel est mon berger: je ne manquerai de rien.”", "PSAUMES 23:1"),
            Pair("“et déchargez-vous sur lui de tous vos soucis, car lui-même prend soin de vous.”", "1 PIERRE 5:7"),
            Pair("“Je puis tout par celui qui me fortifie.”", "PHILIPPIENS 4:13"),
            Pair("“Confie-toi en l'Éternel de tout ton cœur, Et ne t'appuie pas sur ta sagesse;”", "PROVERBES 3:5"),
            Pair("“Venez à moi, vous tous qui êtes fatigués et chargés, et je vous donnerai du repos.”", "MATTHIEU 11:28"),
            Pair("“Car ce n'est pas un esprit de timidité que Dieu nous a donné, mais un esprit de force, d'amour et de sagesse.”", "2 TIMOTHÉE 1:7")
        ),
        "NVI-PT" to listOf(
            Pair("“Parem de lutar! Saibam que eu sou Deus! Serei exaltado entre as nações, serei exaltado na terra.”", "SALMOS 46:10"),
            Pair("“O Senhor é o meu pastor; de nada terei falta.”", "SALMOS 23:1"),
            Pair("“Lancem sobre ele toda a sua ansiedade, porque ele tem cuidado de vocês.”", "1 PEDRO 5:7"),
            Pair("“Tudo posso naquele que me fortalece.”", "FILIPENSES 4:13"),
            Pair("“Confie no Senhor de todo o seu coração e não se apoie em seu próprio entendimento;”", "PROVÉRBIOS 3:5"),
            Pair("“Venham a mim, todos os que estão cansados e sobrecarregados, e eu lhes darei descanso.”", "MATEUS 11:28"),
            Pair("“Pois Deus não nos deu espírito de covardia, mas de poder, de amor e de equilíbrio.”", "2 TIMÓTEO 1:7")
        ),
        "ARC" to listOf(
            Pair("“Aquietai-vos e sabei que eu sou Deus; serei exaltado entre as nações; serei exaltado sobre a terra.”", "SALMOS 46:10"),
            Pair("“O Senhor é o meu pastor; nada me faltará.”", "SALMOS 23:1"),
            Pair("“lançando sobre ele toda a vossa ansiedade, porque ele tem cuidado de vós.”", "1 PEDRO 5:7"),
            Pair("“Posso todas as coisas naquele que me fortalece.”", "FILIPENSES 4:13"),
            Pair("“Confia no Senhor de todo o teu coração e não te estribes no teu próprio entendimento.”", "PROVÉRBIOS 3:5"),
            Pair("“Vinde a mim, todos os que estais cansados e oprimidos, e eu vos aliviarei.”", "MATEUS 11:28"),
            Pair("“Porque Deus não nos deu o espírito de temor, mas de fortaleza, e de amor, e de moderação.”", "2 TIMÓTEO 1:7")
        ),
        "NKJV" to listOf(
            Pair("“Be still, and know that I am God; I will be exalted among the nations, I will be exalted in the earth!”", "PSALM 46:10"),
            Pair("“The Lord is my shepherd; I shall not want.”", "PSALM 23:1"),
            Pair("“Casting all your care upon Him, for He cares for you.”", "1 PETER 5:7"),
            Pair("“I can do all things through Christ who strengthens me.”", "PHILIPPIANS 4:13"),
            Pair("“Trust in the Lord with all your heart, And lean not on your own understanding.”", "PROVERBS 3:5"),
            Pair("“Come to Me, all you who labor and are heavy laden, and I will give you rest.”", "MATTHEW 11:28"),
            Pair("“For God has not given us a spirit of fear, but of power and of love and of a sound mind.”", "2 TIMOTHY 1:7")
        )
    )

    fun getDailyVerse(version: String, dayOfYear: Int): Pair<String, String> {
        val mappedVersion = if (versesByVersion.containsKey(version)) version else "NIV"
        val list = versesByVersion[mappedVersion]!!
        return list[dayOfYear % list.size]
    }
}
