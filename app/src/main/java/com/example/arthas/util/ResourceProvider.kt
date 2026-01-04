package com.example.arthas.util

import android.content.Context
import androidx.annotation.StringRes
import java.lang.ref.WeakReference

class ResourceProvider(context: Context) {

    private val _context = WeakReference(context)

    fun getString(@StringRes id: Int) =
        _context.get()?.getString(id) ?: String()
}