package com.neurotech.core_bluetooth_comunication_impl

import java.util.*

internal object ListUUID {

    val SERVICE_UUID: UUID = UUID.fromString("a37530b0-1a89-4a35-b52e-9f1bd5fd05d7")
    val configurationCharacteristicUUID: UUID = UUID.fromString("a37530b1-1a89-4a35-b52e-9f1bd5fd05d7")
    val ssidCharacteristicUUID: UUID = UUID.fromString("a37530b2-1a89-4a35-b52e-9f1bd5fd05d7")
    val passwordCharacteristicUUID: UUID = UUID.fromString("a37530b3-1a89-4a35-b52e-9f1bd5fd05d7")

    val STATE_SERVICE_UUID: UUID = UUID.fromString("6bd5ad50-0624-4c0a-9472-2b016cfe3570")
    val temperatureCharacteristicUUID: UUID = UUID.fromString("6bd5ad51-0624-4c0a-9472-2b016cfe3570")
    val switchCharacteristicUUID: UUID = UUID.fromString("6bd5ad52-0624-4c0a-9472-2b016cfe3570")
    val encoderPositionCharacteristicUUID: UUID = UUID.fromString("6bd5ad53-0624-4c0a-9472-2b016cfe3570")


    val COMMAND_SERVICE_UUID: UUID  = UUID.fromString("011cf590-6f83-42a7-9799-98536a472ac0")
    val openStateCommandCharacteristicUUID: UUID = UUID.fromString("011cf591-6f83-42a7-9799-98536a472ac0")
}