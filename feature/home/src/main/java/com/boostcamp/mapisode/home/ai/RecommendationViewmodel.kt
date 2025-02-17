package com.boostcamp.mapisode.home.ai

import com.boostcamp.mapisode.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecommendationViewmodel @Inject constructor(

) :
    BaseViewModel<RecommendationIntent, RecommendationState, RecommendationSideEffect>(
        RecommendationState(),
    ) {
    override fun onIntent(intent: RecommendationIntent) {

    }
}
