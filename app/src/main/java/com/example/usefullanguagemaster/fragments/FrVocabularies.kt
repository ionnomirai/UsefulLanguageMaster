package com.example.usefullanguagemaster.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usefullanguagemaster.R
import com.example.usefullanguagemaster.adapters.AdapterExpressionSets
import com.example.usefullanguagemaster.database.ExpressionSetsTextData
import com.example.usefullanguagemaster.databinding.FrVocabulariesBinding
import com.example.usefullanguagemaster.viewModels.ViewModelGeneral
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

    private var sharedPreferences: SharedPreferences? = null

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

        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.sp_settings_key), Context.MODE_PRIVATE
        )

        binding.apply {
            bAddNewVocabulary.setOnClickListener{
                findNavController().navigate(R.id.action_frVocabularies_to_frVocabulariesAdd)
            }

            val adapter = AdapterExpressionSets(getActiveRB())
            rvVocubalaries.apply {
                this.adapter = adapter
                this.layoutManager = LinearLayoutManager(requireContext())
            }

            // we monitor the database and receive data from there.
            viewLifecycleOwner.lifecycleScope.launch {
                vmGeneralF.getAllExpresionSets()
            }

            /* We monitor the data (dictionaries or vocabularies) received from the database.
            * If added, update adapter.*/
            viewLifecycleOwner.lifecycleScope.launch {
                vmGeneralF.allExpressionSets.collect{
                    adapter.submitList(it)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface ActiveRB{
        fun getId(): Int
        fun setId(id:Int)
    }

    /* Interface for saving the id of the active vocabulary. */
    private fun getActiveRB(): ActiveRB =  object : ActiveRB{
        override fun getId(): Int {return vmGeneralF.activeExpressionSetId}

        override fun setId(id: Int) {
            vmGeneralF.viewModelScope.launch {
                sharedPreferences?.edit(commit = true) {
                    putInt(getString(R.string.sp_vocabulary_id), id)
                }
                vmGeneralF.activeExpressionSetId = id
            }
        }
    }
}