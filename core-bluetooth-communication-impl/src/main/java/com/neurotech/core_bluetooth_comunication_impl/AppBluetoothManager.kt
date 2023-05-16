package com.neurotech.core_bluetooth_comunication_impl

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.COMMAND_SERVICE_UUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.SERVICE_UUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.STATE_SERVICE_UUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.configurationCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.encoderPositionCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.openStateCommandCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.passwordCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.ssidCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.switchCharacteristicUUID
import com.neurotech.core_bluetooth_comunication_impl.ListUUID.temperatureCharacteristicUUID
import com.neurotech.utils.StressLogger.log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.asFlow
import no.nordicsemi.android.ble.ktx.state
import no.nordicsemi.android.ble.ktx.stateAsFlow
import no.nordicsemi.android.ble.ktx.suspend
import java.nio.ByteBuffer

@OptIn(ExperimentalCoroutinesApi::class)
class AppBluetoothManager(
    context: Context
) : BleManager(context) {

    private var configurationCharacteristic: BluetoothGattCharacteristic? = null
    private var ssidCharacteristic: BluetoothGattCharacteristic? = null
    private var passwordCharacteristic: BluetoothGattCharacteristic? = null

    private var encoderPositionCharacteristic: BluetoothGattCharacteristic? = null
    private var switchCharacteristic: BluetoothGattCharacteristic? = null
    private var temperatureCharacteristic: BluetoothGattCharacteristic? = null

    private var openStateCommandCharacteristic: BluetoothGattCharacteristic? = null

    @OptIn(DelicateCoroutinesApi::class)
    val scope = CoroutineScope(newSingleThreadContext("BleFlow"))

    private val _temperatureFlow = MutableSharedFlow<Int>()
    private val _switchFlow = MutableSharedFlow<Boolean>()
    private val _encoderFlow = MutableSharedFlow<Int>()

    val temperatureFlow: Flow<Int> get() = _temperatureFlow
    val switchFlow: Flow<Boolean> get() = _switchFlow
    val encoderFlow: Flow<Int> get() = _encoderFlow

    private val errorFlow: MutableStateFlow<Exception?> = MutableStateFlow(null)

    var isAutoConnect = true
    private var isLog = false


    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        val baseService = try {
            gatt.getService(SERVICE_UUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }
        val stateService = try {
            gatt.getService(STATE_SERVICE_UUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }
        val commandService = try {
            gatt.getService(COMMAND_SERVICE_UUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }
        var baseCharacteristicResult = false
        var stateCharacteristicResult = false
        var commandCharacteristicResult = false
        if (baseService != null && stateService != null && commandService != null) {
            try {
                baseCharacteristicResult = baseCharacteristicInit(baseService)
                stateCharacteristicResult = stateCharacteristicInit(stateService)
                commandCharacteristicResult = commandCharacteristicInit(commandService)
            } catch (e: Exception) {
                errorFlow.value = e
            }
        }
        return baseCharacteristicResult && stateCharacteristicResult && commandCharacteristicResult
    }


    override fun log(priority: Int, message: String) {
        if(isLog){
            log("$message. Priority $priority")
        }

        super.log(priority, message)
    }

    private fun baseCharacteristicInit(baseService: BluetoothGattService): Boolean {
        configurationCharacteristic = try {
            baseService.getCharacteristic(configurationCharacteristicUUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }
        ssidCharacteristic = try {
            baseService.getCharacteristic(ssidCharacteristicUUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }
        passwordCharacteristic = try {
            baseService.getCharacteristic(passwordCharacteristicUUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }
        return configurationCharacteristic != null && ssidCharacteristic != null && passwordCharacteristic != null
    }

    private fun commandCharacteristicInit(memoryService: BluetoothGattService): Boolean {
        openStateCommandCharacteristic = try {
            memoryService.getCharacteristic(openStateCommandCharacteristicUUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }

        return openStateCommandCharacteristic != null
    }

    private fun stateCharacteristicInit(stateService: BluetoothGattService): Boolean {
        temperatureCharacteristic = try {
            stateService.getCharacteristic(temperatureCharacteristicUUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }
        switchCharacteristic = try {
            stateService.getCharacteristic(switchCharacteristicUUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }
        encoderPositionCharacteristic = try {
            stateService.getCharacteristic(encoderPositionCharacteristicUUID)
        } catch (e: Exception) {
            errorFlow.value = e
            null
        }

        return temperatureCharacteristic != null &&
                switchCharacteristic != null &&
                encoderPositionCharacteristic != null
    }

    override fun initialize() {
        try {
            requestMtu(512).enqueue()
        } catch (e: Exception) {
            errorFlow.value = e
        }
    }

    override fun onServicesInvalidated() {
        configurationCharacteristic = null
        ssidCharacteristic = null
        passwordCharacteristic = null
        encoderPositionCharacteristic = null
        switchCharacteristic = null
        temperatureCharacteristic = null
        openStateCommandCharacteristic = null
    }


    init {
        scope.launch(Dispatchers.IO) {
            delay(500)
            stateAsFlow().collect {
                if (it.isReady) {
                    try {
                        observeNotification()
                    } catch (e: Exception) {
                        errorFlow.value = e
                    }
                }
            }
        }
        scope.launch(Dispatchers.IO) {
            errorFlow.collect{
                if(it != null){
                    log(it.message.toString())
                    cancel()
                }
            }
        }
    }

    suspend fun connectToDevice(device: BluetoothDevice) {
        isAutoConnect = true
        try {
            if (!state.isConnected) {
                connect(device)
                    .retry(4, 300)
                    .useAutoConnect(true)
                    .timeout(15_000)
                    .suspend()
            }
        } catch (e: Exception) {
            errorFlow.value = e
        }
    }


    private suspend fun observeNotification() {

        try {
            enableNotifications(temperatureCharacteristic).suspend()
            enableNotifications(switchCharacteristic).suspend()
            enableNotifications(encoderPositionCharacteristic).suspend()
        } catch (e: Exception) {
            errorFlow.value = e
        }

        scope.launch {
            try {
                setNotificationCallback(temperatureCharacteristic).asFlow()
                    .collect {
                        val bytes = it.value
                        if (bytes != null) {
                            val value = ByteBuffer.wrap(bytes).int
                            _temperatureFlow.emit(value)
                        }
                    }
            } catch (e: Exception) {
                errorFlow.value = e
            }
        }
        scope.launch {
            try {
                setNotificationCallback(switchCharacteristic).asFlow()
                    .collect {
                        val bytes = it.value
                        if (bytes != null) {
                            val value = ByteBuffer.wrap(bytes).int

                            _switchFlow.emit(if(value == 0) false else true)
                        }
                    }
            } catch (e: Exception) {
                errorFlow.value = e
            }
        }

        scope.launch {
            try {
                setNotificationCallback(encoderPositionCharacteristic).asFlow()
                    .collect {
                        val bytes = it.value
                        bytes?.let { b ->
                            _encoderFlow.emit(ByteBuffer.wrap(b.reversedArray()).int)
                        }
                    }
            } catch (e: Exception) {
                errorFlow.value = e
            }
        }
    }

    suspend fun writeSsid(ssid: String) {
        try {
            writeCharacteristic(
                ssidCharacteristic,
                ssid.toByteArray(),
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            ).suspend()
        } catch (e: Exception) {
            errorFlow.value = e
        }
    }

    suspend fun writePassword(password: String) {
        try {
            writeCharacteristic(
                passwordCharacteristic,
                password.toByteArray(),
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            ).suspend()
        } catch (e: Exception) {
            errorFlow.value = e
        }
    }

    suspend fun writeOpenState(openState: Int) {
        try {
            val byteValue = ByteBuffer.allocate(4).putInt(openState).array()
            byteValue.reverse()
            writeCharacteristic(
                openStateCommandCharacteristic,
                byteValue,
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            ).suspend()
        } catch (e: Exception) {
            errorFlow.value = e
        }
    }


}