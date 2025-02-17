package com.boostcamp.mapisode.episode.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.boostcamp.mapisode.episode.EpisodeViewModel
import com.boostcamp.mapisode.episode.screen.EpisodeContentRoute
import com.boostcamp.mapisode.episode.screen.EpisodeInfoRoute
import com.boostcamp.mapisode.episode.screen.EpisodePhotoRoute
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
	navController: NavController,
	onInfoPickClick: () -> Unit,
	onContentPickClick: () -> Unit,
	onBack: () -> Unit,
	onMainBack: () -> Unit,
) {
	composable<MainRoute.Episode> {
		EpisodePhotoRoute(
			onCompletePhotoPicker = onInfoPickClick,
			onBackClick = onBack,
			viewModel = hiltViewModel(),
		)
	}

	@Composable
	fun getParentViewModel(navController: NavController): EpisodeViewModel? {
		val parentEntry = navController.currentBackStackEntryAsState().value?.let {
			try {
				navController.getBackStackEntry(MainRoute.Episode)
			} catch (e: IllegalArgumentException) {
				null
			}
		}
		return parentEntry?.let { hiltViewModel(it) }
	}

	composable<NewEpisodeRoute.PickInfo> {
		getParentViewModel(navController)?.run {
			EpisodeInfoRoute(
				onCompleteInfoPick = onContentPickClick,
				onBackClick = onBack,
				viewModel = this,
			)
		}
	}

	composable<NewEpisodeRoute.WriteContent> {
		getParentViewModel(navController)?.run {
			EpisodeContentRoute(
				onCompleteContentClick = onMainBack,
				onBackClick = onBack,
				viewModel = this,
			)
		}
	}
}
