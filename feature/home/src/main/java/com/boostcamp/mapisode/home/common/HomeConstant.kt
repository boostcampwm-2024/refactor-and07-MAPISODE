package com.boostcamp.mapisode.home.common

import com.boostcamp.mapisode.designsystem.R
import com.boostcamp.mapisode.model.GroupModel
import kotlinx.collections.immutable.persistentListOf
import java.util.Date

object HomeConstant {
	const val DEFAULT_ZOOM = 16.0
	val tempGroupList = persistentListOf(
		GroupModel(
			id = "1",
			adminUser = "",
			name = "그룹1",
			description = "그룹1 설명",
			imageUrl = "https://avatars.githubusercontent.com/u/98825364?v=4?s=100",
			createdAt = Date(),
			members = emptyList(),
		),
		GroupModel(
			id = "2",
			adminUser = "",
			name = "그룹2",
			description = "그룹2 설명",
			imageUrl = "https://avatars.githubusercontent.com/u/98825364?v=4?s=100",
			createdAt = Date(),
			members = emptyList(),
		),
		GroupModel(
			id = "3",
			adminUser = "",
			name = "그룹3",
			description = "그룹3 설명",
			imageUrl = "https://avatars.githubusercontent.com/u/98825364?v=4?s=100",
			createdAt = Date(),
			members = emptyList(),
		),
	)
	const val EXTRA_RANGE = 0.01 // 약 1 ~ 1.5km
	const val MOCK_IMAGE_URL =
		"https://github.com/user-attachments/assets/411506ef-5fbf-4c6b-b68d-d00343a0b50e"
	const val MAX_NUM_OF_PHOTOS = 4
	const val NUM_OF_COLUMNS = 2
	val options = persistentListOf(
		AiOption(
			type = OptionType.RESTAURANT,
			icon = R.drawable.ic_restaurant,
			text = "식당!",
			prompt = "Prompt 1",
		),
		AiOption(
			type = OptionType.CAFE,
			icon = R.drawable.ic_cafe,
			text = "카페!",
			prompt = "Prompt 2",
		),
		AiOption(
			type = OptionType.CALM,
			icon = R.drawable.ic_calm,
			text = "차분한",
			prompt = "Prompt 3",
		),
		AiOption(
			type = OptionType.MEETING,
			icon = R.drawable.ic_meeting,
			text = "모임하기 좋은",
			prompt = "Prompt 4",
		),
		AiOption(
			type = OptionType.WITH_PET,
			icon = R.drawable.ic_pet,
			text = "애완동물과",
			prompt = "Prompt 5",
		),
		AiOption(
			type = OptionType.WALKING,
			icon = R.drawable.ic_walk,
			text = "산책하기 좋은",
			prompt = "Prompt 6",
		),
	)
}
