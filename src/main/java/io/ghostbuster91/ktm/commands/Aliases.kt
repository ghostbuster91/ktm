package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.pair
import com.github.ajalt.clikt.parameters.options.validate
import io.ghostbuster91.ktm.identifier.artifact.AliasRepository
import io.ghostbuster91.ktm.logger

class Aliases(private val aliasRepository: AliasRepository) : CliktCommand() {
    private val artifactRegex = "([\\w.]+):([\\w.]+)".toRegex()
    private val aliasRegex = "(\\w)".toRegex()
    private val add by option(metavar = "\$alias \$artifact", help = "Use following pattern: \$alias \$artifact")
            .pair()
            .validate { (alias, artifact) -> (alias.matches(aliasRegex) && artifact.matches(artifactRegex)) || fail("Wrong input!") }

    override fun run() {
        if (add != null) {
            aliasRepository.addAlias(add!!.first, add!!.second)
        } else {
            aliasRepository.getAliases().forEach { logger.info(it) }
        }
    }
}