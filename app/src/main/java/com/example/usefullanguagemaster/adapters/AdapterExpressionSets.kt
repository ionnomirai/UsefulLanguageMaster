package com.example.usefullanguagemaster.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.usefullanguagemaster.database.ExpressionSets
import com.example.usefullanguagemaster.databinding.CvVocabularyBinding

class AdapterExpressionSets
    :
    ListAdapter<ExpressionSets, AdapterExpressionSets.ExpSetsViewHolder>(ExpressionSetsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpSetsViewHolder {
        val bindingOuter = CvVocabularyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpSetsViewHolder(bindingOuter)
    }

    override fun onBindViewHolder(holder: ExpSetsViewHolder, position: Int) {
        holder.setData(getItem(position))
    }

    class ExpSetsViewHolder(val bindingInner: CvVocabularyBinding) :
        RecyclerView.ViewHolder(bindingInner.root) {

        fun setData(es: ExpressionSets) {
            bindingInner.apply {
                tvLanguageTCurrent.text = ""
                tvVocabularyCurrent.text = ""
            }
        }
    }

}