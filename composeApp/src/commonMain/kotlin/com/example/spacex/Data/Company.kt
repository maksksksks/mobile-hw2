package com.example.spacex.Data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyInfo(
    val name: String? = null,
    val founder: String? = null,
    val founded: Int? = null,
    val employees: Int? = null,
    val vehicles: Int? = null,
    @SerialName("launch_sites")
    val launch_sites: Int? = null,
    @SerialName("test_sites")
    val test_sites: Int? = null,
    val ceo: String? = null,
    val cto: String? = null,
    val coo: String? = null,
    @SerialName("cto_propulsion")
    val cto_propulsion: String? = null,
    val valuation: Long? = null,
    val headquarters: Headquarters? = null,
    val links: CompanyLinks? = null,
    val summary: String? = null
)

@Serializable
data class Headquarters(
    val address: String? = null,
    val city: String? = null,
    val state: String? = null
)

@Serializable
data class CompanyLinks(
    val website: String? = null,
    val flickr: String? = null,
    val twitter: String? = null,
    @SerialName("elon_twitter")
    val elon_twitter: String? = null
)

@Serializable
data class HistoryEvent(
    val id: Int? = null,
    val title: String? = null,
    @SerialName("event_date_utc")
    val event_date_utc: String? = null,
    @SerialName("event_date_unix")
    val event_date_unix: Long? = null,
    @SerialName("flight_number")
    val flight_number: Int? = null,
    val details: String? = null,
    val links: HistoryLinks? = null
)

@Serializable
data class HistoryLinks(
    val reddit: String? = null,
    val article: String? = null,
    val wikipedia: String? = null
)