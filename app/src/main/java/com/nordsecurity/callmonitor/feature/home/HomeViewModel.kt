package com.nordsecurity.callmonitor.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordsecurity.callmonitor.core.domain.CallResourceRepository
import com.nordsecurity.callmonitor.core.domain.IpAddressProvider
import com.nordsecurity.callmonitor.core.domain.ServerServiceController
import com.nordsecurity.callmonitor.core.domain.SyncManager
import com.nordsecurity.callmonitor.core.domain.UserDataRepository
import com.nordsecurity.callmonitor.core.model.CallResource
import com.nordsecurity.callmonitor.core.model.ServerStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    ipAddressProvider: IpAddressProvider,
    private val serverServiceController: ServerServiceController,
    private val callResourceRepository: CallResourceRepository,
    syncManager: SyncManager,
    userDataRepository: UserDataRepository
) : ViewModel() {

    private val _ipAddress: MutableStateFlow<String?> =
        MutableStateFlow(ipAddressProvider.getLocalIpAddress())
    val ipAddress: StateFlow<String?>
        get() = _ipAddress.asStateFlow()

    val serverStatus = userDataRepository.userData.map {
        it.serverStatus
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ServerStatus(startTime = "-", isRunning = false)
    )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )


    val feedState = callResourceRepository.getCallResources(useNew = false)
        .map(HomeFeedUiState::Success)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HomeFeedUiState.Loading
        )

    fun refreshCallsList() {
        viewModelScope.launch(Dispatchers.IO) {
            callResourceRepository.refreshCallResources()
        }
    }

    fun startServer() {
        serverServiceController.startService()

    }

    fun stopServer() {
        serverServiceController.stopService()
    }
}

/**
 * A sealed hierarchy describing the state of the feed of call resources.
 */
sealed interface HomeFeedUiState {
    data object Loading : HomeFeedUiState
    data class Success(
        val feed: List<CallResource>,
    ) : HomeFeedUiState
}
