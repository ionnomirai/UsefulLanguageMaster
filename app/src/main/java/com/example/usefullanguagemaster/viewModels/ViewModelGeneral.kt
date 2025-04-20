package com.example.usefullanguagemaster.viewModels

import androidx.lifecycle.ViewModel
import com.example.usefullanguagemaster.database.ExpressionSetsTextData
import com.example.usefullanguagemaster.database.LanguagesLearning
import com.example.usefullanguagemaster.database.UlmDbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelGeneral : ViewModel() {
    private val vmGeneralTag = "ViewModelGeneralTag"

    private val repository = UlmDbRepository.get()

    //-------------
    private var _allLanguagesLearning: MutableStateFlow<List<LanguagesLearning>> = MutableStateFlow(
        emptyList()
    )
    val allLanguagesLearning: StateFlow<List<LanguagesLearning>>
        get() = _allLanguagesLearning.asStateFlow()

    suspend fun getAllLanguagesLearning(){
        repository.getAllLanguagesLearning().collect{
            _allLanguagesLearning.value = it
        }
    }
    //-------------

    // Get ExpressionSets (expression_sets), but with full string information without foreign keys.
    private var _allExpressionSets: MutableStateFlow<List<ExpressionSetsTextData>> = MutableStateFlow(
        emptyList()
    )
    val allExpressionSets: StateFlow<List<ExpressionSetsTextData>>
        get() = _allExpressionSets.asStateFlow()

    suspend fun getAllExpresionSets(){
        repository.getAllExpSetsText().collect{
            _allExpressionSets.value = it
        }
    }
}