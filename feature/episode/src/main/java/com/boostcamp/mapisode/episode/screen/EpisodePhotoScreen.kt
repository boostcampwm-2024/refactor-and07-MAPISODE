package com.boostcamp.mapisode.episode.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.boostcamp.mapisode.designsystem.theme.MapisodeTheme
import com.boostcamp.mapisode.episode.EpisodeViewModel
import com.boostcamp.mapisode.episode.state.EpisodeEffect
import com.boostcamp.mapisode.episode.state.EpisodeIntent
import com.boostcamp.mapisode.ui.photopicker.MapisodePhotoPicker

@Composable
fun EpisodePhotoRoute(
	onBackClick: () -> Unit,
	onCompletePhotoPicker: () -> Unit,
	viewModel: EpisodeViewModel,
) {
	LaunchedEffect(Unit) {
		viewModel.effect.collect {
			when (it) {
				is EpisodeEffect.NavigateToPreviousScreen -> onBackClick()
				is EpisodeEffect.NavigateToInfoScreen -> onCompletePhotoPicker()
			}
		}
	}

	EpisodePhotoScreen(
		onBackClick = { viewModel.sendIntent(EpisodeIntent.OnBackClick) },
		onSetImages = { images -> viewModel.sendIntent(EpisodeIntent.OnCompletePhotoPicker(images)) },
	)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EpisodePhotoScreen(
	onBackClick: () -> Unit,
	onSetImages: (List<String>) -> Unit,
) {
	Scaffold(
		modifier = Modifier,
		containerColor = MapisodeTheme.colorScheme.scaffoldBackground,
	) {
		Box(
			modifier = Modifier.fillMaxSize(),
		) {
			MapisodePhotoPicker(
				numOfPhoto = 4,
				onPhotoSelected = { photos ->
					val uris = photos.map { it.uri }
					onSetImages(uris)
				},
				onBackPressed = { onBackClick() },
				onPermissionDenied = { onBackClick() },
			)
		}
	}
}
