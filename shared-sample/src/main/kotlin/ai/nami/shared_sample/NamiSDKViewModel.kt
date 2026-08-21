package ai.nami.shared_sample

import ai.nami.sdk.common.NamiLog
import ai.nami.sdk_ui_extensions.NamiSDUISDK
import ai.nami.sdk_ui_extensions.SDKEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


sealed interface NamiSDKUIEffect {
    data object Back : NamiSDKUIEffect
    data object NavigateToError : NamiSDKUIEffect
    data object NavigateToSuccessScreen : NamiSDKUIEffect
}

class NamiSDKViewModel(private val namiSDUISDK: NamiSDUISDK?) : ViewModel() {

    private val _effect = MutableSharedFlow<NamiSDKUIEffect>()
    val effect: SharedFlow<NamiSDKUIEffect> = _effect.asSharedFlow()

    init {
        NamiLog.e(tag = "debug-nav3", message = "NamiSDKViewModel init")
        viewModelScope.launch {
            namiSDUISDK?.sdkEvents?.collect {
                NamiLog.e(tag = "debug-nav3", message = "NamiSdKViewModel collect $it")
                when (it) {
                    is SDKEvent.OnExit -> {
                        _effect.emit(NamiSDKUIEffect.Back)
                    }

                    is SDKEvent.OnFinish -> {
                        _effect.emit(NamiSDKUIEffect.NavigateToSuccessScreen)
                    }
                }
            }
        }
    }


}