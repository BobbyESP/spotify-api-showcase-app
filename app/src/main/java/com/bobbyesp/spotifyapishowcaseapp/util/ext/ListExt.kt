package com.bobbyesp.spotifyapishowcaseapp.util.ext

/**
 * Returns the second element, or `null` if the list has less than 2 elements.
 */
fun <T> List<T>.secondOrNull(): T? {
    return if (isEmpty()) null else this[1]
}