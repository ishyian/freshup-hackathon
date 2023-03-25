package com.melitopolcherry.hackathon.data.model

import com.melitopolcherry.hackathon.data.R

data class University(
    val id: String,
    val name: String,
    val description: String,
    val image: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

val UNIVERSITIES = listOf(
    University(
        "mdpu",
        "Мелітопольський державний педагогічний університет імені Богдана Хмельницького",
        "Мелітопольський державний педагогічний університет імені Богдана Хмельницького — один з університетів у місті Мелітополь, який існує з 1923 року.",
        R.drawable.mdpu,
        "вулиця Гетьманська, 20, Мелітополь, Запорізька область",
        46.84306487908948,
        35.37820026931577
    ),
    University(
        "dnu",
        "Дніпровський національний університет імені Олеся Гончара",
        "Дніпровський національний університет імені Олеся Гончара — один із провідних закладів вищої освіти в Україні четвертого рівня акредитації з правом підготовки іноземців за акредитованими напрямами й спеціальностями.",
        R.drawable.dnu,
        " проспект Гагаріна, 72, Дніпро, Дніпропетровська область",
        48.433818624695185,
        35.0427966
    )
)

data class Faculty(
    val id: String,
    val universityId: String,
    val name: String,
    val image: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val groups: List<String>
)

val FACULTIES = listOf(
    Faculty(
        "fac1",
        "mdpu",
        "Факультет інформатики, математики та економіки",
        "https://mdpu.org.ua/wp-content/uploads/2019/08/mdpu_aspirant.jpg",
        "вул.Гетьманська, 20, м.Мелітополь, Запорізька область, Україна",
        46.842331335036135,
        35.37828043285787,
        listOf("322і", "419і", "М122і")
    ),
    Faculty(
        "fac2",
        "mdpu",
        "Філологічний факультет",
        "https://mdpu.org.ua/wp-content/uploads/2019/08/mdpu_aspirant.jpg",
        "вулиця Гетьманська, 20, Мелітополь, Запорізька область, 72300",
        46.842331335036135,
        35.37828043285787,
        listOf("112/1ф", "110ф", "412ф")
    ),
    Faculty(
        "fac3",
        "mdpu",
        "Природничо-географічний факультет",
        "https://i.imgur.com/ClE9xxp.png",
        "вулиця Гетьманська, 10, Мелітополь, Запорізька область, 72300",
        46.84225214639486,
        35.38292843435228,
        listOf("131п", "210п", "519п")
    ),
    Faculty(
        "fac4",
        "mdpu",
        "Хіміко-біологічний факультет",
        "https://mdpu.org.ua/wp-content/uploads/2019/08/mdpu_aspirant.jpg",
        "вулиця Гетьманська, 10, Мелітополь, Запорізька область, 72300",
        46.842331335036136,
        35.37828043285788,
        listOf("320х", "422/2х", "M222х")
    ),
    Faculty(
        "fac5",
        "dnu",
        "Факультет української й іноземної філології та мистецтвознавства",
        "https://api.new.buki.com.ua/news_image/dR/LY/dRLY63H3j7sU1T3GCQGC9YesUs0QZ41SzKzD5RzJ.jpg",
        "проспект Гагаріна, 72, Дніпро, Дніпропетровська область (Корпус 1)",
        48.433171909996425,
        35.043162375613555,
        listOf()
    ),
    Faculty(
        "fac6",
        "dnu",
        "Факультет суспільних наук і міжнародних відносин",
        "https://api.new.buki.com.ua/news_image/dR/LY/dRLY63H3j7sU1T3GCQGC9YesUs0QZ41SzKzD5RzJ.jpg",
        "проспект Гагаріна, 72, Дніпро, Дніпропетровська область (Корпус 1)",
        48.433171909996425,
        35.043162375613555,
        listOf()
    ),
    Faculty(
        "fac7",
        "dnu",
        "Історичний факультет",
        "https://api.new.buki.com.ua/news_image/dR/LY/dRLY63H3j7sU1T3GCQGC9YesUs0QZ41SzKzD5RzJ.jpg",
        "проспект Гагаріна, 72, Дніпро, Дніпропетровська область (Корпус 1)",
        48.433171909996425,
        35.043162375613555,
        listOf()
    ),
    Faculty(
        "fac8",
        "dnu",
        "Факультет психології та спеціальної освіти",
        "https://www.dnu.dp.ua/images/facults/fak_psy_spec_osvity.jpg",
        "проспект Дмитра Яворницького, 35, Дніпро, Дніпропетровська область (Корпус 4)",
        48.458509291439746,
        35.05655162619412,
        listOf()
    ),
    Faculty(
        "fac9",
        "dnu",
        "Факультет прикладної математики",
        "https://lh3.googleusercontent.com/u/0/drive-viewer/AAOQEOSkKJg1GGffkzbHze8yFBdS1rSusaGGp6s-XbckK6mFTETHtLW_KdP43bLbaT0uz7n5K_ou-5othwtTrHZxqnuLVIiU-w=w2880-h1472",
        "проспект Дмитра Яворницького, 35, Дніпро, Дніпропетровська область (Корпус 3)",
        48.458509291439746,
        35.05655162619412,
        listOf()
    ),
    Faculty(
        "fac10",
        "dnu",
        "Факультет економіки",
        "https://lh3.googleusercontent.com/u/0/drive-viewer/AAOQEOQiWywrQndjTMr3tqbv5msKK8T_Qv8dbb5GsABnZVcwP9PPZFDxhXjOIe-sCkTiNhD1YhNDC5Ww9d6N-GE7P1GVVKBt=w2880-h1472",
        "проспект Дмитра Яворницького, 35, Дніпро, Дніпропетровська область (Корпус 5)",
        48.458509291439746,
        35.05655162619412,
        listOf()
    ),
    Faculty(
        "fac11",
        "dnu",
        "Факультет систем і засобів масової комунікації",
        "https://lh3.googleusercontent.com/u/0/drive-viewer/AAOQEOQYFabHH5UE7xDqF3rAgps-mzIMvlkdHoUyMlOwCdy5CpH9j6fO21HuGO3_STUiE4MBVvRMqmmKC51QzSJNykS5eV9G=w2880-h1472",
        "вулиця Наукова, 13, Дніпро, Дніпропетровська область (Корпус 9)",
        48.458509291439746,
        35.05655162619412,
        listOf()
    ),
)

data class Event(
    val id: String,
    val name: String,
    val teacherName: String,
    val group: String,
    val facultyId: String,
    val dateTime: String,
    val type: String,
    val classroom: String
)

val EVENTS = listOf(
    Event(
        "ev1",
        "Математичний аналіз",
        "Конюхов С.Л.",
        "122 і",
        "fac1",
        "05-03-2022 08:00",
        "lecture",
        "Online"
    ),
    Event(
        "ev2",
        "Інформатика",
        "Конюхов С.Л.",
        "122 і",
        "fac1",
        "05-03-2022 10:00",
        "practice",
        "Online"
    ),
    Event(
        "ev3",
        "Лінійна алгебра",
        "Конюхов С.Л.",
        "122 і",
        "fac1",
        "05-03-2022 12:00",
        "practice",
        "Online"
    )
)