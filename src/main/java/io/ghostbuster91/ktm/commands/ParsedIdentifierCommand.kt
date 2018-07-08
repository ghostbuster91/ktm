package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.option
import io.ghostbuster91.ktm.identifier.Identifier
import io.ghostbuster91.ktm.identifier.IdentifierResolver

abstract class ParsedIdentifierCommand(private val identifierResolver: IdentifierResolver, help: String = "") : CliktCommand(help = help) {
    private val artifact by argument().convert { Identifier.Unparsed(it) }
    private val version by option()

    val parsed by lazy { identifierResolver.resolve(artifact, version) }
}