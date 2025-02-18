package com.boostcamp.mapisode.home.ai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boostcamp.mapisode.designsystem.compose.MapisodeScaffold
import com.boostcamp.mapisode.designsystem.compose.MapisodeText
import com.boostcamp.mapisode.designsystem.compose.button.MapisodeFilledButton
import com.boostcamp.mapisode.designsystem.theme.MapisodeTheme
import com.boostcamp.mapisode.home.common.HomeConstant.NUM_OF_COLUMNS
import com.boostcamp.mapisode.home.common.HomeConstant.options
import com.boostcamp.mapisode.home.common.OptionType
import com.boostcamp.mapisode.home.common.ResultEpisode
import com.boostcamp.mapisode.home.common.ResultViewType
import com.boostcamp.mapisode.home.component.EpisodeListCard
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import timber.log.Timber

@Composable
fun RecommendationRoute(
    viewModel: RecommendationViewmodel = hiltViewModel(),
    episodes: List<String>,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MapisodeScaffold(
        modifier = Modifier.fillMaxSize(),
        isStatusBarPaddingExist = true,
        isNavigationBarPaddingExist = true,
        topBar = {
            AiEpisodeTopBar(
                title = "AI 추천",
                onClickBack = onBackClick,
                onClickClear = { }
            )
        }
    ) {
        if (!uiState.isOptionSelected) {
            RecommendationChoiceScreen(
                modifier = Modifier.fillMaxSize(),
                onOptionClick = { optionId ->
                    viewModel.onIntent(RecommendationIntent.OptionClick(optionId))
                }
            )
        } else {
            RecommendationResultScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                resultViewType = uiState.resultViewType,
                episodes = listOf(
                    ResultEpisode(
                        id = "1",
                        owner = "강남역",
                        distance = "distance",
                        reason = "reason1",
                        thumbnail = "thumbnail",
                        coordinates = LatLng(37.504538, 127.025319)
                    ), ResultEpisode(
                        id = "2",
                        owner = "강남역",
                        distance = "distance",
                        reason = "reason12",
                        thumbnail = "thumbnail",
                        coordinates = LatLng(37.498123, 127.026378)
                    ), ResultEpisode(
                        id = "3",
                        owner = "경제",
                        distance = "distance",
                        reason = "reason13",
                        thumbnail = "thumbnail",
                        coordinates = LatLng(37.500667, 127.036155)
                    ), ResultEpisode(
                        id = "4",
                        owner = "코엑스",
                        distance = "distance",
                        reason = "reason4",
                        thumbnail = "thumbnail",
                        coordinates = LatLng(37.503632, 127.037445)
                    )
                ),
                showMapView = { viewModel.onIntent(RecommendationIntent.ShowMapType) },
                showListView = { viewModel.onIntent(RecommendationIntent.ShowListType) },
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
fun RecommendationChoiceScreen(
    modifier: Modifier = Modifier,
    onOptionClick: (OptionType) -> Unit = { }
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MapisodeText(
            text = "무엇을 추천해드릴까요?",
            style = MapisodeTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(NUM_OF_COLUMNS),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(options) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable(onClick = { onOptionClick(item.type) }),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.text,
                            tint = MapisodeTheme.colorScheme.chipSelectedStroke,
                            modifier = Modifier
                                .size(80.dp)
                                .aspectRatio(1f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MapisodeText(
                            text = item.text,
                            style = MapisodeTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RecommendationResultScreen(
    modifier: Modifier = Modifier,
    resultViewType: ResultViewType,
    episodes: List<ResultEpisode>,
    showMapView: () -> Unit,
    showListView: () -> Unit,
    onBackClick: () -> Unit,
) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MapisodeFilledButton(
            onClick = {
                Timber.e("ResultViewType: $resultViewType")
                when (resultViewType) {
                    ResultViewType.MAP_VIEW -> showListView()
                    ResultViewType.LIST_VIEW -> showMapView()
                }
            },
            text = if (resultViewType == ResultViewType.MAP_VIEW) "리스트 보기" else "지도 보기",
            textStyle = MapisodeTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 20.dp, top = 4.dp)
                .zIndex(1f),
        )

        when (resultViewType) {
            ResultViewType.MAP_VIEW -> {
                ResultMap(
                    episodes = episodes,
                    onMarkerClick = { }
                )
            }

            ResultViewType.LIST_VIEW -> {
                ResultList(
                    episodes = episodes,
                )
            }
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun ResultMap(
    episodes: List<ResultEpisode>,
    onMarkerClick: (ResultEpisode) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        val cameraPositionState = rememberCameraPositionState()

        val bounds = rememberSaveable {
            LatLngBounds.Builder().apply {
                episodes.forEach { include(it.coordinates) }
            }.build()
        }

        NaverMap(
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                isZoomControlEnabled = false,
                isLocationButtonEnabled = true,
                isLogoClickEnabled = false,
                isScaleBarEnabled = false,
                isCompassEnabled = false,
            ),
            onMapLoaded = {
                val padding = 100
                cameraPositionState.move(
                    CameraUpdate.fitBounds(
                        bounds,
                        padding
                    )
                )
            }
        ) {
            episodes.forEach { episode ->
                Marker(
                    state = MarkerState(episode.coordinates),
                    onClick = {
                        onMarkerClick(episode)
                        true
                    }
                )
            }
        }
    }
}

@Composable
fun ResultList(
    episodes: List<ResultEpisode>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 20.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MapisodeText(
                    text = "에피소드",
                    style = MapisodeTheme.typography.labelLarge,
                )
            }
        }
        items(
            items = episodes,
            key = { episode -> episode.id },
        ) { episode ->
            EpisodeListCard(
                imageUrl = episode.thumbnail,
                title = "",
                createdBy = episode.owner,
                content = episode.reason,
            )
        }
    }
}


@Composable
fun EpisodeCard() {

}
