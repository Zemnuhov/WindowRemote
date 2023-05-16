package com.neurotech.core_bluetooth_comunication_api

import kotlinx.coroutines.flow.Flow

interface BluetoothDataApi {
    suspend fun getTemperatureFlow(): Flow<Int>
    suspend fun getSwitchFlow(): Flow<Boolean>
    suspend fun getEncoderFlow(): Flow<Int>
}