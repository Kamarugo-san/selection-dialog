package br.com.kamarugosan.selectiondialog.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.kamarugosan.selectiondialog.SelectionDialog
import br.com.kamarugosan.selectiondialog.SelectionItemClearedListener
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
            exampleDataSet
        ) { item, index ->
            Toast.makeText(this@ExampleActivity, "$item - $index", Toast.LENGTH_SHORT).show()
        }
            .bindToEditText(exampleInput1) {
                Toast.makeText(this@ExampleActivity, "CLEARED", Toast.LENGTH_SHORT).show()
            }
            .setSelectedItem(selectedItem)
            .allowSearch(true)
            .setTitle(getString(R.string.example_selection_1))
            .setDialogCancelable(false)
            .setShowCancelButton(true)
            .allowSearchTextAsSelection { searchText ->
                Toast.makeText(this@ExampleActivity, searchText, Toast.LENGTH_SHORT).show()
            }
            .build()

        val exampleInput2 = findViewById<TextInputEditText>(R.id.example_selection2_input)
        MultipleSelectionDialog.Builder(
            this,
            ExampleEnum.values().toList(),
            object : MultipleSelectionDialog.SelectionListener<ExampleEnum> {
                override fun onSelected(items: List<ExampleEnum>) {
                    Toast.makeText(this@ExampleActivity, "MULTIPLE SELECTED", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
            .bindToEditText(exampleInput2, object : SelectionItemClearedListener {
                override fun onCleared() {
                    Toast.makeText(this@ExampleActivity, "CLEARED MULTIPLE", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .setTitle(getString(R.string.example_selection_2))
            .build()

        /*
        SelectionDialog.Builder(
            this,
            ExampleEnum.values().toList(),
            object : ItemSelectionListener<ExampleEnum> {
                override fun onSelected(item: ExampleEnum, index: Int) {
                    Toast.makeText(this@ExampleActivity, item.toStringWithContext(this@ExampleActivity), Toast.LENGTH_SHORT).show()
                }
            }
        )
            .bindToEditText(exampleInput2, object : SelectionItemClearedListener {
                override fun onCleared() {
                    Toast.makeText(this@ExampleActivity, "CLEARED", Toast.LENGTH_SHORT).show()
                }
            })
            .setShowCancelButton(true)
            .setTitle(getString(R.string.example_selection_2))
            .build()
        */

        val exampleMultipleInput1 =
            findViewById<TextInputEditText>(R.id.example_multiple_selection_input)
        val selectedIndexes = listOf(1, 2, 3)

        MultipleSelectionDialog.Builder(
            this,
            exampleDataSet
        ) {
            Toast.makeText(this@ExampleActivity, "MULTIPLE SELECTED", Toast.LENGTH_SHORT)
                .show()
        }
            .setTitle(getString(R.string.example_multiple_selection_1))
            .setSelectedIndexes(selectedIndexes)
            .allowSearch(true)
            .bindToEditText(exampleMultipleInput1) {
                Toast.makeText(this@ExampleActivity, "CLEARED MULTIPLE", Toast.LENGTH_SHORT)
                    .show()
            }
            .build()
    }
}