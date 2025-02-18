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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.boostcamp.mapisode.designsystem.compose.MapisodeScaffold
import com.boostcamp.mapisode.designsystem.compose.MapisodeText
import com.boostcamp.mapisode.designsystem.compose.button.MapisodeFilledButton
import com.boostcamp.mapisode.designsystem.theme.MapisodeTheme
import com.boostcamp.mapisode.home.common.HomeConstant.NUM_OF_COLUMNS
import com.boostcamp.mapisode.home.common.HomeConstant.options
import com.boostcamp.mapisode.home.common.ResultEpisode
import com.boostcamp.mapisode.home.common.ResultType
import com.boostcamp.mapisode.home.component.EpisodeListCard
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState

@Composable
fun RecommendationRoute(
    viewmodel: RecommendationViewmodel = hiltViewModel(),
    episodes: List<String>,
    onBackClick: () -> Unit,
) {
    MapisodeScaffold(
        modifier = Modifier.fillMaxSize(),
        isStatusBarPaddingExist = true,
        isNavigationBarPaddingExist = true,
        topBar = {
            //  구분 해야...
            AiEpisodeTopBar(
                title = "AI 추천",
                onClickBack = onBackClick,
                onClickClear = { }
            )
        }
    ) {
        RecommendationResultScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            resultType = ResultType.LIST_VIEW,
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
            showMapView = { },
            showListView = { },
            onBackClick = onBackClick
        )

    }
}

@Composable
fun RecommendationChoiceScreen(
    modifier: Modifier = Modifier,
    onOptionClick: (String) -> Unit = { }
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
                        .clickable(onClick = { onOptionClick(item.prompt) }),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = item.image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .aspectRatio(1f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        MapisodeText(
                            text = item.text,
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
    resultType: ResultType,
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
                when (resultType) {
                    ResultType.MAP_VIEW -> showListView()
                    ResultType.LIST_VIEW -> showMapView()
                }
            },
            text = if (resultType == ResultType.MAP_VIEW) "리스트 보기" else "지도 보기",
            textStyle = MapisodeTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 20.dp, top = 4.dp),
        )

        when (resultType) {
            ResultType.MAP_VIEW -> {
                ResultMap(
                    episodes = episodes,
                    onMarkerClick = { }
                )
            }

            ResultType.LIST_VIEW -> {
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
