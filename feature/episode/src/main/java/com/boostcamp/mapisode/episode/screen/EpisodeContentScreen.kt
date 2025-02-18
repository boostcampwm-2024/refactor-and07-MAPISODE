package com.boostcamp.mapisode.episode.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.boostcamp.mapisode.designsystem.R
import com.boostcamp.mapisode.designsystem.compose.MapisodeCircularLoadingIndicator
import com.boostcamp.mapisode.designsystem.compose.MapisodeIconButton
import com.boostcamp.mapisode.designsystem.compose.MapisodeText
import com.boostcamp.mapisode.designsystem.compose.MapisodeTextField
import com.boostcamp.mapisode.designsystem.compose.button.MapisodeFilledButton
import com.boostcamp.mapisode.designsystem.compose.button.MapisodeOutlinedButton
import com.boostcamp.mapisode.designsystem.theme.MapisodeTheme
import com.boostcamp.mapisode.episode.EpisodeViewModel
import com.boostcamp.mapisode.episode.state.EpisodeEffect
import com.boostcamp.mapisode.episode.state.EpisodeIntent
import com.boostcamp.mapisode.episode.state.EpisodeState

@Composable
fun EpisodeContentRoute(
	onCompleteContentClick: () -> Unit,
	onBackClick: () -> Unit,
	viewModel: EpisodeViewModel,
) {
	val uiState = viewModel.state.collectAsStateWithLifecycle().value
	val context = LocalContext.current

	LaunchedEffect(Unit) {
		viewModel.effect.collect {
			when (it) {
				is EpisodeEffect.NavigateToPreviousScreen -> onBackClick()
				is EpisodeEffect.NavigateBackToHomeScreen -> onCompleteContentClick()
			}
		}
	}

	EpisodeContentScreen(
		uiState = uiState,
		context = context,
		onUserInputChange = { viewModel.sendIntent(EpisodeIntent.OnUserInputChange(it)) },
		onGenerateLLMClick = { viewModel.sendIntent(EpisodeIntent.OnGenerateLLMClick) },
		onSelectEpisodeClick = { generatedEpisode ->
			viewModel.sendIntent(EpisodeIntent.OnSelectEpisodeClick(generatedEpisode))
		},
		onSelfTypedEpisodeChange = { viewModel.sendIntent(EpisodeIntent.OnSelfTypedEpisodeChange(it)) },
		onCompleteInfoPick = { viewModel.sendIntent(EpisodeIntent.OnCompleteInfoPick) },
		onBackClick = { viewModel.sendIntent(EpisodeIntent.OnBackClick) },
	)

	if (uiState.isLoading) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(
					color = MapisodeTheme.colorScheme.scrim,
				)
				.clickable(
					onClick = {},
					indication = null,
					interactionSource = null,
				),
			contentAlignment = Alignment.Center,
		) {
			MapisodeCircularLoadingIndicator()
		}
	}
}

@Composable
fun EpisodeContentScreen(
	uiState: EpisodeState,
	context: Context,
	onUserInputChange: (String) -> Unit,
	onGenerateLLMClick: () -> Unit,
	onSelectEpisodeClick: (String) -> Unit,
	onSelfTypedEpisodeChange: (String) -> Unit,
	onCompleteInfoPick: () -> Unit,
	onBackClick: () -> Unit,
) {
	var valueChanged by remember { mutableStateOf(uiState.userInput) }

	LaunchedEffect(uiState.userInput) {
		valueChanged = uiState.userInput
	}

	Scaffold(
		modifier = Modifier
			.fillMaxSize()
			.padding(
				top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
				bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
			),
		topBar = { EpisodeTopBar(title = "내용 입력", onBackClick = onBackClick) },
		containerColor = MapisodeTheme.colorScheme.scaffoldBackground,
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(top = it.calculateTopPadding()),
			contentAlignment = Alignment.TopCenter,
		) {
			Column(
				modifier = Modifier.fillMaxWidth(0.9f),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Top,
			) {
				Box(
					modifier = Modifier.fillMaxWidth(),
					contentAlignment = Alignment.Center,
				) {
					Spacer(modifier = Modifier.height(16.dp))

					Row(
						modifier = Modifier
							.wrapContentWidth()
							.horizontalScroll(rememberScrollState()),
						horizontalArrangement = Arrangement.spacedBy(10.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						uiState.imageUrls.forEach { imageUrl ->
							AsyncImage(
								model = ImageRequest.Builder(context)
									.data(imageUrl)
									.crossfade(true)
									.build(),
								contentDescription = "애피소드 이미지",
								modifier = Modifier
									.size(140.dp)
									.clip(RoundedCornerShape(16.dp)),
								contentScale = ContentScale.Crop,
							)
						}
					}
				}

				Spacer(modifier = Modifier.height(16.dp))

				MapisodeTextField(
					value = valueChanged,
					onValueChange = { text ->
						valueChanged = text
					},
					modifier = Modifier.aspectRatio(1.5f),
					placeholder = "경험을 자유롭게 적어주세요.\nAI 추천을 받기 위한 내용을 입력하시면 더 정확한 추천을 받을 수 있어요.",
				)

				Spacer(modifier = Modifier.height(16.dp))

				MapisodeIconButton(
					onClick = onGenerateLLMClick,
					modifier = Modifier
						.width(320.dp)
						.height(40.dp)
						.clip(
							RoundedCornerShape(8.dp),
						),
					backgroundColor = MapisodeTheme.colorScheme.filledButtonEnableBackground,
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(8.dp),
					) {
						Image(
							painter = painterResource(id = R.drawable.ic_generative_ai_star),
							contentDescription = "Generate",
							modifier = Modifier.size(16.dp),
						)
						MapisodeText(
							text = "AI 추천받기",
							style = MapisodeTheme.typography.labelLarge,
							color = MapisodeTheme.colorScheme.filledButtonContent,
						)
					}
				}

				Spacer(modifier = Modifier.height(16.dp))

				if (uiState.generatedEpisodes.isNotEmpty()) {
					MapisodeText(
						text = "AI 추천 내용",
						modifier = Modifier.fillMaxWidth(),
						style = MapisodeTheme.typography.labelLarge,
					)

					Spacer(modifier = Modifier.height(4.dp))

					repeat(maxOf(3, uiState.generatedEpisodes.size)) { index ->
						val generatedEpisode = uiState.generatedEpisodes.getOrNull(index) ?: ""
						MapisodeOutlinedButton(
							text = generatedEpisode,
							onClick = {
								if (generatedEpisode.isNotBlank()) {
									valueChanged += " $generatedEpisode"
								}
							},
							borderColor = MapisodeTheme.colorScheme.chipUnselectedStroke,
							contentColor = MapisodeTheme.colorScheme.chipUnselectedStroke,
						)
						Spacer(modifier = Modifier.height(8.dp))
					}
				}
			}

			Column(
				modifier = Modifier.align(Alignment.BottomCenter),
			) {
				MapisodeFilledButton(
					modifier = Modifier
						.fillMaxWidth(0.9f)
						.widthIn(max = 360.dp)
						.height(52.dp),
					onClick = {
						onCompleteInfoPick()
					},
					text = if (uiState.isEpisodeSelected) "완료" else "내용을 입력해주세요",
					enabled = uiState.isEpisodeSelected,
					showRipple = true,
				)
				Spacer(modifier = Modifier.height(10.dp))
			}
		}
	}
}
