package com.example.usefullanguagemaster.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.usefullanguagemaster.database.ExpressionSets

class ExpressionSetsDiffCallback: DiffUtil.ItemCallback<ExpressionSets>(){
    override fun areItemsTheSame(oldItem: ExpressionSets, newItem: ExpressionSets): Boolean {
         return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExpressionSets, newItem: ExpressionSets): Boolean {
        return oldItem == newItem
    }
}