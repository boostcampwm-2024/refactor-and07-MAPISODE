package com.boostcamp.mapisode.episode.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.boostcamp.mapisode.designsystem.compose.MapisodeDivider
import com.boostcamp.mapisode.designsystem.compose.MapisodeText
import com.boostcamp.mapisode.designsystem.compose.TextAlignment
import com.boostcamp.mapisode.designsystem.compose.Thickness
import com.boostcamp.mapisode.designsystem.compose.button.MapisodeFilledButton
import com.boostcamp.mapisode.designsystem.theme.MapisodeTheme
import com.boostcamp.mapisode.episode.EpisodeViewModel
import com.boostcamp.mapisode.episode.state.EpisodeEffect
import com.boostcamp.mapisode.episode.state.EpisodeIntent
import com.boostcamp.mapisode.episode.state.EpisodeState
import com.boostcamp.mapisode.model.GroupModel
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapType
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.naver.maps.map.compose.rememberMarkerState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class, ExperimentalNaverMapApi::class)
@Composable
fun EpisodeInfoRoute(
	onCompleteInfoPick: () -> Unit,
	onBackClick: () -> Unit,
	viewModel: EpisodeViewModel,
) {
	val uiState = viewModel.state.collectAsStateWithLifecycle().value
	val context = LocalContext.current
	val cameraPositionState: CameraPositionState = rememberCameraPositionState {
		position = uiState.cameraPosition
	}
	val episodeMarkerState = rememberMarkerState()
	episodeMarkerState.position = cameraPositionState.position.target

	LaunchedEffect(Unit) {
		viewModel.effect.collect { effect ->
			when (effect) {
				is EpisodeEffect.NavigateToPreviousScreen -> onBackClick()
				is EpisodeEffect.NavigateToContentScreen -> onCompleteInfoPick()
				is EpisodeEffect.ShowToast -> {
					Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
				}
			}
		}
	}

	LaunchedEffect(cameraPositionState.position.target) {
		viewModel.sendIntent(EpisodeIntent.SetIsCameraMoving(true))
		snapshotFlow { cameraPositionState.position.target }
			.debounce(300L)
			.collectLatest { target ->
				viewModel.sendIntent(EpisodeIntent.SetEpisodeAddress(target))
				delay(100L)
				viewModel.sendIntent(EpisodeIntent.SetIsCameraMoving(false))
			}
	}

	EpisodeInfoScreen(
		uiState = uiState,
		onBackClick = { viewModel.sendIntent(EpisodeIntent.OnBackClick) },
		onCompleteInfoClick = { viewModel.sendIntent(EpisodeIntent.OnCompleteInfoClick) },
		onGroupClick = { groups -> viewModel.sendIntent(EpisodeIntent.OnGroupClick(groups)) },
		cameraPositionState = cameraPositionState,
		episodeMarkerState = episodeMarkerState,
	)
}

@Composable
fun EpisodeInfoScreen(
	uiState: EpisodeState,
	onBackClick: () -> Unit,
	onCompleteInfoClick: () -> Unit,
	onGroupClick: (List<GroupModel>) -> Unit,
	cameraPositionState: CameraPositionState,
	episodeMarkerState: MarkerState,
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
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(top = it.calculateTopPadding()),
			contentAlignment = Alignment.Center,
		) {
			Column(
				modifier = Modifier.fillMaxWidth(0.9f),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Spacer(modifier = Modifier.height(10.dp))

				MapisodeText(
					text = "그룹 선택",
					style = MapisodeTheme.typography.titleLarge,
				)

				Spacer(modifier = Modifier.height(10.dp))

				LazyRow(
					modifier = Modifier
						.fillMaxWidth()
						.clip(RoundedCornerShape(8.dp))
						.border(
							border = BorderStroke(
								width = 2.dp,
								color = MapisodeTheme.colorScheme.chipSelectedStroke,
							),
							shape = RoundedCornerShape(8.dp),
						),
					contentPadding = PaddingValues(20.dp),
					horizontalArrangement = Arrangement.spacedBy(
						12.dp,
						Alignment.CenterHorizontally,
					),
				) {
					val mine = uiState.groups.firstOrNull()
					val others = uiState.groups.drop(1)
					mine?.run {
						item {
							GroupCard(
								group = mine,
								isSelected = uiState.selectedGroups.contains(mine),
								addGroup = { selectedGroup ->
									onGroupClick(listOf(selectedGroup))
								},
								subtractGroup = { unselectedGroup ->
									onGroupClick(uiState.selectedGroups - unselectedGroup)
								},
							)
						}

						items(others.toList()) { group ->
							GroupCard(
								group = group,
								isSelected = uiState.selectedGroups.contains(group),
								addGroup = {
									onGroupClick(uiState.selectedGroups + group - mine)
								},
								subtractGroup = { onGroupClick(uiState.selectedGroups - group) },
							)
						}
					}
				}

				Spacer(modifier = Modifier.height(20.dp))

				MapisodeDivider(thickness = Thickness.Thin)

				Spacer(modifier = Modifier.height(20.dp))

				MapisodeText(
					text = "장소 선택",
					style = MapisodeTheme.typography.titleLarge,
				)

				Spacer(modifier = Modifier.height(10.dp))

				LocationSelection(
					uiState = uiState,
					cameraPositionState = cameraPositionState,
					episodeMarkerState = episodeMarkerState,
					onCompleteInfoClick = { onCompleteInfoClick() },
				)

				Spacer(modifier = Modifier.height(20.dp))
			}
		}
	}
}

