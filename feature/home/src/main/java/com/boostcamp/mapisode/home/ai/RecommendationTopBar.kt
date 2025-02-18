package com.boostcamp.mapisode.home.ai

import androidx.compose.runtime.Composable
import com.boostcamp.mapisode.designsystem.compose.MapisodeIcon
import com.boostcamp.mapisode.designsystem.compose.MapisodeIconButton
import com.boostcamp.mapisode.designsystem.compose.topbar.TopAppBar

@Composable
internal fun AiEpisodeTopBar(
	title: String,
	onClickBack: () -> Unit,
	onClickClear: () -> Unit,
) {
	TopAppBar(
		title = title,
		navigationIcon = {
			MapisodeIconButton(
				onClick = {
					onClickBack()
				},
			) {
				MapisodeIcon(com.boostcamp.mapisode.designsystem.R.drawable.ic_arrow_back_ios)
			}
		},
		actions = {
			MapisodeIconButton(
				onClick = {
					onClickClear()
				},
			) {
				MapisodeIcon(com.boostcamp.mapisode.designsystem.R.drawable.ic_clear)
			}
		},
	)
}
