package br.com.kamarugosan.selectiondialog

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
import java.util.*

@SuppressLint("ClickableViewAccessibility")
class SelectionDialog<T> internal constructor(builder: Builder<T>) : DialogInterface {
    private val dataSet: List<T> = builder.dataSet
    private val selectionListener: ItemSelectionListener<T> = builder.selectionListener
    private val editText: EditText? = builder.editText
    private val clearedListener: SelectionItemClearedListener? = builder.clearedListener
    private val dialog: AlertDialog
    private val adapter: SelectionAdapter

    class Builder<T>(
        internal val context: Context,
        internal val dataSet: List<T>,
        internal val selectionListener: ItemSelectionListener<T>
    ) {
        internal var allowSearch = false
        internal var title: String? = null
        internal var dialogCancelable = true
        internal var showCancelButton = false
        internal var selectedItem: T? = null
        internal var editText: EditText? = null
        internal var clearedListener: SelectionItemClearedListener? = null

        internal var searchTextAsSelectionListener: SearchTextAsSelectionListener? = null

        internal var searchTextAsSelectionLabel =
            context.getString(R.string.selection_dialog_search_as_selection)

        fun allowSearch(allowSearch: Boolean) = apply { this.allowSearch = allowSearch }

        fun bindToEditText(textInput: EditText, clearedListener: SelectionItemClearedListener?) =
            apply {
                this.editText = textInput
                this.clearedListener = clearedListener
            }

        fun setTitle(title: String?) = apply { this.title = title }

        fun setDialogCancelable(dialogCancelable: Boolean) =
            apply { this.dialogCancelable = dialogCancelable }

        fun setShowCancelButton(showCancelButton: Boolean) =
            apply { this.showCancelButton = showCancelButton }

        fun setSelectedItem(selectedItem: T?) = apply { this.selectedItem = selectedItem }

        @JvmOverloads
        fun allowSearchTextAsSelection(
            searchTextAsSelectionListener: SearchTextAsSelectionListener,
            searchTextAsSelectionLabel: String = context.getString(R.string.selection_dialog_search_as_selection)
        ) =
            apply {
                this.searchTextAsSelectionListener = searchTextAsSelectionListener
                this.searchTextAsSelectionLabel = searchTextAsSelectionLabel
            }

        fun build(): SelectionDialog<T> = SelectionDialog(this)
    }

    init {
        val selectionOptions: MutableList<SelectionOption> = ArrayList()
        var selectedItem: Int? = null
        dataSet.forEachIndexed { index, item ->
            selectionOptions.add(SelectionOption(index, item!!))

            if (builder.selectedItem != null && builder.selectedItem == item) {
                selectedItem = index
            }
        }

        adapter = SelectionAdapter(selectionOptions, object : SelectionItemClickListener {
            override fun onSelect(position: Int) {
                val item: T = builder.dataSet[position]
                selectionListener.onSelected(item, position)

                configureEditTextSelection(item.toString())

                dismiss()
            }
        })
        adapter.selectedItem = selectedItem

        val dialogView =
            LayoutInflater.from(builder.context).inflate(R.layout.dialog_selection, null, false)

        val dialogBuilder = AlertDialog.Builder(builder.context)
            .setTitle(builder.title)
            .setView(dialogView)
            .setCancelable(builder.dialogCancelable)

        if (builder.showCancelButton) {
            dialogBuilder.setNeutralButton(R.string.selection_dialog_cancel_btn, null)
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
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (builder.searchTextAsSelectionListener != null) {
                        val searchText = query ?: ""
                        builder.searchTextAsSelectionListener!!.onSearchTextUsedAsSelection(
                            searchText
                        )

                        configureEditTextSelection(searchText)
                        adapter.clearSelection()
                        searchView.setQuery(null, false)

                        dismiss()
                    }

                    return true
                }

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

            if (builder.searchTextAsSelectionListener != null) {
                emptyListTv.text = builder.searchTextAsSelectionLabel
            }
        }

        if (editText != null) {
            configureEditTextSelection(builder.selectedItem?.toString())

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

    fun selectItem(itemToSelect: T) {
        dataSet.forEachIndexed { index, item ->
            if (itemToSelect == item) {
                adapter.selectItem(index)

                configureEditTextSelection(item.toString())

                return@forEachIndexed
            }
        }
    }

    fun disableOption(index: Int) {
        adapter.disableOption(index)
    }

    fun enableOption(index: Int) {
        adapter.enableOption(index)
    }

    private fun configureEditTextSelection(selectedItem: String?) {
        if (editText == null) {
            return
        }

        editText.setText(selectedItem)

        if (selectedItem != null && editText.isEnabled && clearedListener != null) {
            editText.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_action_clear, 0
            )

            editText.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableRight = 2
                    if (event.x >= (editText.width - editText.compoundDrawables[drawableRight].bounds.width())) {
                        editText.text = null

                        adapter.clearSelection()
                        clearedListener.onCleared()
                        configureEditTextSelection(null)

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

    fun getSelectedItem(): T? {
        if (adapter.selectedItem != null) {
            return dataSet[adapter.selectedItem!!]
        }

        return null
    }

    fun show() {
        dialog.show()
    }
}