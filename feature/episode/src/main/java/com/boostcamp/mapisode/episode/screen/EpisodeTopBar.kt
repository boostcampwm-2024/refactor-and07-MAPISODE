package com.boostcamp.mapisode.episode.screen

import androidx.compose.runtime.Composable
import com.boostcamp.mapisode.designsystem.R
import com.boostcamp.mapisode.designsystem.compose.MapisodeIcon
import com.boostcamp.mapisode.designsystem.compose.MapisodeIconButton
import com.boostcamp.mapisode.designsystem.compose.topbar.TopAppBar

@Composable
fun EpisodeTopBar(
	title: String,
	onBackClick: () -> Unit,
) {
	TopAppBar(
		title = title,
		navigationIcon = {
			MapisodeIconButton(
				onClick = { onBackClick() },
			) {
				MapisodeIcon(
					id = R.drawable.ic_arrow_back_ios,
				)
			}
		},
	)
}
