package br.com.kamarugosan.selectiondialog.multiple

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.kamarugosan.selectiondialog.R
import br.com.kamarugosan.selectiondialog.SelectionOption
import br.com.kamarugosan.selectiondialog.unaccent
import java.util.*
import kotlin.collections.ArrayList

class MultipleSelectionAdapter(
    val fullDataSet: List<SelectionOption>
) :
    RecyclerView.Adapter<MultipleSelectionViewHolder>() {
    private var filteredDataSet: List<SelectionOption> = ArrayList(fullDataSet)
    var selectedIndexes: MutableList<Int> = ArrayList()
    var lastFilter: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleSelectionViewHolder {
        return MultipleSelectionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_multiple_selection, parent, false),
            object : MultipleSelectionItemClickListener {
                override fun onSelect(position: Int) {
                    if (!selectedIndexes.contains(position)) {
                        selectedIndexes.add(position)
                        selectedIndexes.sort()
                    }
                }

                override fun onDeselect(position: Int) {
                    if (selectedIndexes.contains(position)) {
                        selectedIndexes.remove(position)
                    }
                }
            }
        )
    }

    override fun onBindViewHolder(holder: MultipleSelectionViewHolder, position: Int) {
        val item = filteredDataSet[position]
        val isSelected = selectedIndexes.contains(item.index)

        holder.bind(item, isSelected)
    }

    override fun getItemCount(): Int = filteredDataSet.size

    fun clearSelection() {
        if (selectedIndexes.isNotEmpty()) {
            val oldSelectedIndexes = selectedIndexes
            selectedIndexes = ArrayList()

            oldSelectedIndexes.forEach {
                notifyItemChanged(it)
            }
        }
    }

    fun filter(filter: String?): Int {
        filteredDataSet = if (filter == null || filter.isEmpty()) {
            ArrayList(fullDataSet)
        } else {
            val clearedFilter = filter.lowercase().unaccent()
            val newFilteredDataSet: MutableList<SelectionOption> = ArrayList()

            var dataSetToFilter = fullDataSet

            lastFilter?.apply {
                // Optimizing search by reducing the set to filter if the new one is a continuation
                // of the last.
                if (this.isNotEmpty() && filter.startsWith(this)) {
                    dataSetToFilter = filteredDataSet
                }
            }

            dataSetToFilter.forEach {
                if (it.filterText.contains(clearedFilter)) {
                    newFilteredDataSet.add(it)
                }
            }

            newFilteredDataSet
        }

        lastFilter = filter

        notifyDataSetChanged()
        return filteredDataSet.size
    }
}