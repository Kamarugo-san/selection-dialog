package br.com.kamarugosan.selectiondialog.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.kamarugosan.selectiondialog.ItemSelectionListener
import br.com.kamarugosan.selectiondialog.SearchTextAsSelectionListener
import br.com.kamarugosan.selectiondialog.SelectionDialog
import br.com.kamarugosan.selectiondialog.SelectionItemClearedListener
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
                override fun onSelected(item: ExampleObject) {
                    Toast.makeText(this@ExampleActivity, item.toString(), Toast.LENGTH_SHORT).show()
                }
            })
            .bindToEditText(exampleInput1)
            .setSelectedItem(selectedItem)
            .allowSearch(true)
            .setTitle(getString(R.string.example_selection_1))
            .setDialogCancelable(false)
            .setShowCancelButton(true)
            .allowClear(object : SelectionItemClearedListener {
                override fun onCleared() {
                    Toast.makeText(this@ExampleActivity, "CLEARED", Toast.LENGTH_SHORT).show()
                }
            })
            .allowSearchTextAsSelection(object : SearchTextAsSelectionListener {
                override fun onSearchTextUsedAsSelection(searchText: String) {
                    Toast.makeText(this@ExampleActivity, searchText, Toast.LENGTH_SHORT).show()
                }
            })
            .build()
    }
}