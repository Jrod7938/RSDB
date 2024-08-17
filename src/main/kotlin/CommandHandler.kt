package com.gepc

import com.gepc.commands.FlipCommand
import com.gepc.commands.GECommand
import com.gepc.commands.HighscoreCommand
import com.gepc.commands.WikiCommand
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on

suspend fun Kord.handleCommands() {
    on<ChatInputCommandInteractionCreateEvent> {
        when (interaction.command.rootName) {
            "flip" -> FlipCommand.handle(this)
            "ge" -> GECommand.handle(this)
            "highscore" -> HighscoreCommand.handle(this)
            "wiki" -> WikiCommand.handle(this)
        }
    }
}
