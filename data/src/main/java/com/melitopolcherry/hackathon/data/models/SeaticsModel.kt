package com.melitopolcherry.hackathon.data.models

data class SeaticsModel(
    var tgUserSec: String? = null,
    var tgUserRow: String? = null,
    var tgQty: Int? = null,
    var tgPrice: Double? = null,
    var tgID: Int? = null,
    var tgVfsUrl: String? = null,
    var tgVfsUrlLarge: String? = null
) {
    override fun toString(): String {
        return "{tgUserSec: \"$tgUserSec\", tgUserRow: \"$tgUserRow\", tgQty: ${tgQty}, tgPrice: ${tgPrice?.toFloat()}, tgID: ${tgID}}"
    }
}