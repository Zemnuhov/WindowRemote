package com.novsu.windowremoote.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class MainViewModel(
    private val bleData: BluetoothDataApi,
    private val bleWriter: BluetoothWriterApi
) : ViewModel() {

    val encoderState = liveData {
        bleData.getEncoderFlow().collect {
            emit(it)
        }
    }

    val switchState = liveData {
        bleData.getSwitchFlow().collect {
            emit(if(it) "Открыто" else "Закрыто")
        }
    }


    fun writeOpenState(openState: Int) {
        viewModelScope.launch {
            bleWriter.writeOpenState(openState)
        }
    }

    fun writeNetworkData(ssid: String, password: String) {
        viewModelScope.launch {
            bleWriter.writeSsid(ssid)
            bleWriter.writePassword(password)
        }
    }


    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val bleDataApi: Provider<BluetoothDataApi>,
        private val bleWriterApi: Provider<BluetoothWriterApi>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == MainViewModel::class.java)
            return MainViewModel(
                bleDataApi.get(),
                bleWriterApi.get()
            ) as T
        }
    }
}