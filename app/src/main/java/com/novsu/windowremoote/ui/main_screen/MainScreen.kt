package com.novsu.windowremoote.ui.main_screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.novsu.windowremoote.R
import com.novsu.windowremoote.databinding.FragmentMainBinding
import com.novsu.windowremoote.databinding.FragmentScanBinding
import com.novsu.windowremoote.di.AppComponent
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Provider

class MainScreen: Fragment(R.layout.fragment_main) {

    @Inject
    lateinit var factory: Lazy<MainViewModel.Factory>

    val viewModel: MainViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    override fun onAttach(context: Context) {
        super.onAttach(context)
        AppComponent.get().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(p0: SeekBar?) {
                if(p0 != null){
                    viewModel.writeOpenState(p0.progress)
                }
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.seekBarStateTextView.text = p0?.progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
        })
        viewModel.encoderState.observe(viewLifecycleOwner){
            binding.currentStateTextView.text = it.toString()
        }

        viewModel.switchState.observe(viewLifecycleOwner){
            binding.currentSwitchTextView.text = it
        }

        binding.button.setOnClickListener {
            if(binding.ssidEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()){
                viewModel.writeNetworkData(
                    binding.ssidEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
                binding.ssidEditText.text.clear()
                binding.passwordEditText.text.clear()
            }
        }
    }
}