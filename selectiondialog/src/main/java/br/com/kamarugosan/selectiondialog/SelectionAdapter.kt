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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        return SelectionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_selection, parent, false),
            object : SelectionItemClickListener {
                override fun onSelect(position: Int) {
                    val oldSelectedItem = selectedItem
                    selectedItem = position

                    if (oldSelectedItem != null) {
                        notifyItemChanged(oldSelectedItem)
                    }

                    selectionListener.onSelect(position)
                }
            }
        )
    }

    fun deselect() {
        if (selectedItem != null) {
            val oldSelectedItem = selectedItem!!
            selectedItem = null

            notifyItemChanged(oldSelectedItem)
        }
    }

    override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
        val item = filteredDataSet[position]
        val isSelected = selectedItem?.equals(item.index) ?: false

        holder.bind(item, isSelected)
    }

    override fun getItemCount(): Int = filteredDataSet.size

    fun filter(filter: String?): Int {
        filteredDataSet = if (filter == null || filter.isEmpty()) {
            ArrayList(fullDataSet)
        } else {
            val clearedFilter = filter.toLowerCase(Locale.getDefault()).unaccent()
            val newFilteredDataSet: MutableList<SelectionOption> = ArrayList()

            fullDataSet.forEach {
                if (it.filterText.contains(clearedFilter)) {
                    newFilteredDataSet.add(it)
                }
            }

            newFilteredDataSet
        }

        notifyDataSetChanged()
        return filteredDataSet.size
    }
}