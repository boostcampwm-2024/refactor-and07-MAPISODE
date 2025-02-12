package com.boostcamp.mapisode.episode.aiEpisode

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.boostcamp.mapisode.designsystem.compose.MapisodeDivider
import com.boostcamp.mapisode.designsystem.compose.MapisodeScaffold
import com.boostcamp.mapisode.designsystem.compose.MapisodeText
import com.boostcamp.mapisode.designsystem.compose.MapisodeTextField
import com.boostcamp.mapisode.designsystem.compose.TextAlignment
import com.boostcamp.mapisode.designsystem.compose.Thickness
import com.boostcamp.mapisode.designsystem.compose.button.MapisodeFilledButton
import com.boostcamp.mapisode.designsystem.theme.MapisodeTheme
import com.boostcamp.mapisode.ui.photopicker.MapisodePhotoPicker

@Composable
fun AiEpisodeRoute(
	navigateToMain: () -> Unit,
	viewModel: AiEpisodeViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		viewModel.onIntent(AiEpisodeIntent.LoadMyGroups)
	}

	LaunchedEffect(Unit) {
		viewModel.sideEffect.collect { sideEffect ->
			when (sideEffect) {
				is AiEpisodeSideEffect.NavigateToHome -> navigateToMain()
				is AiEpisodeSideEffect.ShowToast -> Unit
			}
		}
	}

	if (uiState.showPhotoPicker) {
		MapisodePhotoPicker(
			numOfPhoto = 4,
			onPhotoSelected = { photos ->
				val uris = photos.map { it.uri }
				viewModel.onIntent(AiEpisodeIntent.SetImages(uris))
				viewModel.onIntent(AiEpisodeIntent.HidePhotoPicker)
			},
			onBackPressed = {
				viewModel.onIntent(AiEpisodeIntent.BackToHome)
			},
			onPermissionDenied = {

				viewModel.onIntent(AiEpisodeIntent.BackToHome)
			},
		)
	} else {
		AiEpisodeScreen(
			myGroups = uiState.myGroups,
			selectedGroups = uiState.selectedGroups,
			addGroup = { viewModel.onIntent(AiEpisodeIntent.AddGroup(it)) },
			subtractGroup = { viewModel.onIntent(AiEpisodeIntent.SubtractGroup(it)) },
			addAllGroup = { viewModel.onIntent(AiEpisodeIntent.AddAllGroup) },
			clearGroup = { viewModel.onIntent(AiEpisodeIntent.ClearGroup) },
			aiText = uiState.aiText,
			onAiTextChange = { viewModel.onIntent(AiEpisodeIntent.SetAiText(it)) },
			onBackClicked = { viewModel.onIntent(AiEpisodeIntent.ShowPhotoPicker) },
			onBackToHome = { viewModel.onIntent(AiEpisodeIntent.BackToHome) },
			submitAiEpisode = { viewModel.onIntent(AiEpisodeIntent.SubmitAiEpisode) },
		)
	}
}

@Composable
internal fun AiEpisodeScreen(
	myGroups: List<GroupInfo>,
	selectedGroups: List<GroupInfo>,
	addGroup: (GroupInfo) -> Unit,
	subtractGroup: (GroupInfo) -> Unit,
	addAllGroup: () -> Unit,
	clearGroup: () -> Unit,
	aiText: String,
	onAiTextChange: (String) -> Unit,
	onBackClicked: () -> Unit,
	onBackToHome: () -> Unit,
	submitAiEpisode: () -> Unit,
) {
	MapisodeScaffold(
		isNavigationBarPaddingExist = true,
		topBar = {
			AiEpisodeTopBar(
				onClickBack = onBackClicked,
				onClickClear = onBackToHome,
			)
		},
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(it),
			contentAlignment = Alignment.Center,
		) {
			Column(
				modifier = Modifier.fillMaxWidth(0.9f),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Spacer(modifier = Modifier.weight(3f))

				MapisodeText(
					text = "어디에 공유하시겠습니까?",
					style = MapisodeTheme.typography.titleLarge,
				)

				Spacer(modifier = Modifier.height(28.dp))

				LazyRow(
					modifier = Modifier
						.fillMaxWidth()
						.clip(RoundedCornerShape(8.dp))
						.border(
							border = BorderStroke(
								width = 4.dp,
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
					val mine = myGroups.first()
					val others = myGroups.drop(1)
					item() {
						GroupCard(
							group = mine,
							isSelected = selectedGroups.contains(mine),
							addGroup = {
								clearGroup()
								addGroup(it)
							},
							subtractGroup = subtractGroup,
						)
					}

					items(others) { group ->
						GroupCard(
							group = group,
							isSelected = selectedGroups.contains(group),
							addGroup = {
								addGroup(it)
								subtractGroup(mine)
							},
							subtractGroup = subtractGroup,
						)
					}
				}

				Spacer(modifier = Modifier.weight(2f))

				MapisodeDivider(thickness = Thickness.Thin)

				Spacer(modifier = Modifier.weight(2f))

				MapisodeText(
					text = "무슨 일이 있었나요?",
					style = MapisodeTheme.typography.titleLarge,
				)

				Spacer(modifier = Modifier.height(28.dp))

				MapisodeTextField(
					value = aiText,
					onValueChange = onAiTextChange,
				)

				Spacer(modifier = Modifier.weight(3f))

				MapisodeFilledButton(
					text = "완료",
					onClick = submitAiEpisode,
				)

				Spacer(modifier = Modifier.weight(1f))
			}
		}
	}
}

@Composable
fun GroupCard(
	group: GroupInfo,
	isSelected: Boolean,
	addGroup: (GroupInfo) -> Unit,
	subtractGroup: (GroupInfo) -> Unit,
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
			model = group.imageUri,
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
