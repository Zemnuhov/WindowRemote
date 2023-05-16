package com.neurotech.core_bluetooth_comunication_api


interface BluetoothWriterApi {
    suspend fun writeSsid(ssid: String)
    suspend fun writePassword(password: String)
    suspend fun writeOpenState(openState: Int)
}