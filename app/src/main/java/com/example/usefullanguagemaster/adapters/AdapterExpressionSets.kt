package com.example.usefullanguagemaster.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.usefullanguagemaster.database.ExpressionSetsTextData
import com.example.usefullanguagemaster.databinding.CvVocabularyBinding
import com.example.usefullanguagemaster.fragments.FrVocabularies

/* - activeRb - is an interface for saving the active vocabulary */
class AdapterExpressionSets(activeRb: FrVocabularies.ActiveRB) :
    ListAdapter<ExpressionSetsTextData, AdapterExpressionSets.ExpSetsViewHolder>(
        ExpressionSetsDiffCallback()
    ) {
    private var indexActiveRb = 0 // Index of the active cardView (checked)

    private val tag = "AdapterExpressionSetsTag"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpSetsViewHolder {
        val bindingOuter = CvVocabularyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpSetsViewHolder(bindingOuter, rbInteraction)
    }

    override fun onBindViewHolder(holder: ExpSetsViewHolder, position: Int) {
        holder.setData(getItem(position))
    }

    /* - rbInteraction - is an interface for interacting with the active radioButton and disabling the previous rb.*/
    class ExpSetsViewHolder(
        val bindingInner: CvVocabularyBinding,
        val rbInteraction: RbInteraction
    ) :
        RecyclerView.ViewHolder(bindingInner.root) {

        fun setData(es: ExpressionSetsTextData) {
            bindingInner.apply {
                tvVocabularyCurrent.text = es.name
                tvLanguageTCurrent.text = es.languageTranslation


                if (es.id == rbInteraction.getId()) {
                    rbTurnOnOff.isChecked = true
                    /* If it active element: we need to save current the index, which will be used
                    as the previous index, when the user clicks on an another cardView. */
                    rbInteraction.savePreviousIndex(adapterPosition)
                } else {
                    rbTurnOnOff.isChecked = false
                }

                cvGeneral.setOnClickListener {
                    if (!rbTurnOnOff.isChecked) {
                        rbTurnOnOff.isChecked = true
                        rbInteraction.disablePreviousCard(currentIndex = adapterPosition, id =  es.id)
                    }
                }

            }
        }
    }

    interface RbInteraction {
        fun saveId(id: Int)
        fun getId(): Int

        fun savePreviousIndex(index: Int)
        fun getPreviousIndex(): Int
        fun disablePreviousCard(currentIndex: Int, id: Int)
    }

    private val rbInteraction = object : RbInteraction {
        override fun saveId(id: Int) {
            activeRb.setId(id)
        }

        override fun getId(): Int = activeRb.getId()

        override fun savePreviousIndex(index: Int) {
            indexActiveRb = index
        }

        override fun getPreviousIndex(): Int = indexActiveRb

        /* WARNING! Add code: in case if there is not such an index (for example: after deleting item or items)*/
        /* The logic:
        * - firstly we need to save new active id (deep saving through the interface).
        * - secondly we update the previous cardView (disable it).
        * - and at the end, we get new index, which will be used in the future as index of the previous cardView*/
        override fun disablePreviousCard(currentIndex: Int, id: Int) {
            saveId(id)
            notifyItemChanged(getPreviousIndex())
            savePreviousIndex(currentIndex)
        }
    }

}