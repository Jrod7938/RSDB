/*
 * This file is part of the RuneScape Discord Bot project.
 *
 * Licensed under the MIT License. You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT
 *
 * © 2024 Jancarlos Rodriguez, Omar Rodriguez
 */

package com.rsdb.models

/**
 * A data class representing a highscore entry for a specific skill in RuneScape.
 *
 * This class contains information about a player's rank, level, and experience in a particular skill.
 *
 * @property rank The player's rank in the skill.
 * @property level The player's level in the skill.
 * @property experience The total experience points the player has in the skill.
 */
data class HighscoreEntry(
    val rank: Int,
    val level: Int,
    val experience: Long
)
