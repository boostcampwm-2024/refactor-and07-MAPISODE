package com.boostcamp.mapisode.episode.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boostcamp.mapisode.designsystem.theme.MapisodeTheme
import com.boostcamp.mapisode.episode.EpisodeViewModel

@Composable
fun EpisodeInfoRoute(
	onCompleteInfoPick: () -> Unit,
	onBackClick: () -> Unit,
	viewModel: EpisodeViewModel,
) {
	val uiState = viewModel.state.collectAsStateWithLifecycle()

	EpisodeInfoScreen(
		onCompleteInfoPick = onCompleteInfoPick,
		onBackClick = onBackClick,
	)
}

@Composable
fun EpisodeInfoScreen(
	onCompleteInfoPick: () -> Unit,
	onBackClick: () -> Unit,
) {
	Scaffold(
		modifier = Modifier
			.fillMaxSize()
			.padding(
				top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
				bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
			),
		topBar = { EpisodeTopBar(title = "위치 및 그룹 선택", onBackClick = onBackClick) },
		containerColor = MapisodeTheme.colorScheme.scaffoldBackground,
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(it.calculateTopPadding()),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Button(
				onClick = onCompleteInfoPick,
			) {
				Text(text = "onCompleteInfoPick")
			}
		}
	}
}
