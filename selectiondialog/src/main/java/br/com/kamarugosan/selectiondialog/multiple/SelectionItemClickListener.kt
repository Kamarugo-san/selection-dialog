package br.com.kamarugosan.selectiondialog.multiple

interface MultipleSelectionItemClickListener {
    fun onSelect(position: Int)
    fun onDeselect(position: Int)
}