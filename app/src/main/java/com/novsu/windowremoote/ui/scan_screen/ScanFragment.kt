package com.novsu.windowremoote.ui.scan_screen

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.neurotech.core_ble_device_scan_api.Device
import com.neurotech.core_bluetooth_comunication_api.ConnectionState
import com.novsu.windowremoote.R
import com.novsu.windowremoote.databinding.FragmentScanBinding
import com.novsu.windowremoote.di.AppComponent
import dagger.Lazy
import javax.inject.Inject

class ScanFragment: Fragment(R.layout.fragment_scan), ScanAdapter.ClickItemDevice {

    @Inject
    internal lateinit var factory: Lazy<ScanViewModel.Factory>

    private val viewModel: ScanViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private var scanAdapter: ScanAdapter? = null

    private val bluetoothAdapter by lazy {
        (requireContext().getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
    private val location by lazy {
        (requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AppComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewList.layoutManager = LinearLayoutManager(context)
        scanAdapter = ScanAdapter()
        scanAdapter?.callBack = this
        binding.recyclerViewList.adapter = scanAdapter
        requestPermissions()
        setObservers()
    }

    private fun setObservers(){
        viewModel.devices.observe(viewLifecycleOwner){ devices ->
            scanAdapter?.submitList(devices.list)
        }
        binding.refreshListDevice.setOnRefreshListener {
            viewModel.startScan()
        }

        viewModel.scanState.observe(viewLifecycleOwner){
            binding.refreshListDevice.isRefreshing = it
        }

        viewModel.connectionState.observe(viewLifecycleOwner){
            when(it){
                ConnectionState.CONNECTED -> {
                    findNavController().navigate(R.id.action_scanFragment_to_mainScreen)
                }
                ConnectionState.CONNECTING -> binding.connectProgress.isVisible = true
                else -> binding.connectProgress.isVisible = false
            }
        }

    }

    private fun requestPermissions() {
        val permissionsList = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionsList.add(Manifest.permission.BLUETOOTH)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_DENIED
            ) {
                permissionsList.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_DENIED
            ) {
                permissionsList.add(Manifest.permission.BLUETOOTH_SCAN)
            }
        }

        if(permissionsList.isNotEmpty()){
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsList.toTypedArray(),
                1
            )
        }


        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                if(it.resultCode != Activity.RESULT_OK){
                    Toast.makeText(requireContext(), "Блютуз не включен!", Toast.LENGTH_SHORT).show()
                }
            }
            resultLauncher.launch(enableBtIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if(!location.isLocationEnabled){
                Toast.makeText(requireContext(), "Требуется включить местоположение", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun clickItem(device: Device) {
        viewModel.connectToDevice(device.mac)
        viewModel.connectionState.observe(viewLifecycleOwner){
            if(it == ConnectionState.CONNECTED){
                viewModel.rememberDevice(device)
            }
        }
    }


}