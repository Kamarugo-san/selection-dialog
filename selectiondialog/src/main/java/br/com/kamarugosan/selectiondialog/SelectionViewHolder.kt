package br.com.kamarugosan.selectiondialog

import android.view.View
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView

class SelectionViewHolder(itemView: View, private val listener: SelectionItemClickListener) :
    RecyclerView.ViewHolder(itemView) {
    private val radioButton: RadioButton = itemView.findViewById(R.id.selection_dialog_item)

    fun bind(item: SelectionOption, isSelected: Boolean) {
        radioButton.text = item.text
        radioButton.isEnabled = item.enabled

        // Removing listener because there'll be a change on checked state. We don't want to
        // trigger the action when binding the info to the views
        radioButton.setOnCheckedChangeListener(null)

        radioButton.isChecked = isSelected
        radioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listener.onSelect(item.index)
            }
        }
    }
}