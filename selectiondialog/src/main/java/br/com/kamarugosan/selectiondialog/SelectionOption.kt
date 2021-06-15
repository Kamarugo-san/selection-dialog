package br.com.kamarugosan.selectiondialog

import java.util.*

class SelectionOption(val index: Int, obj: Any) {
    val text: String = obj.toString()
    val filterText: String = obj.toString().toLowerCase(Locale.getDefault()).unaccent()
}