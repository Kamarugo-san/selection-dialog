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

@SuppressLint("ClickableViewAccessibility", "InflateParams")
class SelectionDialog<T> internal constructor(private val builder: Builder<T>) : DialogInterface {
    private val dataSet: List<T> = builder.dataSet
    private val selectionListener: SelectionListener<T> = builder.selectionListener
    private val editText: EditText? = builder.editText
    private val clearedListener: SelectionItemClearedListener? = builder.clearedListener
    private val dialog: AlertDialog by lazy {
        val dialogView =
            LayoutInflater.from(builder.context).inflate(R.layout.dialog_selection, null, false)

        val dialogBuilder = AlertDialog.Builder(builder.context)
            .setTitle(builder.title)
            .setView(dialogView)
            .setCancelable(builder.dialogCancelable)

        if (builder.showCancelButton) {
            dialogBuilder.setNeutralButton(R.string.selection_dialog_cancel_btn, null)
        }

        val recyclerView: RecyclerView =
            dialogView.findViewById(R.id.selection_dialog_recycler_view)!!
        recyclerView.adapter = adapter

        val emptyListTv: TextView =
            dialogView.findViewById(R.id.selection_dialog_empty_list_text_view)

        if (dataSet.isEmpty()) {
            emptyListTv.visibility = View.VISIBLE
        } else {
            emptyListTv.setText(R.string.selection_dialog_search_no_matches)
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

        dialogBuilder.create()
    }
    private val adapter: SelectionAdapter

    /**
     * Builder for the [SelectionDialog].
     */
    class Builder<T>(
        /**
         * The parent context.
         */
        internal val context: Context,

        /**
         * The data set the user can select from.
         */
        internal val dataSet: List<T>,

        /**
         * The listener to be called when the user makes a selection.
         */
        internal val selectionListener: SelectionListener<T>
    ) {
        /**
         * Builder for the [SelectionDialog].
         *
         * @param context           the parent context
         * @param dataSet           the data set the user can select from
         * @param selectionListener the listener to be called when the user makes a selection
         */
        constructor(context: Context, dataSet: List<T>, selectionListener: (T, Int) -> Unit) : this(
            context,
            dataSet,
            object : SelectionListener<T> {
                override fun onSelected(item: T, index: Int) {
                    selectionListener(item, index)
                }
            })

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

        /**
         * Sets whether the [SearchView] will be shown.
         *
         * @param allowSearch whether or not the [SearchView] should be shown
         */
        fun allowSearch(allowSearch: Boolean) = apply { this.allowSearch = allowSearch }

        /**
         * Binds the [SelectionDialog] to the given [EditText]. That is, the [EditText] will have
         * an arrow icon on the right to indicate it is a selection field. When a value is selected
         * the icon will turn into an X and allow the user to clear the selection by touching it.
         *
         * @param editText        the text field to bind the events to
         * @param clearedListener a listener for when the user clears the selection. Null if you
         * don't want to allow the user to clear the selection.
         */
        fun bindToEditText(
            editText: EditText,
            clearedListener: SelectionItemClearedListener? = null
        ) =
            apply {
                this.editText = editText
                this.clearedListener = clearedListener
            }

        /**
         * Binds the [SelectionDialog] to the given [EditText]. That is, the [EditText] will have
         * an arrow icon on the right to indicate it is a selection field. When a value is selected
         * the icon will turn into an X and allow the user to clear the selection by touching it.
         *
         * @param editText       the text field to bind the events to
         * @param clearedListener a listener for when the user clears the selection. Null if you
         * don't want to allow the user to clear the selection.
         */
        fun bindToEditText(editText: EditText, clearedListener: () -> Unit) =
            bindToEditText(editText, object : SelectionItemClearedListener {
                override fun onCleared() {
                    clearedListener()
                }
            })

        /**
         * Sets the [AlertDialog] title.
         *
         * @param title the title to be shown
         */
        fun setTitle(title: String?) = apply { this.title = title }

        /**
         * Sets whether the [AlertDialog] is cancelable.
         *
         * @param dialogCancelable whether the dialog is cancelable
         */
        fun setDialogCancelable(dialogCancelable: Boolean) =
            apply { this.dialogCancelable = dialogCancelable }

        /**
         * Sets whether the [AlertDialog] should display a cancel button.
         *
         * @param showCancelButton whether the dialog should display a cancel button
         */
        fun setShowCancelButton(showCancelButton: Boolean) =
            apply { this.showCancelButton = showCancelButton }

        /**
         * Sets the initial selected item.
         *
         * @param selectedItem the initial selected item
         */
        fun setSelectedItem(selectedItem: T?) = apply { this.selectedItem = selectedItem }

        /**
         * Sets whether the [SearchView]'s text should be used as a selection value when the enter
         * key is pressed on the keyboard. Only works if [allowSearch] is true.
         *
         * @param searchTextAsSelectionListener listener to be called when the enter key is pressed
         * @param searchTextAsSelectionLabel    custom label to hint the user at the possibility of
         * using the search text as selection
         */
        @JvmOverloads
        fun allowSearchTextAsSelection(
            searchTextAsSelectionListener: SearchTextAsSelectionListener,
            searchTextAsSelectionLabel: String = this.searchTextAsSelectionLabel
        ) =
            apply {
                this.searchTextAsSelectionListener = searchTextAsSelectionListener
                this.searchTextAsSelectionLabel = searchTextAsSelectionLabel
            }

        /**
         * Sets whether the [SearchView]'s text should be used as a selection value when the enter
         * key is pressed on the keyboard. Only works if [allowSearch] is true.
         *
         * @param searchTextAsSelectionListener listener to be called when the enter key is pressed
         */
        fun allowSearchTextAsSelection(
            searchTextAsSelectionListener: (String) -> Unit
        ) = allowSearchTextAsSelection(object : SearchTextAsSelectionListener {
            override fun onSearchTextUsedAsSelection(searchText: String) {
                searchTextAsSelectionListener(searchText)
            }
        }, searchTextAsSelectionLabel)

        /**
         * Sets whether the [SearchView]'s text should be used as a selection value when the enter
         * key is pressed on the keyboard. Only works if [allowSearch] is true.
         *
         * @param searchTextAsSelectionListener listener to be called when the enter key is pressed
         * @param searchTextAsSelectionLabel    custom label to hint the user at the possibility of
         * using the search text as selection
         */
        fun allowSearchTextAsSelection(
            searchTextAsSelectionListener: (String) -> Unit,
            searchTextAsSelectionLabel: String
        ) =
            allowSearchTextAsSelection(object : SearchTextAsSelectionListener {
                override fun onSearchTextUsedAsSelection(searchText: String) {
                    searchTextAsSelectionListener(searchText)
                }
            }, searchTextAsSelectionLabel)

        /**
         * Builds the [SelectionDialog].
         */
        fun build(): SelectionDialog<T> = SelectionDialog(this)
    }

    init {
        val selectionOptions: MutableList<SelectionOption> = ArrayList()
        var selectedItem: Int? = null
        dataSet.forEachIndexed { index, item ->
            selectionOptions.add(SelectionOption(index, item!!, builder.context))

            if (builder.selectedItem != null && builder.selectedItem == item) {
                selectedItem = index
            }
        }

        adapter = SelectionAdapter(selectionOptions, object : SelectionItemClickListener {
            override fun onSelect(position: Int) {
                val item: T = builder.dataSet[position]
                selectionListener.onSelected(item, position)

                configureEditTextSelection(selectionOptions[position].text)

                dismiss()
            }
        })
        adapter.selectedItem = selectedItem

        if (editText != null) {
            if (builder.selectedItem != null && builder.selectedItem is ToStringWithContext) {
                configureEditTextSelection(
                    (builder.selectedItem!! as ToStringWithContext).toStringWithContext(
                        builder.context
                    )
                )
            } else {
                configureEditTextSelection(builder.selectedItem?.toString())
            }

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

    /**
     * Shows the dialog.
     */
    fun show() {
        dialog.show()
    }

    /**
     * Sets the given item as the currently selected on the dialog.
     *
     * @param itemToSelect the item to set as selected
     */
    fun selectItem(itemToSelect: T) {
        dataSet.forEachIndexed { index, item ->
            if (itemToSelect == item) {
                adapter.selectItem(index)

                val selectionText = if (itemToSelect is ToStringWithContext) {
                    (item as ToStringWithContext).toStringWithContext(builder.context)
                } else {
                    item.toString()
                }

                configureEditTextSelection(selectionText)


                return@forEachIndexed
            }
        }
    }

    /**
     * Clears the current selection.
     *
     * @param callClearedListener whether the [clearedListener] should be called
     */
    fun clearSelection(callClearedListener: Boolean = true) {
        adapter.clearSelection()
        configureEditTextSelection(null)

        if (callClearedListener && clearedListener != null) {
            clearedListener.onCleared()
        }
    }

    /**
     * Disables a selection option.
     *
     * @param index the position of the option to disable
     */
    fun disableOption(index: Int) {
        adapter.disableOption(index)
    }

    /**
     * Enables a selection option.
     *
     * @param index the position of the option to enable
     */
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

    /**
     * Used to get the current selected item.
     *
     * @return the selected item or null if none are selected
     */
    fun getSelectedItem(): T? {
        if (adapter.selectedItem != null) {
            return dataSet[adapter.selectedItem!!]
        }

        return null
    }

    /**
     * Listener for [SelectionDialog].
     */
    interface SelectionListener<T> {
        /**
         * When the user selects one of the options of the dialog.
         *
         * @param item  the object selected by the user
         * @param index the position of the selected option in the data set
         */
        fun onSelected(item: T, index: Int)
    }

    /**
     * Listener for when the search text is used as the selection value.
     */
    interface SearchTextAsSelectionListener {
        /**
         * Called when the user presses the enter key on the keyboard.
         *
         * @param searchText the text on the [SearchView]
         */
        fun onSearchTextUsedAsSelection(searchText: String)
    }
}