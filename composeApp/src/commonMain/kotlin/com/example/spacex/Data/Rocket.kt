package com.example.spacex.Data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rockets(
    val id: Long? = null,
    val active: Boolean? = null,
    val stages: Int? = null,
    val boosters: Int? = null,
    @SerialName("cost_per_launch")
    val cost_per_launch: Long? = null,
    @SerialName("success_rate_pct")
    val success_rate_pct: Int? = null,
    val first_flight: String? = null,
    val country: String? = null,
    val company: String? = null,
    val height: Measure? = null,
    val diameter: Measure? = null,
    val mass: Mass? = null,
    @SerialName("payload_weights")
    val payload_weights: List<PayloadWeight>? = null,
    @SerialName("first_stage")
    val first_stage: FirstStageRocket? = null,
    @SerialName("second_stage")
    val second_stage: SecondStageRocket? = null,
    val engines: Engines? = null,
    @SerialName("landing_legs")
    val landing_legs: LandingLegs? = null,
    @SerialName("flickr_images")
    val flickr_images: List<String>? = null,
    val wikipedia: String? = null,
    val description: String? = null,
    @SerialName("rocket_id")
    val rocket_id: String? = null,
    @SerialName("rocket_name")
    val rocket_name: String? = null,
    @SerialName("rocket_type")
    val rocket_type: String? = null,

    val is_favorite: Boolean? = false
)

@Serializable
data class Measure(
    val meters: Double? = null,
    val feet: Double? = null
)

@Serializable
data class Mass(
    val kg: Int? = null,
    val lb: Int? = null
)

@Serializable
data class PayloadWeight(
    val id: String? = null,
    val name: String? = null,
    val kg: Int? = null,
    val lb: Int? = null
)

@Serializable
data class FirstStageRocket(
    val reusable: Boolean? = null,
    val engines: Int? = null,
    @SerialName("fuel_amount_tons")
    val fuel_amount_tons: Double? = null,
    @SerialName("burn_time_sec")
    val burn_time_sec: Int? = null,
    @SerialName("thrust_sea_level")
    val thrust_sea_level: Thrust? = null,
    @SerialName("thrust_vacuum")
    val thrust_vacuum: Thrust? = null
)

@Serializable
data class SecondStageRocket(
    val reusable: Boolean? = null,
    val engines: Int? = null,
    @SerialName("fuel_amount_tons")
    val fuel_amount_tons: Double? = null,
    @SerialName("burn_time_sec")
    val burn_time_sec: Int? = null,
    val thrust: Thrust? = null,
    val payloads: Payloads? = null
)

@Serializable
data class Payloads(
    val option_1: String? = null,
    val composite_fairing: CompositeFairing? = null
)

@Serializable
data class CompositeFairing(
    val height: Measure? = null,
    val diameter: Measure? = null
)

@Serializable
data class Thrust(
    val kN: Int? = null,
    val lbf: Int? = null
)

@Serializable
data class Engines(
    val number: Int? = null,
    val type: String? = null,
    val version: String? = null,
    val layout: String? = null,
    val isp: Isp? = null,
    @SerialName("engine_loss_max")
    val engine_loss_max: Int? = null,
    @SerialName("propellant_1")
    val propellant_1: String? = null,
    @SerialName("propellant_2")
    val propellant_2: String? = null,
    @SerialName("thrust_sea_level")
    val thrust_sea_level: Thrust? = null,
    @SerialName("thrust_vacuum")
    val thrust_vacuum: Thrust? = null,
    @SerialName("thrust_to_weight")
    val thrust_to_weight: Double? = null
)

@Serializable
data class Isp(
    @SerialName("sea_level")
    val sea_level: Int? = null,
    val vacuum: Int? = null
)

@Serializable
data class LandingLegs(
    val number: Int? = null,
    val material: String? = null
)