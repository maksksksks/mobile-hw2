package com.example.spacex.Data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Launch(
    val flight_number: Long? = null,
    val mission_name: String? = null,
    val mission_id: List<String>? = null,

    val upcoming: Boolean? = null,
    @SerialName("launch_date_unix")
    val launch_date_unix: Long? = null,

    val launch_year: String? = null,
    val launch_date_utc: String? = null,
    val launch_date_local: String? = null,
    val is_tentative: Boolean? = null,
    val tentative_max_precision: String? = null,
    val tbd: Boolean? = null,
    val launch_window: Int? = null,

    val rocket: Rocket? = null,
    val ships: List<String>? = null,
    val telemetry: Telemetry? = null,
    val launch_site: LaunchSite? = null,
    val launch_success: Boolean? = null,
    val launch_failure_details: LaunchFailureDetails? = null,
    val links: Links? = null,
    val details: String? = null,
    val static_fire_date_utc: String? = null,
    val static_fire_date_unix: Long? = null,
    val timeline: Timeline? = null,
    val crew: List<String>? = null,

    val is_favorite: Boolean? = false
)

@Serializable
data class Rocket(
    val rocket_id: String? = null,
    val rocket_name: String? = null,
    val rocket_type: String? = null,
    val first_stage: FirstStage? = null,
    val second_stage: SecondStage? = null,
    val fairings: Fairings? = null
)

@Serializable
data class FirstStage(
    val cores: List<Core>? = null
)

@Serializable
data class Core(
    val core_serial: String? = null,
    val flight: Int? = null,
    val block: Int? = null,
    val gridfins: Boolean? = null,
    val legs: Boolean? = null,
    val reused: Boolean? = null,
    val land_success: Boolean? = null,
    val landing_intent: Boolean? = null,
    val landing_type: String? = null,
    val landing_vehicle: String? = null
)

@Serializable
data class SecondStage(
    val block: Int? = null,
    val payloads: List<Payload>? = null
)

@Serializable
data class Payload(
    val payload_id: String? = null,
    val norad_id: List<Long>? = null,
    val reused: Boolean? = null,
    val customers: List<String>? = null,
    val nationality: String? = null,
    val manufacturer: String? = null,
    val payload_type: String? = null,
    val payload_mass_kg: Double? = null,
    val payload_mass_lbs: Double? = null,
    val orbit: String? = null,
    val orbit_params: OrbitParams? = null
)
@Serializable
data class OrbitParams(
    val reference_system: String? = null,
    val regime: String? = null,
    val longitude: Double? = null,
    val semi_major_axis_km: Double? = null,
    val eccentricity: Double? = null,
    val periapsis_km: Double? = null,
    val apoapsis_km: Double? = null,
    val inclination_deg: Double? = null,
    val period_min: Double? = null,
    val lifespan_years: Double? = null,
    val epoch: String? = null,
    val mean_motion: Double? = null,
    val raan: Double? = null,
    val arg_of_pericenter: Double? = null,
    val mean_anomaly: Double? = null
)

@Serializable
data class Fairings(
    val reused: Boolean? = null,
    val recovery_attempt: Boolean? = null,
    val recovered: Boolean? = null,
    val ship: String? = null
)

@Serializable
data class Telemetry(
    val flight_club: String? = null
)

@Serializable
data class LaunchSite(
    val site_id: String? = null,
    val site_name: String? = null,
    val site_name_long: String? = null
)

@Serializable
data class LaunchFailureDetails(
    val time: Int? = null,
    val altitude: Int? = null,
    val reason: String? = null
)

@Serializable
data class Links(
    val mission_patch: String? = null,
    val mission_patch_small: String? = null,
    val reddit_campaign: String? = null,
    val reddit_launch: String? = null,
    val reddit_recovery: String? = null,
    val reddit_media: String? = null,
    val presskit: String? = null,
    val article_link: String? = null,
    val wikipedia: String? = null,
    val video_link: String? = null,
    val youtube_id: String? = null,
    val flickr_images: List<String>? = null
)

@Serializable
data class Timeline(
    val webcast_liftoff: Int? = null
)