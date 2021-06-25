package br.com.kamarugosan.selectiondialog.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.kamarugosan.selectiondialog.SelectionDialog
import br.com.kamarugosan.selectiondialog.multiple.MultipleSelectionDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/**
 * Here you can find examples on how to use both [SelectionDialog] and [MultipleSelectionDialog].
 */
class ExampleActivity : AppCompatActivity(R.layout.activity_example) {
    private val selectedItem = ExampleObject("Ayrton", "Senna")
    private val exampleDataSet = listOf(
        ExampleObject("Alain", "Prost"),
        ExampleObject("Alan", "Jones"),
        ExampleObject("Alberto", "Ascari"),
        selectedItem,
        ExampleObject("Damon", "Hill"),
        ExampleObject("Denny", "Hulme"),
        ExampleObject("Emerson", "Fittipaldi"),
        ExampleObject("Fernando", "Alonso"),
        ExampleObject("Giuseppe", "Farina"),
        ExampleObject("Graham", "Hill"),
        ExampleObject("Jack", "Brabham"),
        ExampleObject("Jackie", "Stewart"),
        ExampleObject("Jacques", "Villeneuve"),
        ExampleObject("James", "Hunt"),
        ExampleObject("Jenson", "Button"),
        ExampleObject("Juan", "Manuel Fangio"),
        ExampleObject("Jim", "Clark"),
        ExampleObject("Jochen", "Rindt"),
        ExampleObject("Jody", "Scheckter"),
        ExampleObject("John", "Surtees"),
        ExampleObject("Keke", "Rosberg"),
        ExampleObject("Kimi", "Räikkönen"),
        ExampleObject("Lewis", "Hamilton"),
        ExampleObject("Mario", "Andretti"),
        ExampleObject("Michael", "Schumacher"),
        ExampleObject("Mika", "Häkkinen"),
        ExampleObject("Mike", "Hawthorn"),
        ExampleObject("Nelson", "Piquet"),
        ExampleObject("Niki", "Lauda"),
        ExampleObject("Nigel", "Mansell"),
        ExampleObject("Nico", "Rosberg"),
        ExampleObject("Phil", "Hill"),
        ExampleObject("Sebastian", "Vettel"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Simplest single selection dialog
        val singleSelectionDialog =
            SelectionDialog.Builder(this, exampleDataSet) { item, index ->
                Toast.makeText(this@ExampleActivity, "$item - $index", Toast.LENGTH_SHORT).show()
            }.build()
        val singleBtn = findViewById<MaterialButton>(R.id.example_single_launch)
        singleBtn.setOnClickListener {
            singleSelectionDialog.show()
        }

        // Simplest multiple selection dialog
        val multipleSelectionDialog =
            MultipleSelectionDialog.Builder(this, exampleDataSet) { items ->
                Toast.makeText(
                    this@ExampleActivity, "MULTIPLE SELECTED ${items.size}", Toast.LENGTH_SHORT
                ).show()
            }.build()
        val multipleBtn = findViewById<MaterialButton>(R.id.example_multiple_launch)
        multipleBtn.setOnClickListener {
            multipleSelectionDialog.show()
        }

        // Single selection dialog bound to EditText
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

        // Multiple selection dialog bound to EditText
        val exampleMultipleInput1 =
            findViewById<TextInputEditText>(R.id.example_multiple_selection_input)
        val selectedIndexes = listOf(3, 21, 32)

        MultipleSelectionDialog.Builder(
            this,
            exampleDataSet
        ) { items ->
            Toast.makeText(
                this@ExampleActivity, "MULTIPLE SELECTED ${items.size}", Toast.LENGTH_SHORT
            )
                .show()
        }
            .bindToEditText(exampleMultipleInput1) {
                Toast.makeText(this@ExampleActivity, "CLEARED MULTIPLE", Toast.LENGTH_SHORT)
                    .show()
            }
            .setTitle(getString(R.string.example_multiple_selection_1))
            .setSelectedIndexes(selectedIndexes)
            .allowSearch(true)
            .build()

        // Example of objects implementing ToStringWithContext
        // Selection enums work for both single and multiple selection dialogs
        val exampleInput2 = findViewById<TextInputEditText>(R.id.example_selection2_input)
        MultipleSelectionDialog.Builder(
            this,
            ExampleEnum.values().toList()
        ) { items ->
            Toast.makeText(
                this@ExampleActivity,
                "MULTIPLE SELECTED ${items.size}}",
                Toast.LENGTH_SHORT
            )
                .show()
        }
            .bindToEditText(exampleInput2) // Not allowing user to clear the selection
            .setSelectedIndexes(listOf(1, 2))
            .setTitle(getString(R.string.example_selection_2))
            .build()
    }
}