package br.com.kamarugosan.selectiondialog

import android.content.Context
import java.util.*

class SelectionOption(val index: Int, obj: Any, context: Context) {
    val text: String = if (obj is ToStringWithContext) {
        obj.toStringWithContext(context)
    } else {
        obj.toString()
    }
    val filterText: String = text.toLowerCase(Locale.getDefault()).unaccent()
    var enabled: Boolean = true
}