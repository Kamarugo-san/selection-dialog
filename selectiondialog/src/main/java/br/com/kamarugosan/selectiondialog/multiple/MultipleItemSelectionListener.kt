package br.com.kamarugosan.selectiondialog.multiple

interface MultipleItemSelectionListener<T> {
    fun onSelected(items: List<T>)
}