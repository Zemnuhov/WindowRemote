package com.novsu.windowremoote.ui.scan_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.neurotech.core_ble_device_scan_api.BluetoothScanAPI
import com.neurotech.core_ble_device_scan_api.Device
import com.neurotech.core_bluetooth_comunication_api.BluetoothConnectionApi
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class ScanViewModel(
    private val bluetoothScan: BluetoothScanAPI,
    private val bluetoothConnection: BluetoothConnectionApi,
): ViewModel() {

    companion object {
        const val TAG = "ScanViewModel"
    }

    val devices = liveData{
        bluetoothScan.getDevicesFlow().collect{
            emit(it)
        }
    }

    val scanState = liveData{
        bluetoothScan.getScanState().collect{
            emit(it)
        }
    }

    val connectionState = liveData{
        bluetoothConnection.getConnectionStateFlow().collect{
            emit(it)
        }
    }


    fun startScan(){
        viewModelScope.launch {
            Log.i(TAG, "Start Scan")
            bluetoothScan.startScan()
        }
    }

    fun connectToDevice(mac: String){
        viewModelScope.launch {
            bluetoothConnection.connectionToPeripheral(mac)
        }
    }

    fun rememberDevice(device: Device){
        viewModelScope.launch{

        }
    }

    @Suppress("UNCHECKED_CAST")
    internal class Factory @Inject constructor (
        private val scanCore: Provider<BluetoothScanAPI>,
        private val connectionApi: Provider<BluetoothConnectionApi>
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == ScanViewModel::class.java)
            return ScanViewModel(scanCore.get(), connectionApi.get()) as T
        }
    }


}

