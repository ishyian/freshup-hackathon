package com.melitopolcherry.hackathon.data.utils

import java.util.Locale

abstract class Enums {

    enum class SearchState {
        Search,
        Cities
    }

    enum class BundleCodes {
        Event, EventId, Ticket, TicketCount, TicketGroups, TicketGroupId, LoginType,
        PaymentMove, NotificationMove, LandingId, OrderId, Point, EntityType, AUTO;
    }

    enum class ErrorCodes(val description: String) {
        BAD_REQUEST("Oops... Something went wrong."),
        INTERNAL_ERROR("Oops... Something went wrong."),
        NOT_FOUND("Sorry, we can't find what you are looking for."),
        UNAUTHORIZED("Sorry, backstage access only. You don't have permissions to perform this request."),
        AUTHENTICATION_FAILED("We couldn't process your account registration. Please try again."),
        AUTHENTICATION_REQUIRED("Sorry, backstage access only. You don't have permissions to perform this request."),
        EMAIL_NOT_VERIFIED("Please check your email and verify your Evenz account and try again!"),
        EMAIL_NOT_FOUND("Couldn't find Evenz account associated with this email. Try again to join the party."),
        PAYMENT_METHOD_REQUIRED("Please update your payment preferences in order to perform this request."),
        TICKETS_COUNT_NA("Requested ticket count not available."),
        ADDRESS_REQUIRED("Please update your address when checking out."),
        PHONE_NUMBER_REQUIRED("Please update your phone number when checking out."),
        TICKET_FORMAT_NOT_SUPPORTED("Ticket format not supported."),
        EMAIL_ALREADY_EXISTS("We already have an account associated with this email address."),
        PHONE_NUMBER_ALREADY_EXISTS("We already have an account associated with this phone number."),
        PHONE_NUMBER_ALREADY_VERIFIED("This phone number is already verified with Evenz."),
        PASSWORD_NOT_SUPPORTED_BY_AUTH_METHOD("Password not supported by authentication method."),
        INCORRECT_PASSWORD("Oops, incorrect password."),
        PASSWORDS_DIDNT_MATCH("Make sure that your passwords match!"),
        FACEBOOK_ERROR("Unable to connect to Facebook. Please try again."),
        AUTH_PROVIDER_EMAIL_REQUIRED("Please make sure that the authentication method you are using is affiliated with a valid email address."),
        CODE_NOT_VALID("Invalid verification code. Paste in the verification code sent to your email."),
        CODE_NOT_FOUND("Promo code not found."),
        INCORRECT_CVC("Incorrect security code."),
        INVALID_EXPIRATION_MONTH("Incorrect expiration month."),
        INVALID_EXPIRATION_YEAR("Incorrect expiration year."),
        CARD_DECLINED("Declined card."),
        CARD_EXPIRED("Expired credit card."),
        CARD_PROCESSING_ERROR("Processing error. Try again"),
        CARD_NUMBER_INCORRECT("Incorrect card number."),
        NEW_PASSWORD_IS_THE_SAME("Switch up your new password and try again."),
        ORDER_ALREADY_EXISTS("This order is already processing."),
        CODE_GENERATION_TIMEOUT("This code was already generated.");

        companion object {
            fun valueOf(code: String): ErrorCodes {
                for (e in values()) {
                    if (e.description == code) return e
                }
                return BAD_REQUEST
            }
        }
    }

    enum class EventCategories(val value: String) {
        SHELTERS("shelter"),
        SHTAB("shtab"),
        UNBROKEN("unbroken"),
        VOLUNTEER("volunteer");

        companion object {
            fun categoryOf(value: String): String {
                for (e in values()) {
                    if (e.name == value) {
                        return e.value
                    }
                }
                return SHELTERS.value
            }

            fun getCategoryBy(value: String): EventCategories {
                for (e in values()) {
                    if (e.value == value) {
                        return e
                    }
                }
                return SHELTERS
            }
        }
    }

