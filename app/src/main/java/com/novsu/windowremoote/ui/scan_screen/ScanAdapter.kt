package com.novsu.windowremoote.ui.scan_screen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.neurotech.core_ble_device_scan_api.Device
import com.novsu.windowremoote.databinding.ItemDeviceCardBinding

internal class ScanAdapter: ListAdapter<Device, ScanAdapter.ScanViewHolder>(DeviceItemCallBack()) {

    companion object{
        private const val TAG = "ScanAdapter"
    }

    interface ClickItemDevice{
        fun clickItem(device: Device)
    }

    var callBack: ClickItemDevice? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        return ScanViewHolder(
            ItemDeviceCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    internal inner class ScanViewHolder(private val binding: ItemDeviceCardBinding): ViewHolder(binding.root){
        fun bind(device: Device){
            binding.nameDevice.text = device.name
            binding.macDevice.text = device.mac
            binding.root.setOnClickListener {
                Log.i(TAG,"Selected device Name: ${device.name} Mac: ${device.mac}")
                val increaseAnimationX = ObjectAnimator.ofFloat(it, "scaleX", 0.8F)
                val increaseAnimationY = ObjectAnimator.ofFloat(it, "scaleY", 0.8F)
                val decreaseAnimationX = ObjectAnimator.ofFloat(it, "scaleX", 1F)
                val decreaseAnimationY = ObjectAnimator.ofFloat(it, "scaleY", 1F)
                AnimatorSet().let { animatorSet ->
                    animatorSet.play(increaseAnimationX).with(increaseAnimationY)
                    animatorSet.play(decreaseAnimationX).with(decreaseAnimationY).after(increaseAnimationX)
                    animatorSet.duration = 300
                    animatorSet.interpolator = DecelerateInterpolator()
                    animatorSet.start()
                }
                callBack?.clickItem(device)
            }
        }
    }
}

class DeviceItemCallBack: DiffUtil.ItemCallback<Device>(){
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return true
    }

}