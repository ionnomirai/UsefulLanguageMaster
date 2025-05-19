package com.example.usefullanguagemaster.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.usefullanguagemaster.R
import com.example.usefullanguagemaster.databinding.FrAddNewDataDetailedBinding

class FrAddNewDataDetailed: Fragment() {
    private var _binding: FrAddNewDataDetailedBinding? = null
    private val binding
        get(): FrAddNewDataDetailedBinding{
            return checkNotNull(_binding){
                "Cannot access binding because it is null. Is the view visible"
            }
        }

    private val typeOfElement = listOf("Word", "Short phrases", "Phrases")
    private val partsOfSpeech = listOf("none", "n", "v", "adj")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrAddNewDataDetailedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting up adapters.
        setSpinnerAdapters(typeOfElement, partsOfSpeech)
    }

    // fill and apply the spinner adapters (type of elements: word, phrase etc., or part of speech: n., v., adj., etc.)
    private fun setSpinnerAdapters(typeOfElement: List<String>, pos: List<String>){
        val adapterTypesWords = ArrayAdapter(requireContext(), R.layout.back_spinner_item, typeOfElement)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        with(binding.spType){
            adapter = adapterTypesWords
            setSelection(0)
            prompt = "Select type of the element"
        }

        val adapterPOS = ArrayAdapter(requireContext(), R.layout.back_spinner_item, pos)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        with(binding.spPOS){
            adapter = adapterPOS
            setSelection(0)
            prompt = "Select part of speech of the element"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}