    enum class TicketFormats(val value: String) {
        PHYSICAL("PHYSICAL"),
        ETICKET("ETICKET"),
        FLASH_SEATS("FLASH_SEATS"),
        TM_MOBILE("TM_MOBILE"),
        TM_MOBILE_LINK("TM_MOBILE_LINK"),
        GUEST_LIST("GUEST_LIST"),
        PAPERLESS("PAPERLESS"),
        DIRECT_TRANSFER("DIRECT_TRANSFER"),
        WILL_CALL("WILL_CALL"),
        UPLOAD_QR_CODE("UPLOAD_QR_CODE"),
        UNDEFINED("Undefined");

        companion object {
            fun getFormatBy(value: String): TicketFormats {
                for (e in values()) {
                    if (e.value == value) {
                        return e
                    }
                }
                return UNDEFINED
            }
        }

        fun getFormattedName(value: TicketFormats): String {
            var s: String
            do {
                s = value.value.replace("_", " ")
            } while (s.contains("_"))

            return s.lowercase(Locale.getDefault()).capitalize(Locale.getDefault())
        }
    }

    enum class TicketDescType(val value: String) {
        ETICKET("ETICKET"),
        FLASH_SEATS("FLASH_SEATS"),
        GUEST_LIST("GUEST_LIST"),
        PAPERLESS("PAPERLESS"),
        PHYSICAL("PHYSICAL"),
        TM_MOBILE("TM_MOBILE"),
        TM_MOBILE_LINK("TM_MOBILE_LINK"),
        WILL_CALL("WILL_CALL"),
        DIRECT_TRANSFER("DIRECT_TRANSFER"),
        UPLOAD_QR_CODE("UPLOAD_QR_CODE");

        companion object {
            fun getDescByType(value: TicketDescType): String {
                return when (value) {
                    ETICKET -> {
                        "Ticket(s) will arrive to you via email. The tickets must be printed before you arrive to the venue"
                    }
                    FLASH_SEATS -> {
                        "Tickets will arrive to you via an application called Flash Seats. An email will be sent to you to accept the tickets via the Flash seats app. If you do not already have an account, you will be promoted to sign up. Please do not print these tickets. the venue will only accept the tickets if they shown through a Flash Seats app."
                    }
                    GUEST_LIST -> {
                        "Customers placed on a guest list will not receive any type of tickets. Closer to the event you will receive information on your guest list access venue entry point. Please make sure at check out all our contact information is correct."
                    }
                    PAPERLESS -> {
                        "Ticket(s) are typically stored on a card similar to a credit card but this card only stores tickets.. Prior to the event we will ship or arranged a local pick of  the card. Upon entry you will just need to swipe the card to gain venue access."
                    }
                    PHYSICAL -> {
                        "Ticket(s) will be shipped to the address provided at the time of checkout or a local to venue pick up will be arranged"
                    }
                    TM_MOBILE -> {
                        "Tickets will arrive to you via the Ticketmaster web or mobile app.  A \"transfer offer\"  will be sent to the email provided at check out and you will be only be able to accept the offer through the app. If you do not already have an account, you will be promoted to sign up. Please do not print these tickets. the venue will only permit entrance if the tickets are shown on a mobile device."
                    }
                    TM_MOBILE_LINK -> {
                        "Tickets(s) will be delivered via a Ticketmaster link which will be sent to the email provided at check out. Just click the link and follow the instructions to login in or create a Ticketmaster account.  Please do not print these tickets. the venue will only permit entrance if the tickets are shown on a mobile device."
                    }
                    WILL_CALL -> {
                        "Ticket(s) will be held at the Will Call window of the venue or box office. A photo ID is required to receive the tickets, therefore be sure to provide correct customer information at checkout."
                    }
                    DIRECT_TRANSFER -> {
                        "Ticket(s) will arrive via the TM Mlobile app or Flash Seats. The ticket(s) offer or transfer will come  from an outside source and not directly from Evenz.co."
                    }
                    UPLOAD_QR_CODE -> {
                        "Ticket(s) will arrive to email provided at check out. Download the image and show on a mobile device to gain entry. Please do not print these tickets. the venue will only permit entrance if the tickets are shown on a mobile device."
                    }
                }
            }

            fun getType(s: String): TicketDescType {
                for (e in values()) {
                    if (e.value == s) {
                        return e
                    }
                }
                return ETICKET
            }
        }
    }

