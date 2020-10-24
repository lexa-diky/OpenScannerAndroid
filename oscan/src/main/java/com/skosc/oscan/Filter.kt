package com.skosc.oscan

fun interface Filter<in T> {

    fun filter(value: T): Boolean
}

operator fun <T> Filter<T>.plus(other: Filter<T>): Filter<T> =
    Filter { this.filter(it) && other.filter(it) }