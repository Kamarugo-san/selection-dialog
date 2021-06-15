package br.com.kamarugosan.selectiondialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

@SuppressLint("ClickableViewAccessibility")
class SelectionDialog<T> internal constructor(
    builder: Builder<T>
) : DialogInterface {
    private val dataSet: List<T> = builder.dataSet
    private val selectionListener: ItemSelectionListener<T> = builder.selectionListener
    private val clearedListener: SelectionItemClearedListener? = builder.clearedListener
    private val editText: EditText?
    private val dialog: AlertDialog
    private val selectionAdapter: SelectionAdapter

    class Builder<T>(
        internal val context: Context,
        internal val dataSet: List<T>,
        internal val selectionListener: ItemSelectionListener<T>
    ) {
        internal var allowSearch = false
        internal var editText: EditText? = null
        internal var title: String? = null
        internal var dialogCancelable = false
        internal var showCancelButton = false
        internal var selectedItem: T? = null
        internal var clearedListener: SelectionItemClearedListener? = null

        internal var searchTextAsSelectionListener: SearchTextAsSelectionListener? = null

        @StringRes
        internal var searchTextAsSelectionLabel: Int = R.string.selection_dialog_search_as_selection

        fun allowSearch(allowSearch: Boolean) = apply { this.allowSearch = allowSearch }

        fun bindToEditText(textInput: EditText) = apply { this.editText = textInput }

        fun setTitle(title: String?) = apply { this.title = title }

        fun setDialogCancelable(dialogCancelable: Boolean) =
            apply { this.dialogCancelable = dialogCancelable }

        fun setShowCancelButton(showCancelButton: Boolean) =
            apply { this.showCancelButton = showCancelButton }

        fun setSelectedItem(selectedItem: T) = apply { this.selectedItem = selectedItem }

        fun allowClear(clearingListener: SelectionItemClearedListener) =
            apply { this.clearedListener = clearingListener }

        @JvmOverloads
        fun allowSearchTextAsSelection(
            searchTextAsSelectionListener: SearchTextAsSelectionListener,
            @StringRes searchTextAsSelectionLabel: Int = R.string.selection_dialog_search_as_selection
        ) =
            apply {
                this.searchTextAsSelectionListener = searchTextAsSelectionListener
                this.searchTextAsSelectionLabel = searchTextAsSelectionLabel
            }

        fun build(): SelectionDialog<T> = SelectionDialog(this)
    }

    init {
        val start = System.currentTimeMillis()

        editText = builder.editText

        val selectionOptions: MutableList<SelectionOption> = ArrayList()
        var selectedItem: Int? = null
        dataSet.forEachIndexed { index, item ->
            selectionOptions.add(SelectionOption(index, item!!))

            if (builder.selectedItem != null && builder.selectedItem!! == item) {
                selectedItem = index
            }
        }

        selectionAdapter = SelectionAdapter(selectionOptions, object : SelectionItemClickListener {
            override fun onSelect(position: Int) {
                val item: T = builder.dataSet[position]
                selectionListener.onSelected(item)

                configureEditTextSelection(item.toString())

                dismiss()
            }
        })
        selectionAdapter.selectedItem = selectedItem

        val dialogView =
            LayoutInflater.from(builder.context).inflate(R.layout.dialog_selection, null, false)

        val dialogBuilder = AlertDialog.Builder(builder.context)
            .setTitle(builder.title)
            .setView(dialogView)
            .setCancelable(builder.dialogCancelable)

        if (builder.showCancelButton) {
            dialogBuilder.setNeutralButton(R.string.selection_dialog_cancel_btn, null)
        }

        dialog = dialogBuilder
            .create()

        val recyclerView: RecyclerView =
            dialogView.findViewById(R.id.selection_dialog_recycler_view)!!
        recyclerView.adapter = selectionAdapter

        val emptyListTv: TextView =
            dialogView.findViewById(R.id.selection_dialog_empty_list_text_view)

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
                        selectionAdapter.deselect()
                        searchView.setQuery(null, false)

                        dismiss()
                    }

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val remainingItemsCount = selectionAdapter.filter(newText)

                    if (remainingItemsCount == 0) {
                        emptyListTv.visibility = View.VISIBLE
                    } else {
                        emptyListTv.visibility = View.GONE
                    }

                    return true
                }
            })

            if (builder.searchTextAsSelectionListener != null) {
                emptyListTv.setText(builder.searchTextAsSelectionLabel)
            }
        }

        if (editText != null) {
            configureEditTextSelection(builder.selectedItem?.toString())

            editText.isFocusable = false
            editText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            editText.setOnClickListener {
                show()
            }
        }

        val finish = System.currentTimeMillis()
        val elapsedTime = finish - start
        Log.d("TIME_TO_START", "$elapsedTime")
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
        if (selectionAdapter.selectedItem != null) {
            return dataSet[selectionAdapter.selectedItem!!]
        }

        return null
    }

    fun show() {
        dialog.show()
    }

    override fun cancel() {
        dialog.cancel()
    }

    override fun dismiss() {
        dialog.dismiss()
    }
}