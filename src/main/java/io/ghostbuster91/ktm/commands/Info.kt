package io.ghostbuster91.ktm.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import io.ghostbuster91.ktm.components.jitpack.JitPackApi
import io.ghostbuster91.ktm.identifier.artifact.ArtifactSolverDispatcher
import io.ghostbuster91.ktm.logger

class Info(private val jitPackApi: JitPackApi, artifactResolver: ArtifactSolverDispatcher) : CliktCommand() {
    private val artifact by argument()
            .convert { artifactResolver.resolve(ArtifactSolverDispatcher.Artifact.Unparsed(it)) }

    override fun run() {
        val rawStatus = jitPackApi.builds(groupId = artifact.groupId, artifactId = artifact.artifactId).blockingFirst()
        val rawBuilds = (rawStatus[artifact.groupId] as Map<String, Any>)[artifact.artifactId] as Map<String,String>
        val statuses = rawBuilds.map { (k, v) -> "$k --> $v" }
        logger.info(statuses.joinToString("\n"))
    }
}