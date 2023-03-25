package com.melitopolcherry.hackathon.data.models.protect

import com.google.gson.annotations.SerializedName

data class ProtechtResponse(

    @field:SerializedName("surcharge")
    val surcharge: Double? = null,

    @field:SerializedName("privacy_policy_url")
    val privacyPolicyUrl: String? = null,

    @field:SerializedName("symbol")
    val symbol: String? = null,

    @field:SerializedName("product")
    val product: String? = null,

    @field:SerializedName("quote_literal")
    val quoteLiteral: String? = null,

    @field:SerializedName("verbiage")
    val verbiage: Verbiage? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("total")
    val total: String? = null,

    @field:SerializedName("token_opt_out")
    val tokenOptOut: String? = null,

    @field:SerializedName("quote")
    val quote: List<QuoteItem>? = null,

    @field:SerializedName("learn_more_url")
    val learnMoreUrl: String? = null,

    @field:SerializedName("client")
    val client: Client? = null,

    @field:SerializedName("currency")
    val currency: String? = null,

    @field:SerializedName("order_literal")
    val orderLiteral: String? = null,

    @field:SerializedName("links")
    val links: Links? = null,

    @field:SerializedName("underwriter")
    val underwriter: Underwriter? = null,

    @field:SerializedName("tnc_url")
    val tncUrl: String? = null
)

data class Links(

    @field:SerializedName("tncUrlFlorida")
    val tncUrlFlorida: String? = null,

    @field:SerializedName("tncUrlJSV")
    val tncUrlJSV: String? = null,

    @field:SerializedName("tncUrl")
    val tncUrl: String? = null,

    @field:SerializedName("learnMoreUrl")
    val learnMoreUrl: String? = null,

    @field:SerializedName("learnMoreUrlJSV")
    val learnMoreUrlJSV: String? = null,

    @field:SerializedName("privacyPolicyUrl")
    val privacyPolicyUrl: String? = null,

    @field:SerializedName("privacyPolicyUrlJSV")
    val privacyPolicyUrlJSV: String? = null
)

data class Client(

    @field:SerializedName("is_rev_share")
    val isRevShare: Boolean? = null,

    @field:SerializedName("domain")
    val domain: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("external_id")
    val externalId: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("affiliate")
    val affiliate: Affiliate? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class Verbiage(

    @field:SerializedName("protect_purchase_title")
    val protectPurchaseTitle: String? = null,

    @field:SerializedName("unavailable_purchase_title")
    val unavailablePurchaseTitle: String? = null,

    @field:SerializedName("error_selection_text")
    val errorSelectionText: String? = null,

    @field:SerializedName("selection_required_text")
    val selectionRequiredText: String? = null,

    @field:SerializedName("error_content_text")
    val errorContentText: String? = null,

    @field:SerializedName("exact_privacy_policy_text")
    val exactPrivacyPolicyText: String? = null,

    @field:SerializedName("exact_tnc_text")
    val exactTncText: String? = null,

    @field:SerializedName("decline_text")
    val declineText: String? = null,

    @field:SerializedName("underwriter_text")
    val underwriterText: String? = null,

    @field:SerializedName("price_tier_error_text")
    val priceTierErrorText: String? = null,

    @field:SerializedName("fan_shield_recommendation_text")
    val fanShieldRecommendationText: String? = null,

    @field:SerializedName("protect_text")
    val protectText: String? = null,

    @field:SerializedName("learn_more_text")
    val learnMoreText: String? = null,

    @field:SerializedName("recommendation_text")
    val recommendationText: String? = null,

    @field:SerializedName("terms_and_conditions_text")
    val termsAndConditionsText: String? = null,

    @field:SerializedName("content_mandated_text")
    val contentMandatedText: String? = null,

    @field:SerializedName("content_option_text")
    val contentOptionText: String? = null,

    @field:SerializedName("content_unavailable_text")
    val contentUnavailableText: String? = null,

    @field:SerializedName("mandated_text_with_logo")
    val mandatedTextWithLogo: String? = null,

    @field:SerializedName("peril_text")
    val perilText: List<PerilTextItem>? = null,

    @field:SerializedName("protect_against_title")
    val protectAgainstTitle: String? = null,

    @field:SerializedName("protect_text_with_logo")
    val protectTextWithLogo: String? = null,

    @field:SerializedName("underwriter")
    val underwriter: String? = null,

    @field:SerializedName("mandated_text")
    val mandatedText: String? = null
)

data class PerilTextItem(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("title")
    val title: String? = null
)

data class Affiliate(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: String? = null
)

data class Underwriter(

    @field:SerializedName("dba")
    val dba: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("excluded_states")
    val excludedStates: List<String>? = null,

    @field:SerializedName("tnc_url")
    val tncUrl: String? = null
)

data class QuoteItem(

    @field:SerializedName("surcharge")
    val surcharge: Double? = null,

    @field:SerializedName("premium")
    val premium: Double? = null,

    @field:SerializedName("coverage_amount")
    val coverageAmount: Double? = null,

    @field:SerializedName("reference_number")
    val referenceNumber: String? = null
)
