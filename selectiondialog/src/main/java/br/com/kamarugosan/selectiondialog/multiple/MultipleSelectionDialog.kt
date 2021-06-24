package br.com.kamarugosan.selectiondialog.multiple

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import br.com.kamarugosan.selectiondialog.R
import br.com.kamarugosan.selectiondialog.SelectionItemClearedListener
import br.com.kamarugosan.selectiondialog.SelectionOption

@SuppressLint("ClickableViewAccessibility")
class MultipleSelectionDialog<T> internal constructor(builder: Builder<T>) : DialogInterface {
    private val dataSet: List<T> = builder.dataSet
    private val selectionListener: SelectionListener<T> = builder.selectionListener
    private val editText: EditText? = builder.editText
    private val clearedListener: SelectionItemClearedListener? = builder.clearedListener
    private val dialog: AlertDialog
    private val adapter: MultipleSelectionAdapter
    private var selectedIndexes = builder.selectedIndexes

    class Builder<T>(
        internal val context: Context,
        internal val dataSet: List<T>,
        internal val selectionListener: SelectionListener<T>
    ) {
        constructor(context: Context, dataSet: List<T>, selectionListener: (List<T>) -> Unit) :
                this(context, dataSet, object : SelectionListener<T> {
                    override fun onSelected(items: List<T>) {
                        selectionListener(items)
                    }
                })

        internal var allowSearch = false
        internal var title: String? = null
        internal var selectedIndexes: List<Int> = ArrayList()
        internal var editText: EditText? = null
        internal var clearedListener: SelectionItemClearedListener? = null

        fun allowSearch(allowSearch: Boolean) = apply { this.allowSearch = allowSearch }

        fun bindToEditText(textInput: EditText, clearedListener: SelectionItemClearedListener?) =
            apply {
                this.editText = textInput
                this.clearedListener = clearedListener
            }

        fun bindToEditText(editText: EditText, onClearedListener: () -> Unit) =
            bindToEditText(editText, object : SelectionItemClearedListener {
                override fun onCleared() {
                    onClearedListener()
                }
            })

        fun setTitle(title: String?) = apply { this.title = title }

        fun setSelectedIndexes(selectedIndexes: List<Int>) = apply {
            this.selectedIndexes = ArrayList(selectedIndexes)
        }

        fun build(): MultipleSelectionDialog<T> = MultipleSelectionDialog(this)
    }

    init {
        val selectionOptions: MutableList<SelectionOption> = ArrayList()
        val selectedItems: MutableList<SelectionOption> = ArrayList()
        dataSet.forEachIndexed { index, item ->
            val option = SelectionOption(index, item!!, builder.context)
            selectionOptions.add(option)

            if (builder.selectedIndexes.contains(option.index)) {
                selectedItems.add(option)
            }
        }

        adapter = MultipleSelectionAdapter(selectionOptions)
        adapter.selectedIndexes = ArrayList(builder.selectedIndexes)

        val dialogView =
            LayoutInflater.from(builder.context).inflate(R.layout.dialog_selection, null, false)

        val dialogBuilder = AlertDialog.Builder(builder.context)
            .setTitle(builder.title)
            .setView(dialogView)
            .setCancelable(false)
            .setNegativeButton(R.string.selection_dialog_cancel_btn) { _, _ ->
                if (selectedIndexes.size != adapter.selectedIndexes.size
                    || !adapter.selectedIndexes.containsAll(selectedIndexes)
                ) {
                    val oldSelectedIndexes = ArrayList(adapter.selectedIndexes)
                    adapter.selectedIndexes = ArrayList(selectedIndexes)

                    // Updating the views that were deselected
                    selectedIndexes.forEach { index ->
                        if (!oldSelectedIndexes.contains(index)) {
                            adapter.notifyItemChanged(index)
                        }
                    }

                    // Updating the views that were selected
                    oldSelectedIndexes.forEach { index ->
                        if (!selectedIndexes.contains(index)) {
                            adapter.notifyItemChanged(index)
                        }
                    }
                }
            }
            .setPositiveButton(R.string.selection_dialog_confirm_btn) { _, _ ->
                selectedIndexes = ArrayList(adapter.selectedIndexes)
                val selected: MutableList<T> = ArrayList()
                val selectedOptions: MutableList<SelectionOption> = ArrayList()
                selectedIndexes.forEach {
                    selected.add(dataSet[it])
                    selectedOptions.add(selectionOptions[it])
                }

                configureEditTextSelection(selectedOptions)
                selectionListener.onSelected(selected)
            }

        dialog = dialogBuilder.create()

        val recyclerView: RecyclerView =
            dialogView.findViewById(R.id.selection_dialog_recycler_view)!!
        recyclerView.adapter = adapter

        val emptyListTv: TextView =
            dialogView.findViewById(R.id.selection_dialog_empty_list_text_view)

        if (dataSet.isEmpty()) {
            emptyListTv.visibility = View.VISIBLE
        }

        if (builder.allowSearch) {
            val searchView: SearchView = dialogView.findViewById(R.id.selection_dialog_search_view)
            searchView.visibility = View.VISIBLE
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    val remainingItemsCount = adapter.filter(newText)

                    if (remainingItemsCount == 0) {
                        emptyListTv.setText(R.string.selection_dialog_search_no_matches)
                        emptyListTv.visibility = View.VISIBLE
                    } else {
                        emptyListTv.visibility = View.GONE
                    }

                    return true
                }
            })
        }

        if (editText != null) {
            configureEditTextSelection(selectedItems)

            editText.isFocusable = false
            editText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            editText.setOnClickListener { show() }
        }
    }

    override fun cancel() {
        dialog.cancel()
    }

    override fun dismiss() {
        dialog.dismiss()
    }

    fun show() {
        dialog.show()
    }

    fun clearSelection(callClearedListener: Boolean = true) {
        adapter.clearSelection()
        selectedIndexes = ArrayList()
        configureEditTextSelection(ArrayList())

        if (callClearedListener && clearedListener != null) {
            clearedListener.onCleared()
        }
    }

    private fun configureEditTextSelection(selectedItems: List<SelectionOption>) {
        if (editText == null) {
            return
        }

        val selectionText = StringBuilder()
        selectedItems.forEach {
            if (selectionText.isNotEmpty()) {
                selectionText.append(", ")
            }

            selectionText.append(it.text)
        }

        editText.setText(selectionText.toString())

        if (selectedItems.isNotEmpty() && editText.isEnabled && clearedListener != null) {
            editText.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_action_clear, 0
            )

            editText.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableRight = 2
                    if (event.x >= (editText.width - editText.compoundDrawables[drawableRight].bounds.width())) {
                        editText.text = null

                        adapter.clearSelection()
                        selectedIndexes = ArrayList()
                        clearedListener.onCleared()
                        configureEditTextSelection(ArrayList())

                        return@setOnTouchListener true
                    }
                }

                false
            }
        } else {
            editText.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_input_arrow_down, 0
            )
            editText.setOnTouchListener { _, _ -> false }
        }
    }

    interface SelectionListener<T> {
        fun onSelected(items: List<T>)
    }
}