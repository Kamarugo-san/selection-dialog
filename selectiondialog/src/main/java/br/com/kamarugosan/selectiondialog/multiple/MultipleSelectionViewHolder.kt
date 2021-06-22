package br.com.kamarugosan.selectiondialog.multiple

import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import br.com.kamarugosan.selectiondialog.R
import br.com.kamarugosan.selectiondialog.SelectionOption

class MultipleSelectionViewHolder(
    itemView: View,
    private val listener: MultipleSelectionItemClickListener
) :
    RecyclerView.ViewHolder(itemView) {
    private val checkBox: CheckBox = itemView.findViewById(R.id.multiple_selection_dialog_item)

    fun bind(item: SelectionOption, isSelected: Boolean) {
        checkBox.text = item.text

        checkBox.setOnCheckedChangeListener(null)

        checkBox.isChecked = isSelected

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listener.onSelect(item.index)
            } else {
                listener.onDeselect(item.index)
            }
        }
    }
}