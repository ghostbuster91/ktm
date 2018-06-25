package io.ghostbuster91.ktm.utils

inline fun <T, R> Iterable<T>.foldUntil(initial: R, operation: (acc: R, T) -> R, until: (acc: R) -> Boolean): R {
    var accumulator = initial
    for (element in this) {
        if (!until(accumulator)) {
            break
        }
        accumulator = operation(accumulator, element)
    }
    return accumulator
}