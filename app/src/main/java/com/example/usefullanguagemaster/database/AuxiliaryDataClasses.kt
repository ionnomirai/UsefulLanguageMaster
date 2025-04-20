package com.example.usefullanguagemaster.database

// Here are data classes, which are custom data classes for intermediate data from the database.
// ---------------------------------------------------------------------------------------------

// ExpressionSets (expression_sets), but with full string information without foreign keys.
data class ExpressionSetsTextData(
    val id: Int,
    val name: String,
    val languageLearning: String,
    val languageTranslation: String
)