package com.example.usefullanguagemaster.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.usefullanguagemaster.R
import com.example.usefullanguagemaster.databinding.FrVocabulariesAddBinding
import com.example.usefullanguagemaster.viewModels.ViewModelGeneral

class FrVocabulariesAdd : Fragment(){
    private var _binding : FrVocabulariesAddBinding? = null
    private val binding
        get(): FrVocabulariesAddBinding{
            return checkNotNull(_binding){
                "Cannot access binding because it is null. Is the view visible"
            }
        }

    // view model with general information
    private val vmGeneralF: ViewModelGeneral by activityViewModels()

    private var name: String = ""
    private var languageTranslation: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrVocabulariesAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            bAdd.setOnClickListener{
                getDataFromEditTexts()
                if(name.isNotBlank() && languageTranslation.isNotBlank()){
                    vmGeneralF.insertExpressionSet(name = name, languageTranslation = languageTranslation)

                    //Get back to the previous fragment (screen). Current fragment will be deleted.
                    findNavController().navigate(
                        resId = R.id.frVocabularies,
                        args = null,
                        navOptions = NavOptions.Builder().setPopUpTo(R.id.frVocabulariesAdd, true).build()
                    )
                } else {
                    Toast.makeText(requireContext(), "Please, fill all information.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDataFromEditTexts(){
        binding.apply {
            name = etName.text.toString()
            languageTranslation = etLanguageTranslation.text.toString()
        }
    }
}