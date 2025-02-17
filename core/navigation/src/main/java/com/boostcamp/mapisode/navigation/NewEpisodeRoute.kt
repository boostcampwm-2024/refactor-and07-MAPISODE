package com.boostcamp.mapisode.navigation

import kotlinx.serialization.Serializable

sealed interface NewEpisodeRoute : Route {
	@Serializable
	data object PickInfo : NewEpisodeRoute

	@Serializable
	data object WriteContent : NewEpisodeRoute
}
