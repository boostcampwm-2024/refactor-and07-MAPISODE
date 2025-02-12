package com.boostcamp.mapisode.episode.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.boostcamp.mapisode.episode.aiEpisode.AiEpisodeRoute
import com.boostcamp.mapisode.navigation.MainRoute

fun NavController.navigateToEpisode(
	navOptions: NavOptions? = null,
) {
	navigate(MainRoute.AiEpisode, navOptions)
}

fun NavGraphBuilder.addEpisodeNavGraph(
	onPopBackToMain: () -> Unit,
) {
	composable<MainRoute.AiEpisode> { _ ->
		AiEpisodeRoute(
			navigateToMain = onPopBackToMain,
		)
	}
}