    enum class TicketType(val value: String) {
        EVENT("EVENT"),
        PARKING("PARKING"),
        ADA("ADA"),
        UNDEFINED("Undefined");

        companion object {
            fun getTypeBy(value: String): TicketType {
                for (e in values()) {
                    if (e.value == value) {
                        return e
                    }
                }
                return UNDEFINED
            }
        }

        fun getName(value: TicketType): String {
            return value.value.lowercase(Locale.getDefault()).capitalize(Locale.getDefault())
        }

        //        fun getDesc(type: TicketType) {
        //            when (type) {
        //                EVENT -> {
        //
        //                }
        //                PARKING -> {
        //
        //                }
        //                UNDEFINED -> {
        //
        //                }
        //            }
        //        }
    }

    enum class TicketSource(val value: String) {
        TICKET_EVOLUTION("TICKET_EVOLUTION"),
        TICKET_NETWORK("TICKET_NETWORK"),
        UNDEFINED("Undefined");

        companion object {
            fun getSourceBy(value: String): TicketSource {
                for (e in values()) {
                    if (e.value == value) {
                        return e
                    }
                }
                return UNDEFINED
            }
        }
    }

    enum class StadiumState {
        HAS_STADIUM, NO_STADIUM;
    }

    enum class EventState constructor(val value: Int) {
        NONE(-1),
        DAY_DESELECTED(1),
        DAY_SELECTED(3),
        NIGHT_DESELECTED(0),
        NIGHT_SELECTED(2);

        companion object {
            fun valueOf(value: Int): EventState {
                for (e in values()) {
                    if (e.value == value)
                        return e
                }
                return NONE
            }
        }
    }

    enum class LoginMethods constructor(val value: Int) {
        NONE(-1),
        Email(0),
        Facebook(1),
        Google(2);

        companion object {
            fun valueOf(value: Int): LoginMethods {
                for (e in values()) {
                    if (e.value == value)
                        return e
                }
                return NONE
            }
        }
    }
}

