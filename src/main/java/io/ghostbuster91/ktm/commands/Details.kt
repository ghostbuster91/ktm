package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.output.TermUi
import io.ghostbuster91.ktm.identifier.IdentifierResolver
import io.ghostbuster91.ktm.logger
import java.net.URL

class Details(identifierSolver: IdentifierResolver) : ParsedIdentifierCommand(identifierSolver) {

    override fun run() {
        try {
            TermUi.echo(URL("https://jitpack.io/api/builds/${parsed.name}/${parsed.shortVersion}").readText())
        } catch (e: RuntimeException) {
            logger.info(e.message!!)
        } finally {
            logger.info("Done")
        }
    }
}