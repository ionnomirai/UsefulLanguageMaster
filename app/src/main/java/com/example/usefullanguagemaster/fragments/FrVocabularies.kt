package com.example.usefullanguagemaster.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usefullanguagemaster.R
import com.example.usefullanguagemaster.adapters.AdapterExpressionSets
import com.example.usefullanguagemaster.database.LanguagesLearning
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
    private val tag = "FrVocabulariesTag"

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

        viewLifecycleOwner.lifecycleScope.launch {
            vmGeneralF.getAllExpresionSets()
        }

        binding.apply {
            bAddNewVocabulary.setOnClickListener{
                findNavController().navigate(R.id.action_frVocabularies_to_frVocabulariesAdd)
            }

            val adapter = AdapterExpressionSets()
            rvVocubalaries.apply {
                this.adapter = adapter
                this.layoutManager = LinearLayoutManager(requireContext())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                vmGeneralF.allExpressionSets.collect{
                    adapter.submitList(it)
                }
            }
        }

        // test part
        viewLifecycleOwner.lifecycleScope.launch {
            vmGeneralF.getAllLanguagesLearning()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vmGeneralF.allLanguagesLearning.collect{
                it.forEach { item: LanguagesLearning  ->
                    Log.d(tag, "id: ${item.id} -- name: ${item.language}")
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}