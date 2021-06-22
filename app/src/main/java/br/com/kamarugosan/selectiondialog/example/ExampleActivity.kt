package br.com.kamarugosan.selectiondialog.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.kamarugosan.selectiondialog.ItemSelectionListener
import br.com.kamarugosan.selectiondialog.SearchTextAsSelectionListener
import br.com.kamarugosan.selectiondialog.SelectionDialog
import br.com.kamarugosan.selectiondialog.SelectionItemClearedListener
import br.com.kamarugosan.selectiondialog.multiple.MultipleItemSelectionListener
import br.com.kamarugosan.selectiondialog.multiple.MultipleSelectionDialog
import com.google.android.material.textfield.TextInputEditText

class ExampleActivity : AppCompatActivity(R.layout.activity_example) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedItem = ExampleObject("Joseph", "Joestar")
        val exampleDataSet = listOf(
            ExampleObject("AEIOU", "áàâãäéèêëiíìïîóòôõöúùûü"),
            ExampleObject("Dio", "Brando"),
            ExampleObject("Erina", "Pendleton"),
            ExampleObject("Enrico", "Pucci"),
            ExampleObject("George", "Joestar"),
            ExampleObject("Giorno", "Giovanna"),
            ExampleObject("Jean", "Pierre Polnareff"),
            ExampleObject("Jonathan", "Joestar"),
            ExampleObject("Johnny", "Joestar"),
            ExampleObject("Josefumi", "Kujo"),
            selectedItem,
            ExampleObject("Josuke", "Higashikata"),
            ExampleObject("Jotaro", "Kujo"),
            ExampleObject("Hirose", "Kōichi"),
            ExampleObject("Poco", ""),
            ExampleObject("Robert", "E. O. Speedwagon"),
            ExampleObject("Tonpetty", ""),
            ExampleObject("Will", "A. Zeppeli"),
            ExampleObject("Yoshikage", "Kira"),
        )

        val exampleInput1 = findViewById<TextInputEditText>(R.id.example_selection_input)

        SelectionDialog.Builder(
            this,
            exampleDataSet,
            object : ItemSelectionListener<ExampleObject> {
                override fun onSelected(item: ExampleObject, index: Int) {
                    Toast.makeText(this@ExampleActivity, item.toString(), Toast.LENGTH_SHORT).show()
                }
            })
            .bindToEditText(exampleInput1, object : SelectionItemClearedListener {
                override fun onCleared() {
                    Toast.makeText(this@ExampleActivity, "CLEARED", Toast.LENGTH_SHORT).show()
                }
            })
            .setSelectedItem(selectedItem)
            .allowSearch(true)
            .setTitle(getString(R.string.example_selection_1))
            .setDialogCancelable(false)
            .setShowCancelButton(true)
            .allowSearchTextAsSelection(object : SearchTextAsSelectionListener {
                override fun onSearchTextUsedAsSelection(searchText: String) {
                    Toast.makeText(this@ExampleActivity, searchText, Toast.LENGTH_SHORT).show()
                }
            })
            .build()

        val exampleMultipleInput1 =
            findViewById<TextInputEditText>(R.id.example_multiple_selection_input)
        val selectedIndexes = listOf(1, 2, 3)

        MultipleSelectionDialog.Builder(
            this,
            exampleDataSet,
            object : MultipleItemSelectionListener<ExampleObject> {
                override fun onSelected(items: List<ExampleObject>) {
                    Toast.makeText(this@ExampleActivity, "MULTIPLE SELECTED", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .setTitle(getString(R.string.example_multiple_selection_1))
            .setSelectedIndexes(selectedIndexes)
            .allowSearch(true)
            .bindToEditText(exampleMultipleInput1, object : SelectionItemClearedListener {
                override fun onCleared() {
                    Toast.makeText(this@ExampleActivity, "CLEARED MULTIPLE", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .build()
    }
}