@Composable
fun GroupCard(
	group: GroupModel,
	isSelected: Boolean,
	addGroup: (GroupModel) -> Unit,
	subtractGroup: (GroupModel) -> Unit,
) {
	var border by rememberSaveable { mutableIntStateOf(-1) }

	border = if (isSelected) 5 else 0
	val size = LocalConfiguration.current.screenWidthDp.dp / 4

	Column(
		modifier = Modifier
			.clickable {
				if (isSelected) {
					subtractGroup(group)
				} else {
					addGroup(group)
				}
			}
			.wrapContentSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		AsyncImage(
			model = group.imageUrl,
			contentDescription = group.name,
			modifier = Modifier
				.size(size)
				.aspectRatio(1f)
				.clip(RoundedCornerShape(8.dp))
				.border(
					border = BorderStroke(
						width = border.dp,
						color = MapisodeTheme.colorScheme.chipSelectedStroke,
					),
					shape = RoundedCornerShape(border.dp),
				),
			contentScale = ContentScale.Crop,
		)

		Spacer(modifier = Modifier.height(8.dp))

		MapisodeText(
			text = group.name,
			modifier = Modifier.width(size),
			textAlignment = TextAlignment.Center,
			maxLines = 2,
		)
	}
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun LocationSelection(
	uiState: EpisodeState,
	cameraPositionState: CameraPositionState,
	episodeMarkerState: MarkerState,
	onCompleteInfoClick: () -> Unit,
) {
	NaverMap(
		modifier = Modifier
			.fillMaxWidth()
			.widthIn(max = 360.dp)
			.aspectRatio(1.2f)
			.clip(RoundedCornerShape(8.dp))
			.border(
				border = BorderStroke(
					width = 1.dp,
					color = MapisodeTheme.colorScheme.chipSelectedStroke,
				),
				shape = RoundedCornerShape(8.dp),
			),
		cameraPositionState = cameraPositionState,
		properties = MapProperties(
			locationTrackingMode = LocationTrackingMode.NoFollow,
			isNightModeEnabled = isSystemInDarkTheme(),
			mapType = MapType.Navi,
		),
		uiSettings = MapUiSettings(
			isZoomControlEnabled = false,
			isLocationButtonEnabled = true,
			isLogoClickEnabled = false,
		),
		locationSource = rememberFusedLocationSource(),
	) {
		Marker(state = episodeMarkerState)
	}
	Box(
		modifier = Modifier.fillMaxHeight(),
		contentAlignment = Alignment.TopStart,
	) {
		Column(
			modifier = Modifier.padding(top = 12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
		) {
			if (uiState.isCameraMoving) {
				MapisodeText(
					text = "위치를 탐색중입니다",
					style = MapisodeTheme.typography.titleMedium.copy(
						fontWeight = FontWeight.Bold,
					),
				)
				MapisodeText(
					text = "탐색중",
					style = MapisodeTheme.typography.bodyMedium,
				)
			} else {
				MapisodeText(
					text = "이 장소로 할래요",
					style = MapisodeTheme.typography.titleMedium.copy(
						fontWeight = FontWeight.Bold,
					),
				)
				MapisodeText(
					text = uiState.episodeAddress,
					style = MapisodeTheme.typography.bodyMedium,
				)
			}
		}

		Column(
			modifier = Modifier.align(Alignment.BottomCenter),
		) {
			MapisodeFilledButton(
				modifier = Modifier
					.fillMaxWidth()
					.height(52.dp),
				onClick = {
					onCompleteInfoClick()
				},
				text = if (uiState.isCameraMoving) "위치 설정중" else "선택하기",
				enabled = uiState.isCameraMoving.not(),
				showRipple = true,
			)
			Spacer(modifier = Modifier.height(10.dp))
		}
	}
}
