package com.boostcamp.mapisode.home.ai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.boostcamp.mapisode.designsystem.compose.MapisodeText

val options: List<String> = (0..100).map { "Item $it" }

@Composable
fun RecommendationRoute(
    viewmodel: RecommendationViewmodel = hiltViewModel(),
    episodes: List<String>,
    onBackClick: () -> Unit,
) {
	RecommendationChoiceScreen()

}

@Composable
fun RecommendationChoiceScreen() {

    LazyVerticalGrid(GridCells.Fixed(2)) {
        items(options) { item ->
            Box {
                MapisodeText(
                    text = item,
                )
            }
        }
    }
}

@Composable
fun RecommendationResultScreen() {

}
