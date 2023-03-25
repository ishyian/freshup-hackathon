package com.melitopolcherry.hackathon.data.models

enum class EventCategory(val categoryName: String, val id: Int) {
    CONCERTS("Concerts", 1),
    WORLD("World Music", 2),
    OTHER_FAMILY_FRIENDLY_EVENTS("Other Family Friendly Events", 3),
    SPORTS("Sports", 4),
    BASEBALL("Baseball", 5),
    BASKETBALL("Basketball", 6),
    EXTREME("Extreme Sport", 7),
    FOOTBALL("Football", 8),
    GOLF("Golf", 9),
    GYMNASTICS("Gymnastics", 10),
    HOCKEY("Hockey", 11),
    LACROSSE("Lacrosse", 12),
    OLYMPICS("Olympics", 13),
    INTERNATIONAL_GAMES("International Games", 14),
    RACING("Racing", 15),
    SKATING("Skating", 16),
    SOCCER("Soccer", 17),
    TENNIS("Tennis", 18),
    VOLLEYBALL("Volleyball", 19),
    WRESTLING("Wrestling", 20),
    FIGHTING("Fighting", 21),
    MIXED_MARTIAL_ARTS("Mixed Martial Arts", 22),
    SOFTBALL("Softball", 23),
    CHILDREN_OR_FAMILY("Children / Family", 24),
    ARTS_AND_THEATER("Arts & Theater", 25),
    OTHER("Other", 99);

    companion object {
        fun findMainCategoryById(mainCategoryId: Int): EventCategory? {
            return values().firstOrNull { category -> category.id == mainCategoryId }
        }
    }
}