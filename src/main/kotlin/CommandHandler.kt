package com.rsdb

import com.rsdb.commands.FlipCommand
import com.rsdb.commands.GECommand
import com.rsdb.commands.HighscoreCommand
import com.rsdb.commands.WikiCommand
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
