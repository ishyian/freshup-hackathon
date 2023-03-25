package com.melitopolcherry.hackathon.data.models

import com.melitopolcherry.hackathon.data.utils.Enums

class TicketsFilter {

    var selectedQuantities: ArrayList<Int> = arrayListOf()
//    var selectedTicketTypes = arrayListOf(
//        Enums.TicketFormats.PHYSICAL,
//        Enums.TicketFormats.ETICKET,
//        Enums.TicketFormats.FLASH_SEATS,
//        Enums.TicketFormats.TM_MOBILE,
//        Enums.TicketFormats.TM_MOBILE_LINK,
//        Enums.TicketFormats.GUEST_LIST,
//        Enums.TicketFormats.PAPERLESS,
//        Enums.TicketFormats.DIRECT_TRANSFER,
//        Enums.TicketFormats.WILL_CALL,
//        Enums.TicketFormats.UPLOAD_QR_CODE
//    )
    var selectedSeatTypes = arrayListOf<Enums.TicketType>()
    var minRangePosition: Double? = null
    var minPriceRange: Int? = null
    var maxRangePosition: Double? = null
    var maxPriceRange: Int? = null

    override fun toString(): String {
        return "TicketsFilter(selectedQuantities=$selectedQuantities,\n selectedSeatTypes=$selectedSeatTypes\n minRangePosition=$minRangePosition, \nminPriceRange=$minPriceRange, \nmaxRangePosition=$maxRangePosition, \nmaxPriceRange=$maxPriceRange"
    }
}