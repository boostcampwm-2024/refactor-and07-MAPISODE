package com.boostcamp.mapisode.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.boostcamp.mapisode.episode.navigation.navigateToEpisodeContent
import com.boostcamp.mapisode.episode.navigation.navigateToEpisodeInfo
import com.boostcamp.mapisode.episode.navigation.popUpStackToMain
import com.boostcamp.mapisode.home.navigation.navigateEpisodeDetail
import com.boostcamp.mapisode.home.navigation.navigateEpisodeEdit
import com.boostcamp.mapisode.home.navigation.navigateEpisodeList
import com.boostcamp.mapisode.home.navigation.navigateStoryViewer
import com.boostcamp.mapisode.home.navigation.navigateToAiRecommendation
import com.boostcamp.mapisode.model.EpisodeLatLng
import com.boostcamp.mapisode.mygroup.navigation.navigateGroupCreation
import com.boostcamp.mapisode.mygroup.navigation.navigateGroupDetail
import com.boostcamp.mapisode.mygroup.navigation.navigateGroupEdit
import com.boostcamp.mapisode.mygroup.navigation.navigateGroupJoin
import com.boostcamp.mapisode.mypage.navigation.navigateToProfileEdit
import com.boostcamp.mapisode.navigation.MainRoute
import com.boostcamp.mapisode.navigation.Route

internal class MainNavigator(
	val navController: NavHostController,
) {
	private val currentDestination: NavDestination?
		@Composable get() = navController
			.currentBackStackEntryAsState().value?.destination

	val startDestination = Route.Auth

	val currentTab: MainNavTab?
		@Composable get() = MainNavTab.find { tab ->
			currentDestination?.hasRoute(tab::class) == true
		}

	fun navigate(tab: MainNavTab, latLng: EpisodeLatLng? = null) {
		val navOptions = navOptions {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}
			launchSingleTop = true
			restoreState = true
		}

		when (tab) {
			MainNavTab.HOME -> navController.navigate(MainNavTab.HOME.route, navOptions)
			MainNavTab.EPISODE -> navController.navigate(MainNavTab.EPISODE.route, navOptions)
			MainNavTab.GROUP -> navController.navigate(MainNavTab.GROUP.route, navOptions)
			MainNavTab.MYPAGE -> navController.navigate(MainNavTab.MYPAGE.route, navOptions)
		}
	}

	fun navigateToLogin() {
		navController.navigate(startDestination) {
			popUpTo(startDestination) { inclusive = true }
		}
	}

	fun navigateToMain() {
		navController.navigate(MainRoute.Home)
	}

	fun navigateToEpisodeInfo() {
		navController.navigateToEpisodeInfo()
	}

	fun navigateToEpisodeContent() {
		navController.navigateToEpisodeContent()
	}

	fun popUpStackToMain() {
		navController.popUpStackToMain()
	}

	fun navigateToEpisodeDetail(episodeId: String) {
		navController.navigateEpisodeDetail(episodeId)
	}

	fun navigateToEpisodeList(groupId: String) {
		navController.navigateEpisodeList(groupId)
	}

	fun navigateToEpisodeEdit(episodeId: String) {
		navController.navigateEpisodeEdit(episodeId)
	}

	fun getEpisodeBackStackEntry(): NavBackStackEntry =
		navController.getBackStackEntry(startDestination)

	fun popBackEpisodeToMain() {
		navController.popBackStack(Route.Auth, inclusive = false)
	}

	fun navigateGroupJoin() {
		navController.navigateGroupJoin()
	}

	fun navigateGroupDetail(groupId: String) {
		navController.navigateGroupDetail(groupId)
	}

	fun navigateGroupCreation() {
		navController.navigateGroupCreation()
	}

	fun navigateGroupEdit(groupId: String) {
		navController.navigateGroupEdit(groupId)
	}

	fun navigateToProfileEdit() {
		navController.navigateToProfileEdit()
	}

	fun navigateToStoryViewer() {
		navController.navigateStoryViewer()
	}

	fun navigateToAiRecommendation(episodes: List<String>) {
		navController.navigateToAiRecommendation(episodes)
	}

	private fun popBackStack() {
		navController.popBackStack()
	}

	fun popBackStackIfNotHome() {
		if (!isSameCurrentDestination<MainRoute.Home>()) {
			popBackStack()
		}
	}

	private inline fun <reified T : MainRoute> isSameCurrentDestination(): Boolean =
		navController.currentDestination?.hasRoute<T>() == true

	@Composable
	fun shouldShowBottomBar() = MainNavTab.contains {
		if (it == MainRoute.Episode) {
			false
		} else {
			currentDestination?.hasRoute(it::class) == true
		}
	}
}

@Composable
internal fun rememberMainNavigator(
	navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
	MainNavigator(navController)
}
