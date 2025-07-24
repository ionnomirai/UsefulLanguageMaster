package com.example.usefullanguagemaster.fragments

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.usefullanguagemaster.R
import com.example.usefullanguagemaster.dataClasses.NewItem
import com.example.usefullanguagemaster.databinding.FrAddNewDataDetailedBinding
import com.example.usefullanguagemaster.exceptions.IncorrectDataEntry
import com.example.usefullanguagemaster.viewModels.ViewModelGeneral
import kotlinx.coroutines.launch


class FrAddNewDataDetailed : Fragment() {
    private var _binding: FrAddNewDataDetailedBinding? = null
    private val binding
        get(): FrAddNewDataDetailedBinding {
            return checkNotNull(_binding) {
                "Cannot access binding because it is null. Is the view visible"
            }
        }

    private val tag = "FrAddNewDataDetailedTag"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrAddNewDataDetailedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    // view model with general information
    private val vmGeneralF: ViewModelGeneral by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val expTypes = vmGeneralF.getExpTypes()?.map { it.type } ?: listOf()
            val pos = vmGeneralF.getPOS()?.map { it.shortName } ?: listOf()

            // Setting up adapters.
            setSpinnerAdapters(expTypes, pos)
        }

        binding.apply {
            bCompleteExit.setOnClickListener {
                val item = getWordInfo(
                    type = spType.selectedItem.toString()
                )
                vmGeneralF.viewModelScope.launch {
                    item?.let { vmGeneralF.saveExpressionInDb(it) }
                }
                Log.d(tag, item.toString())
            }

            bAddNewPhraseExample.setOnClickListener {
                if (llPhrases.isGone) {
                    llPhrases.visibility = View.VISIBLE
                } else {
                    addNewPhraseBlock()
                }
            }

        }
    }

    // fill and apply the spinner adapters (type of elements: word, phrase etc., or part of speech: n., v., adj., etc.)
    private fun setSpinnerAdapters(typeOfElement: List<String>, pos: List<String>) {
        val adapterTypesWords =
            ArrayAdapter(requireContext(), R.layout.back_spinner_item, typeOfElement)
                .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        with(binding.spType) {
            adapter = adapterTypesWords
            setSelection(0)
            prompt = "Select type of the element"
        }

        val adapterPOS = ArrayAdapter(requireContext(), R.layout.back_spinner_item, pos)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        with(binding.spPOS) {
            adapter = adapterPOS
            setSelection(0)
            prompt = "Select part of speech of the element"
        }
    }

    // incomplete
    private fun getWordInfo(
        type: String // word, short phrase, phrase
    ): NewItem? {

        try {
            if (type == "Word") {
                when {
                    binding.etWord.text.toString()
                        .isBlank() -> throw IncorrectDataEntry("Please fill in the \"Word\" field.")

                    binding.etTranslation.text.toString()
                        .isBlank() -> throw IncorrectDataEntry("Please fill in the \"Word translation\" field.")
                }
                getPhrases()?.let { ph ->
                    return NewItem(
                        type = type, // word
                        item = binding.etWord.text.toString() to binding.etTranslation.text.toString(), // word -- translation
                        pos = binding.spPOS.selectedItem.toString(), // for example n.
                        phrases = ph
                    )
                }
            } else {
                // not implemented yet
            }

        } catch (e: IncorrectDataEntry) {
            Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
        }

        // temp
        return null
    }

    /*
        The purpose of the function: collecting data on the phrases entered by the user (text + translation)
        Returns: a list consisting of Pair<String><String> if the validity conditions are met.
        Validity conditions:
        - both fields must not be empty, in this case, the entry is not added to the list;
        - both fields must be filled in completely (and not one of them), in this case, null is returned, and
     the function stops working

        Features of work: this function is tied to the screen structure. We know that in the phrase
     block there is an alternation of TextView - EditText - TextView - EditText. Therefore, in the
     cycle where we go through the fields, the zero element can be skipped (start with index 1).
     Each time we shift by 4 elements, and become on the first EditText of each block (when adding fields
     dynamically).
    */
    private fun getPhrases(): List<Pair<String, String>>? {
        val listPhrases: MutableList<Pair<String, String>> = mutableListOf()

        for (i in 1 until binding.llPhrases.childCount step 4) {
            val viewPhrase = binding.llPhrases.getChildAt(i) // get view by index --> Phrase (et)
            val viewPhraseTranslation =
                binding.llPhrases.getChildAt(i + 2) // Phrase translation (et)
            if ((viewPhrase is EditText) && (viewPhraseTranslation is EditText)) {
                try {
                    // if the Pair isn't null, add it to list
                    checkTwoFieldFilled(viewPhrase, viewPhraseTranslation)?.let {
                        listPhrases.add(it)
                    }
                } catch (e: IncorrectDataEntry) {
                    listPhrases.clear()
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    return null
                }
            }
        }
        return listPhrases
    }

    /*  The purpose of the function: to check two EditText fields and return a <String><String> pair
      if everything satisfies the conditions.
        Correctness conditions:
        - EditTexts cannot be half-filled, i.e. one is filled and the other is not --> return the error
      "IncorrectDataEntry" (an error message has already been created, access via the "message" parameter);
        - both EditTexts can be completely empty --> return null;
        - everything is fine in other cases -> return a <String><String> pair.
        */
    private fun checkTwoFieldFilled(et1: EditText, et2: EditText): Pair<String, String>? {
        val t1 = et1.text.toString().trim() //delete whitespaces at the start and end
        val t2 = et2.text.toString().trim()

        val isT1Filled = t1.isNotEmpty()
        val isT2Filled = t2.isNotEmpty()

        when {
            // if only one parameter is true or false
            isT1Filled xor isT2Filled -> {
                if (!isT1Filled) {
                    throw IncorrectDataEntry("Please fill in the phrase.")
                } else {
                    throw IncorrectDataEntry("Please fill in the phrase translation.")
                }
            }

            !isT1Filled && !isT2Filled -> return null
            else -> return t1 to t2
        }
    }

    private fun addNewPhraseBlock() {
        binding.llPhrases.apply {
            addView(createTextView(getString(R.string.phrase)))
            addView(createEditText())
            addView(createTextView(getString(R.string.phrase_translation)))
            addView(createEditText())
        }
    }

    // create TextView for PhraseBlock
    private fun createTextView(textOuter: String): TextView {
        return TextView(requireContext()).apply {
            setText(textOuter)
            layoutParams = LinearLayout
                .LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                .also {
                    it.setMargins(0, convertDpPx(27), 0, 0)
                }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
            typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_medium_500)
            includeFontPadding = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.app_name_color))
        }
    }

    // create TextView for PhraseBlock
    private fun createEditText(): EditText {
        return EditText(requireContext()).apply {
            layoutParams = LinearLayout
                .LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                .also {
                    it.setMargins(0, convertDpPx(27), 0, 0)
                }
            setEms(10)
            inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            maxLines = 15
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
            typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_medium_500)
            includeFontPadding = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.app_name_color))
        }
    }

    // transform dp --> px
    private fun convertDpPx(dpOuter: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpOuter.toFloat(),
            resources.displayMetrics
        )
            .toInt()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}