package com.neurotech.core_bluetooth_comunication_impl.implementation

import com.neurotech.core_bluetooth_comunication_api.BluetoothWriterApi
import com.neurotech.core_bluetooth_comunication_impl.AppBluetoothManager
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationComponent
import javax.inject.Inject

class BluetoothWriter: BluetoothWriterApi {

    @Inject
    lateinit var bleManager: AppBluetoothManager

    init {
        BleCommunicationComponent.get().inject(this)
    }

    override suspend fun writeSsid(ssid: String) {
        bleManager.writeSsid(ssid)
    }

    override suspend fun writePassword(password: String) {
        bleManager.writePassword(password)
    }

    override suspend fun writeOpenState(openState: Int) {
        bleManager.writeOpenState(openState)
    }
}