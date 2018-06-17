package io.ghostbuster91.ktm

import jline.TerminalFactory

class LineWrappingLogger : Logger {

    private var charsInLine = 0

    override fun error(msg: String, e: Throwable) {
        println("$msg : $e")
    }

    override fun log(msg: String) {
//        println(msg) // TODO write to file
    }

    override fun append(msg: String) {
        charsInLine += msg.length
        if (charsInLine > TerminalFactory.get().width) {
            println()
            charsInLine = msg.length
        }
        print(msg)
    }

    override fun info(msg: String) {
        println(msg)
    }
}