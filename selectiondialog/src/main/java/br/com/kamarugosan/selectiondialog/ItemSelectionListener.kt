package br.com.kamarugosan.selectiondialog

interface ItemSelectionListener<T> {
    fun onSelected(item: T)
}