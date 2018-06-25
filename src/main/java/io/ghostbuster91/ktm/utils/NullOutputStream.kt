package io.ghostbuster91.ktm.utils

import java.io.OutputStream
import java.io.PrintStream

private class NullOutputStream : OutputStream() {
    override fun write(b: Int) = Unit
}

class NullPrintStream : PrintStream(NullOutputStream())