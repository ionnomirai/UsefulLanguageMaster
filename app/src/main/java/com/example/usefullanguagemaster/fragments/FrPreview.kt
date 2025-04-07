package com.example.usefullanguagemaster.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.usefullanguagemaster.R
import com.example.usefullanguagemaster.databinding.FrPreviewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FrPreview : Fragment() {
    private var _binding: FrPreviewBinding? = null
    private val binding
        get(): FrPreviewBinding{
            return checkNotNull (_binding){
                "Cannot access binding because it is null. Is the view visible"
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrPreviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000L)
                findNavController().navigate(R.id.action_frPreview_to_frMain)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}