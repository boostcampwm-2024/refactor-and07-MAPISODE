package com.boostcamp.mapisode.episode.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.boostcamp.mapisode.navigation.MainRoute
import com.boostcamp.mapisode.navigation.NewEpisodeRoute

fun NavController.navigateToEpisode(
	navOptions: NavOptions? = null,
) {
	navigate(MainRoute.AiEpisode, navOptions)
}

fun NavController.navigateToPickInfo(
	navOptions: NavOptions? = null,
) {
	navigate(NewEpisodeRoute.PickInfo, navOptions)
}

fun NavController.navigateToWriteContent(
	navOptions: NavOptions? = null,
) {
	navigate(NewEpisodeRoute.WriteContent, navOptions)
}

fun NavGraphBuilder.addEpisodeNavGraph(
	onPopBackToMain: () -> Unit,
	onBack: () -> Unit,
) {
	composable<MainRoute.AiEpisode> { _ ->
	}
	composable<NewEpisodeRoute.PickInfo> {
	}
	composable<NewEpisodeRoute.WriteContent> {
	}
}
