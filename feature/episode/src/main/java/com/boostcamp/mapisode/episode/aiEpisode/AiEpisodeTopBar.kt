package com.boostcamp.mapisode.episode.aiEpisode

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.boostcamp.mapisode.designsystem.compose.MapisodeIcon
import com.boostcamp.mapisode.designsystem.compose.MapisodeIconButton
import com.boostcamp.mapisode.designsystem.compose.topbar.TopAppBar
import com.boostcamp.mapisode.episode.R

@Composable
internal fun AiEpisodeTopBar(
	onClickBack: () -> Unit,
	onClickClear: () -> Unit,
) {
	TopAppBar(
		title = stringResource(R.string.new_episode_menu_create_episode),
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
