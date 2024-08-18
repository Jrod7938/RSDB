package com.rsdb.commands

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

/**
 * Object responsible for managing the "me" command.
 *
 * The "me" command allows Discord users to link their RuneScape profile to their Discord account.
 * This object encapsulates the functionality needed to handle the command, including storing
 * the mapping between Discord user IDs and RuneScape usernames.
 */
object MeCommand {

    /**
     * Handles the execution of the "me" command.
     *
     * This function stores the RuneScape username provided by the user in the `userProfiles` map,
     * linking it to the user's Discord ID. It then responds to the user with a confirmation message
     * that includes a link to their RuneScape highscore page.
     *
     * @param event The event triggered by the "me" command.
     * @param userProfiles A mutable map linking Discord user IDs to their linked RuneScape usernames.
     */
    suspend fun handle(event: ChatInputCommandInteractionCreateEvent, userProfiles: MutableMap<String, String>) {
        val response = event.interaction.deferPublicResponse()
        val discordUserId = event.interaction.user.id.toString()
        val runescapeUsername = event.interaction.command.options["username"]?.value.toString()

        // Store the username linked to the Discord ID
        userProfiles[discordUserId] = runescapeUsername

        // Respond to the user
        response.respond {
            content =
                "RuneScape Profile [$runescapeUsername](https://secure.runescape.com/m=hiscore/compare?user1=$runescapeUsername) has been linked to your Discord profile."
        }
    }
}
