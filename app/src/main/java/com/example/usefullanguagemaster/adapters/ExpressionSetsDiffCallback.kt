package com.example.usefullanguagemaster.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.usefullanguagemaster.database.ExpressionSets
import com.example.usefullanguagemaster.database.ExpressionSetsTextData

class ExpressionSetsDiffCallback: DiffUtil.ItemCallback<ExpressionSetsTextData>(){
    override fun areItemsTheSame(oldItem: ExpressionSetsTextData, newItem: ExpressionSetsTextData): Boolean {
         return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExpressionSetsTextData, newItem: ExpressionSetsTextData): Boolean {
        return oldItem == newItem
    }
}