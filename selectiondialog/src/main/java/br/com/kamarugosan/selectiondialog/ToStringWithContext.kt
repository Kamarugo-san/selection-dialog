package br.com.kamarugosan.selectiondialog

import android.content.Context

/**
 * Implement this if a String representation of your object needs a context to be obtained. For
 * example an enum with values that vary according to the locale could have the string resource and
 * receive a context to get the appropriate string.
 */
interface ToStringWithContext {
    fun toStringWithContext(context: Context) : String
}