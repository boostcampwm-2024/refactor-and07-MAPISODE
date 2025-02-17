package com.boostcamp.mapisode.episode.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.boostcamp.mapisode.episode.screen.EpisodeContentScreen
import com.boostcamp.mapisode.episode.screen.EpisodeInfoScreen
import com.boostcamp.mapisode.episode.screen.EpisodePhotoScreen
import com.boostcamp.mapisode.navigation.MainRoute
import com.boostcamp.mapisode.navigation.NewEpisodeRoute

fun NavController.navigateToEpisodeInfo(
	navOptions: NavOptions? = null,
) {
	navigate(NewEpisodeRoute.PickInfo, navOptions)
}

fun NavController.navigateToEpisodeContent(
	navOptions: NavOptions? = null,
) {
	navigate(NewEpisodeRoute.WriteContent, navOptions)
}

fun NavController.popUpStackToMain(
	navOptions: NavOptions? = null,
) {
	navigate(MainRoute.Home) {
		popUpTo(MainRoute.Episode) {
			inclusive = true
		}
		anim {
			enter = 0
			exit = 0
			popEnter = 0
			popExit = 0
		}
	}
}

fun NavGraphBuilder.addEpisodeNavGraph(
	onInfoPickClick: () -> Unit,
	onContentPickClick: () -> Unit,
	onBack: () -> Unit,
	onMainBack: () -> Unit,
) {
	composable<MainRoute.Episode> {
		EpisodePhotoScreen(
			onCompletePhotoPicker = onInfoPickClick,
			onBackClick = onBack,
		)
	}

	composable<NewEpisodeRoute.PickInfo> {
		EpisodeInfoScreen(
			onCompleteInfoPick = onContentPickClick,
			onBackClick = onBack,
		)
	}

	composable<NewEpisodeRoute.WriteContent> {
		EpisodeContentScreen(
			onCompleteContentClick = onMainBack,
			onBackClick = onBack,
		)
	}
}
