package br.com.kamarugosan.selectiondialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class SelectionAdapter(
    private val fullDataSet: List<SelectionOption>,
    private val selectionListener: SelectionItemClickListener
) :
    RecyclerView.Adapter<SelectionViewHolder>() {
    private var filteredDataSet: List<SelectionOption> = ArrayList(fullDataSet)
    var selectedItem: Int? = null
    var lastFilter: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        return SelectionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_selection, parent, false),
            object : SelectionItemClickListener {
                override fun onSelect(position: Int) {
                    selectItem(position, false)

                    selectionListener.onSelect(position)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
        val item = filteredDataSet[position]
        val isSelected = selectedItem?.equals(item.index) ?: false

        holder.bind(item, isSelected)
    }

    override fun getItemCount(): Int = filteredDataSet.size

    fun selectItem(index: Int, notifySelectedView: Boolean = true) {
        val oldSelectedItem = selectedItem
        selectedItem = index

        if (oldSelectedItem != null) {
            notifyItemChanged(oldSelectedItem)
        }

        if (notifySelectedView) {
            notifyItemChanged(index)
        }
    }

    fun disableOption(index: Int) {
        if (fullDataSet[index].enabled) {
            fullDataSet[index].enabled = false
            notifyByOptionIndex(index)
        }
    }

    fun enableOption(index: Int) {
        if (!fullDataSet[index].enabled) {
            fullDataSet[index].enabled = true
            notifyByOptionIndex(index)
        }
    }

    private fun notifyByOptionIndex(index: Int) {
        filteredDataSet.forEachIndexed { i, selectionOption ->
            if (selectionOption.index == index) {
                notifyItemChanged(i)
                return@forEachIndexed
            }
        }
    }

    fun clearSelection() {
        if (selectedItem != null) {
            var itemToUpdate: Int = selectedItem!!
            filteredDataSet.forEachIndexed { index, selectionOption ->
                if (selectionOption.index == selectedItem!!) {
                    itemToUpdate = index
                    return@forEachIndexed
                }
            }

            selectedItem = null

            notifyItemChanged(itemToUpdate)
        }
    }

    fun filter(filter: String?): Int {
        filteredDataSet = if (filter == null || filter.isEmpty()) {
            ArrayList(fullDataSet)
        } else {
            val clearedFilter = filter.toLowerCase(Locale.getDefault()).unaccent()
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