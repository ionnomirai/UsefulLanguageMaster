package com.example.usefullanguagemaster.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.usefullanguagemaster.R
import com.example.usefullanguagemaster.databinding.FrVocabulariesBinding
import com.example.usefullanguagemaster.viewModels.ViewModelGeneral
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FrVocabularies : Fragment() {
    private var _binding : FrVocabulariesBinding? = null
    private val binding
        get(): FrVocabulariesBinding{
            return checkNotNull(_binding){
                "Cannot access binding because it is null. Is the view visible"
            }
        }
    // view model with general information
    private val vmGeneralF: ViewModelGeneral by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrVocabulariesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}