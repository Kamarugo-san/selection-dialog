package br.com.kamarugosan.selectiondialog.example

import android.content.Context
import androidx.annotation.StringRes
import br.com.kamarugosan.selectiondialog.ToStringWithContext

enum class ExampleEnum(@StringRes val stringRes: Int) : ToStringWithContext {
    ONLINE(R.string.string_res_example_online),
    OFFLINE(R.string.string_res_example_offline),
    IDLE(R.string.string_res_example_idle),
    INVISIBLE(R.string.string_res_example_invisible);

    override fun toStringWithContext(context: Context): String {
        return context.getString(stringRes)
    }
}