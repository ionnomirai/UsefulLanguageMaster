package com.example.usefullanguagemaster.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usefullanguagemaster.dataClasses.NewItem
import com.example.usefullanguagemaster.database.ExpressionSetsTextData
import com.example.usefullanguagemaster.database.ExpressionTypes
import com.example.usefullanguagemaster.database.Expressions
import com.example.usefullanguagemaster.database.LanguagesLearning
import com.example.usefullanguagemaster.database.PartsOfSpeech
import com.example.usefullanguagemaster.database.UlmDbRepository
import com.example.usefullanguagemaster.enums.DataAvailability
import com.example.usefullanguagemaster.enums.LanguagesLearningEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/*Need to change:
* - name allExpressionSets to allExpressionSetsText
* - name getAllExpressionSetsOnce to getAllExpressionSetsOnceText*/
class ViewModelGeneral : ViewModel() {
    private val vmGeneralTag = "ViewModelGeneralTag"

    private val repository = UlmDbRepository.get()

    //--------------------------------------------------------------------------------------------------------

    // Get ExpressionSets (expression_sets), but with full string information without foreign keys.
    private var _allExpressionSets: MutableStateFlow<List<ExpressionSetsTextData>> =
        MutableStateFlow(
            emptyList()
        )
    val allExpressionSets: StateFlow<List<ExpressionSetsTextData>>
        get() = _allExpressionSets.asStateFlow()

    suspend fun getAllExpresionSets() {
        repository.getAllExpSetsText().collect {
            _allExpressionSets.value = it
        }
    }

    suspend fun getAllExpressionSetsOnce() {
        _allExpressionSets.value = repository.getAllExpSetsTextOnce()
    }

    //--------------------------------------------------------------------------------------------------------

    fun insertExpressionSet(
        name: String,
        languagesLearningEnum: LanguagesLearningEnum = LanguagesLearningEnum.ENGLISH,
        languageTranslation: String
    ) {
        viewModelScope.launch {
            repository.insertExpressionSet(name, languagesLearningEnum, languageTranslation)
        }
    }

    //--------------------------------------------------------------------------------------------------------

    /* The id of active expressionSetId */
    var activeExpressionSetId: Int = DataAvailability.NO_DATA.value

    //--------------------------------------------------------------------------------------------------------

    private var expTypes: List<ExpressionTypes>? = null

    suspend fun getExpTypes(): List<ExpressionTypes>? {
        if (expTypes == null){
            expTypes = repository.getExpTypes()
        }
        return expTypes
    }

    private var pos: List<PartsOfSpeech>? = null

    suspend fun getPOS(): List<PartsOfSpeech>?{
        if (pos == null){
            pos = repository.getPOS()
        }
        return pos
    }

    suspend fun saveExpressionInDb(item: NewItem){

        val currentSet = allExpressionSets.value.get(activeExpressionSetId-1)
        val expSet = repository.getExpSet(currentSet.id)
        val expTypeCur = expTypes?.find { it.type == item.type }
        val posCur = pos?.find { it.shortName == item.pos }

        try {
            repository.saveExpression(
                expression = item,
                languageLearning = expSet.learningLanguage,
                languageTranslation = expSet.translationLanguage,
                expressionType = expTypeCur?.id ?: throw IllegalArgumentException("strange expression type"),
                pos = posCur?.id ?: throw IllegalArgumentException("strange pos"),
                setId = currentSet.id
            )
        } catch (e: IllegalArgumentException){
            Log.d(vmGeneralTag, e.message.toString())
        }
    }


    // ------------------test part below
    suspend fun getAllExpressions(): List<Expressions>{
        return repository.getAllExpressionsTest()
    }
}