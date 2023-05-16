package com.neurotech.core_bluetooth_comunication_impl.implementation

import com.neurotech.core_bluetooth_comunication_api.BluetoothDataApi
import com.neurotech.core_bluetooth_comunication_impl.AppBluetoothManager
import com.neurotech.core_bluetooth_comunication_impl.di.BleCommunicationComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BluetoothData: BluetoothDataApi {
    @Inject
    lateinit var bleManager: AppBluetoothManager

    init {
        BleCommunicationComponent.get().inject(this)
    }

    override suspend fun getTemperatureFlow(): Flow<Int> = bleManager.temperatureFlow

    override suspend fun getSwitchFlow(): Flow<Boolean> = bleManager.switchFlow

    override suspend fun getEncoderFlow(): Flow<Int> = bleManager.encoderFlow
}