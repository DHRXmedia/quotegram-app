package com.nesib.yourbooknotes.utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import java.util.*

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Int.toFormattedNumber():String {
    var text = "$this"
    if(this in 1000..9999){
        text = (this/1000).toString() + "." + ((this/100) % 10).toString() + "k"
    }
    else if(this in 10000..99999){
        text = (this/1000).toString() + "." + ((this/100) % 10).toString() + "k"
    }
    return text
}

fun List<String>.toJoinedString(): String {
    var index = 0
    var genresText = ""
    this.forEach { genre ->
        if (index < this.size - 1) {
            genresText += "${genre.toLowerCase(Locale.ROOT)},"
        } else {
            genresText += genre.toLowerCase(Locale.ROOT)
        }
        index++
    }
    return genresText
}