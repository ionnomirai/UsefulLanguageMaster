package com.example.usefullanguagemaster.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.usefullanguagemaster.R
import com.example.usefullanguagemaster.databinding.FrMainBinding
import com.example.usefullanguagemaster.enums.DataAvailability
import com.example.usefullanguagemaster.viewModels.ViewModelGeneral
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FrMain : Fragment() {
    private var _binding: FrMainBinding? = null
    private val binding
        get() : FrMainBinding {
            return checkNotNull(_binding) {
                "Cannot access binding because it is null. Is the view visible"
            }
        }

    private val vmGeneralF: ViewModelGeneral by activityViewModels()

    private var sharedPreferences: SharedPreferences? = null

    private val tag = "FrMainTag"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // deep settings. It is the vocabulary id, for this fragment
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.sp_settings_key), Context.MODE_PRIVATE
        )

        binding.apply {
            cvVocabulary.setOnClickListener {
                findNavController().navigate(R.id.action_frMain_to_frVocabularies)
            }

            // set data to the cardView about current active Vocabulary
            setCardCurrentVocabulary()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // set data to the cardView about current active Vocabulary
    private fun setCardCurrentVocabulary() {
        viewLifecycleOwner.lifecycleScope.launch {
            vmGeneralF.apply {
                /* Use sharedPreference only if it first launch of application.
                *  In other case use variable in ViewModel (temp place).
                *
                * If shared preferences is null - is error, and we should handle it.*/
                try {
                    if (activeExpressionSetId == DataAvailability.NO_DATA.value){
                        sharedPreferences?.let {
                            activeExpressionSetId = it.getInt(getString(R.string.sp_vocabulary_id), 1)
                        } ?: throw NullPointerException("sharedPreference is null (in setCardCurrentVocabulary function)")
                    }
                } catch (e: NullPointerException){
                    Toast.makeText(context, "sharedPreference is null", Toast.LENGTH_SHORT ).show()
                }

                getAllExpressionSetsOnce() //not observe, only get once 'allExpressionSets'

                /* find active vocabulary and set it's name to cardView*/
                binding.tvVocName.text = allExpressionSets.value.firstOrNull {
                    it.id == activeExpressionSetId
                }?.name ?: ""
            }
        }
    }


}