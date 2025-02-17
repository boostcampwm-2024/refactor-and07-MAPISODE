package com.boostcamp.mapisode.episode.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun EpisodeContentScreen(
	onCompleteContentClick: () -> Unit,
	onBackClick: () -> Unit,
) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
	) {
		Column(
			modifier = Modifier.fillMaxSize().padding(it.calculateTopPadding()),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Button(
				onClick = onCompleteContentClick,
			) {
				Text(text = "onCompleteContentClick")
			}
		}
	}
}
