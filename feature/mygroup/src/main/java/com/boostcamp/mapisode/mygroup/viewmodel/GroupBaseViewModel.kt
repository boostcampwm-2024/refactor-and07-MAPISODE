package com.boostcamp.mapisode.mygroup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.mapisode.ui.base.SideEffect
import com.boostcamp.mapisode.ui.base.UiIntent
import com.boostcamp.mapisode.ui.base.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class GroupBaseViewModel<UI_INTENT : UiIntent, UI_STATE : UiState, UI_EFFECT : SideEffect>(
	initialState: UI_STATE,
) : ViewModel() {
	private val _state = MutableStateFlow(initialState)
	val state = _state.asStateFlow()

	private val _effect = Channel<SideEffect>()
	val effect = _effect.receiveAsFlow()

	protected val currentState: UI_STATE
		get() = _state.value

	private val _intent = MutableSharedFlow<UI_INTENT>()

	/**
	 * initialize the reducer with the intent argument
	 */
	init {
		viewModelScope.launch {
			reducer(_intent.asSharedFlow())
		}
	}

	/**
	 * Use intent to update the state and send the effect
	 */
	abstract suspend fun reducer(intent: SharedFlow<UI_INTENT>)

	fun sendIntent(intent: UI_INTENT) {
		viewModelScope.launch { _intent.emit(intent) }
	}

	protected fun sendState(reduce: UI_STATE.() -> UI_STATE) {
		_state.update { currentState.reduce() }
	}

	fun sendEffect(builder: () -> UI_EFFECT) {
		viewModelScope.launch { _effect.send(builder()) }
	}
}
