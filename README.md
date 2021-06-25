![Release](https://jitpack.io/v/Kamarugo-san/selection-dialog.svg)
![License](https://img.shields.io/github/license/Kamarugo-san/selection-dialog)

# Selection Dialog
Easily show a dialog with options for the user to choose from.

![image_01](/img/screenshot_01.png)
![image_02](/img/screenshot_02.png)

## Usage

**Single selection**
```
SelectionDialog.Builder(context, optionDataSet) { item, index ->
    // ...
}.build().show()
```

Or bind to an `EditText`.

```
SelectionDialog.Builder(context, optionDataSet) { item, index ->
    // ...
}
    .bindToEditText(editText)
    .build()
```

**Multiple selection**
```
MultipleSelectionDialog.Builder(this, ExampleEnum.values().toList()) { items ->
    // ...
}.build().show()
```

Or bind to an `EditText`.

```
MultipleSelectionDialog.Builder(this, ExampleEnum.values().toList()) { items ->
    // ...
}
    .bindToEditText(editText)
    .build()
```

Other usage examples can be found [here](/app/src/main/java/br/com/kamarugosan/selectiondialog/example/ExampleActivity.kt).

## Appearance
This library uses the standard material design theme attributes for its components.

*  The item separator view uses the `colorOnSurface` set in your app's theme with a 0.12 alpha modifier
*  The icons displayed when a dialog is bound to an `EditText` are tinted with `colorPrimary`

## Features

### Single selection
*  Display a list of options for the user to choose from
*  Allow user to filter through the option list (case and accents ignored)
*  Possibility to use the search text as a selection option
*  Possibility to bind the dialog to an `EditText` to work together
    *  Adds icon to guide the user (arrow pointing down)
    *  Possibility to add an X icon when there is a value selected to allow the user to clear the selection
    *  When the `EditText` is clicked, the dialog pops up

### Multiple selection
*  Display a list of options for the user to choose from
*  Allow user to filter through the option list (case and accents ignored)
*  Cancelling returns the options to their previous state
*  Possibility to bind the dialog to an `EditText` to work together
    *  Adds icon to guide the user (arrow pointing down)
    *  Possibility to add an X icon when there is a value selected to allow the user to clear the selection
    *  When the `EditText` is clicked, the dialog pops up

## Languages
Currently supported languages:
* Brazillian Portuguese
* English (international)

## Adding to your project
**Step 1:** Add the JitPack repository to your root build.gradle file

```
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2:** Add the selection-dialog dependency to your build.gradle file

```
dependencies {
    implementation 'com.github.Kamarugo-san:selection-dialog:1.0.0'
}
```

## License
```
Copyright 2021 Matheus Camargo Gomes da Silva

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```