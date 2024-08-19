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
 * An enum class representing various skills and activities in RuneScape.
 *
 * Each skill or activity is associated with a display name that can be used
 * for user-friendly representations. The enum also includes a companion object
 * method for retrieving a skill based on its index in the enum.
 *
 * @property displayName The user-friendly name of the skill or activity.
 */
enum class Skill(val displayName: String) {
    OVERALL("Overall"),
    ATTACK("Attack"),
    DEFENCE("Defence"),
    STRENGTH("Strength"),
    CONSTITUTION("Constitution"),
    RANGED("Ranged"),
    PRAYER("Prayer"),
    MAGIC("Magic"),
    COOKING("Cooking"),
    WOODCUTTING("Woodcutting"),
    FLETCHING("Fletching"),
    FISHING("Fishing"),
    FIREMAKING("Firemaking"),
    CRAFTING("Crafting"),
    SMITHING("Smithing"),
    MINING("Mining"),
    HERBLORE("Herblore"),
    AGILITY("Agility"),
    THIEVING("Thieving"),
    SLAYER("Slayer"),
    FARMING("Farming"),
    RUNECRAFTING("Runecrafting"),
    HUNTER("Hunter"),
    CONSTRUCTION("Construction"),
    SUMMONING("Summoning"),
    DUNGEONEERING("Dungeoneering"),
    DIVINATION("Divination"),
    INVENTION("Invention"),
    ARCHAEOLOGY("Archaeology"),
    NECROMANCY("Necromancy"),
    BOUNTY_HUNTER("Bounty Hunter"),
    BH_ROGUES("B.H. Rogues"),
    DOMINION_TOWER("Dominion Tower"),
    THE_CRUCIBLE("The Crucible"),
    CASTLE_WARS_GAMES("Castle Wars games"),
    BA_ATTACKERS("B.A. Attackers"),
    BA_DEFENDERS("B.A. Defenders"),
    BA_COLLECTORS("B.A. Collectors"),
    BA_HEALERS("B.A. Healers"),
    DUEL_TOURNAMENT("Duel Tournament"),
    MOBILISING_ARMIES("Mobilising Armies"),
    CONQUEST("Conquest"),
    FIST_OF_GUTHIX("Fist of Guthix"),
    GG_ATHLETICS("GG: Athletics"),
    GG_RESOURCE_RACE("GG: Resource Race"),
    WE2_ARMADYL_LIFETIME_CONTRIBUTION("WE2: Armadyl Lifetime Contribution"),
    WE2_BANDOS_LIFETIME_CONTRIBUTION("WE2: Bandos Lifetime Contribution"),
    WE2_ARMADYL_PVP_KILLS("WE2: Armadyl PvP kills"),
    WE2_BANDOS_PVP_KILLS("WE2: Bandos PvP kills"),
    HEIST_GUARD_LEVEL("Heist Guard Level"),
    HEIST_ROBBER_LEVEL("Heist Robber Level"),
    CFP_5_GAME_AVERAGE("CFP: 5 game average"),
    AF15_COW_TIPPING("AF15: Cow Tipping"),
    AF15_RATS_KILLED("AF15: Rats killed after the miniquest"),
    RUNESCORE("RuneScore"),
    CLUE_SCROLLS_EASY("Clue Scrolls Easy"),
    CLUE_SCROLLS_MEDIUM("Clue Scrolls Medium"),
    CLUE_SCROLLS_HARD("Clue Scrolls Hard"),
    CLUE_SCROLLS_ELITE("Clue Scrolls Elite"),
    CLUE_SCROLLS_MASTER("Clue Scrolls Master");


    companion object {
        /**
         * Retrieves a [Skill] enum instance based on its index in the enum declaration.
         *
         * @param index The index of the skill in the enum.
         * @return The corresponding [Skill] enum instance, or `null` if the index is out of bounds.
         */
        fun fromIndex(index: Int): Skill? {
            return entries.getOrNull(index)
        }
    }
}