//CONCERTS("Concerts"),
//FAMILY("Family"),
//COMEDY("Comedy"),
//ALTERNATIVE_AND_INDIE("Alternative & Indie"),
//R_AND_B_URBAN_SOUL("R&B/Urban Soul"),
//COUNTRY_AND_FOLK("Country & Folk"),
//JAZZ_AND_BLUES("Jazz & Blues"),
//FESTIVALS("Festivals"),
//WORLD_MUSIC("World Music"),
//CLASSICAL("Classical"),
//ALTERNATIVE_ROCK("Alternative Rock"),
//CABARET("Cabaret"),
//LATIN("Latin"),
//NEW_AGE_AND_SPIRITUAL("New Age & Spiritual"),
//RAP_AND_HIP_HOP("Rap & Hip-Hop"),
//ROCK_AND_POP("Rock & Pop"),
//MISCELLANEOUS("Miscellaneous"),
//HARD_ROCK_OR_METAL("Hard Rock/Metal"),
//DANCE_OR_ELECTRONIC("Dance/Electronic"),
//THEATRE("Theatre"),
//MUSICALS("Musicals"),
//PLAYS("Plays"),
//OPERA("Opera"),
//BALLET_AND_DANCE("Ballet & Dance"),
//ENTERTAINMENT_SHOWS("Entertainment Shows"),
//SPECIAL_EVENTS("Special Events"),
//AWARD_SHOWS("Award Shows"),
//TV_SHOWS("TV Shows"),
//FUNDRAISERS("Fundraisers"),
//EXCLUSIVE_PARTIES("Exclusive Parties"),
//SPORTS("Sports"),
//BASEBALL("Baseball"),
//MINOR_LEAGUE("Minor League"),
//WORLD_BASEBALL_CLASSIC("World Baseball Classic"),
//MEXICAN_LEAGUE("Mexican League"),
//MLB("MLB"),
//NCAA_BASEBALL("NCAA"),
//LACROSSE("Lacrosse"),
//NCAA_MEN_LACROSSE("NCAA Men"),
//NLL("NLL"),
//INTERNATIONAL_GAMES("International Games"),
//SUMMER_GAMES("Summer Games"),
//WINTER_GAMES("Winter Games"),
//BASKETBALL("Basketball"),
//NBA("NBA"),
//INTERNATIONAL_FRIENDLIES("International Friendlies"),
//HIGH_SCHOOL("High School"),
//NBAD_LEAGUE("NBA D-League"),
//WNBA("WNBA"),
//NCAA_MEN_BASKETBALL("NCAA Men"),
//NCAA_WOMEN_BASKETBALL("NCAA Women"),
//FIBA_WORLD_CHAMPIONSHIP("FIBA World Championship"),
//EUROLEAGUE_BASKETBALL("Euroleague Basketball"),
//HOCKEY("Hockey"),
//NCAA_MEN_HOCKEY("NCAA Men"),
//JUNIOR("Junior"),
//INTERNATIONAL("International"),
//AHL("AHL"),
//ECHL("ECHL"),
//NCAA_WOMEN_HOCKEY("NCAA Women"),
//NHL("NHL"),
//AUTO_RACING("Auto Racing"),
//INDY_CAR_SERIES("IndyCar Series"),
//FORMULA1("Formula 1"),
//MONSTER_TRUCKS("Monster Trucks"),
//NASCAR("NASCAR"),
//SUPERCROSS("Supercross"),
//FIGHTING("Fighting"),
//BOXING("Boxing"),
//MMA("Mixed Martial Arts (MMA)"),
//WRESTLING("Wrestling"),
//EXTREME_SPORTS("Extreme Sports"),
//X_GAMES("X-Games"),
//GOLF("Golf"),
//PGA("PGA"),
//CHAMPIONS("Champions"),
//LPGA("LPGA"),
//EUROPEAN("European"),
//AMATEUR("Amateur"),
//FOOTBALL("Football"),
//NFL("NFL"),
//AFL("AFL"),
//LINGERIE_FOOTBALL_LEAGUE("Lingerie Football League"),
//NCAA_FOOTBALL("NCAA"),
//CFL("CFL"),
//UFL("UFL"),
//SOCCER("Soccer"),
//MLS("MLS (Major League Soccer)"),
//NCAA_WOMEN_SOCCER("NCAA Women"),
//NCAA_MEN_SOCCER("NCAA Men"),
//WORLD_CUP("World Cup"),
//SCOTTISH_PREMIERE_LEAGUE("Scottish Premiere League"),
//ENGLISH_PREMIERE_LEAGUE("English Premiere League"),
//CONFEDERACAO_BRASILIEIRA_DE_FUTEBOL("Confederação Brasileira de Futebol"),
//BUNDESLIGA("Bundesliga"),
//SPANISH_PRIMERA_DIVISION("Spanish Primera División"),
//ITALIAN_SERIE_A("Italian Serie A"),
//LIGAT_HA_AL("Ligat HaAl"),
//RUGBY("Rugby"),
//RODEO("Rodeo"),
//TENNIS("Tennis"),
//CYCLING("Cycling"),
//VOLLEYBALL("Volleyball"),
//HORSE_RACING("Horse Racing"),
//OTHER("Other")