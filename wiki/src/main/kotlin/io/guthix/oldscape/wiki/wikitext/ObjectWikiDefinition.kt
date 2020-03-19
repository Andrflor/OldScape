/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.wiki.wikitext

import io.guthix.oldscape.wiki.WikiConfigCompanion
import io.guthix.oldscape.wiki.WikiDefinition
import java.time.LocalDate

class ObjectWikiDefinition : WikiDefinition<ObjectWikiDefinition>() {
    override var ids: List<Int>? = null
    override var name: String? = null
    var versionType: String? = null
    var releaseDate: LocalDate? = null
    var update: String? = null
    var isMembers: Boolean? = null
    var quests: List<String>? = null
    var isTradable: Boolean? = null
    var isEquipable: Boolean? = null
    var isStackable: Boolean? = null
    var isNoteable: Boolean? = null
    var hasPlaceHolder: Boolean? = null
    var isAlchable: Boolean? = null
    var destroy: String? = null
    var valuePrice: Int? = null
    var storePrice: List<Int>? = null
    var seller: String? = null
    var weight: Float? = null
    var examine: String? = null
    var onExchange: Boolean? = null

    //equipment
    var slot: String? = null
    var attBonusStab: Int? = null
    var attBonusSlash: Int? = null
    var attBonusCrush: Int? = null
    var attBonusMagic: Int? = null
    var attBonusRange: Int? = null
    var defBonusStab: Int? = null
    var defBonusSlash: Int? = null
    var defBonusCrush: Int? = null
    var defBonusMagic: Int? = null
    var defBonusRange: Int? = null
    var strengthBonus: Int? = null
    var rangeStrengthBonus: Int? = null
    var magicDamageBonus: Int? = null
    var prayerBonus: Int? = null

    override fun parse(page: String, version: Int?): ObjectWikiDefinition {
        super.parse(page, version)
        return this
    }

    override fun parseKeyValueLine(line: String, version: Int?)  = when {
        line.checkWikiKey("id", version) -> ids = line.getIds()
        line.checkWikiKey("versionType", version) -> versionType = line.getWikiString()
        line.checkWikiKey("name", version) -> name = line.getWikiString()
        line.checkWikiKey("release", version) -> releaseDate = line.getWikiDate()
        line.checkWikiKey("update", version) -> update = line.getWikiString()
        line.checkWikiKey("members", version) -> isMembers = line.getWikiBool()
        line.checkWikiKey("quest", version) -> {
            if(line.getWikiString().equals("No", ignoreCase = true)) {
                quests = null
            } else {
                quests = line.getWikiStrings()
                    ?.map { it.replace("[", "").replace("]", "") }
            }
        }
        line.checkWikiKey("tradeable", version) -> isTradable = line.getWikiBool()
        line.checkWikiKey("equipable", version) -> isEquipable = line.getWikiBool()
        line.checkWikiKey("stackable", version) -> isStackable = line.getWikiBool()
        line.checkWikiKey("noteable", version) -> isNoteable = line.getWikiBool()
        line.checkWikiKey("placeholder", version) -> hasPlaceHolder = line.getWikiBool()
        line.checkWikiKey("alchable", version) -> isAlchable = line.getWikiBool()
        line.checkWikiKey("destroy", version) -> destroy = line.getWikiString()
        line.checkWikiKey("value", version) -> valuePrice = line.getWikiInt()
        line.checkWikiKey("store", version) ->  {
            if(line.getWikiString().equals("No", ignoreCase = true)
                || line.contains("Not sold", ignoreCase = true)
            ) {
                storePrice = null
            } else {
                val prices = line.getWikiStrings()
                storePrice = prices?.map { it.replace(",", "").replace(" ", "").toInt() }
            }
        }

        line.checkWikiKey("seller", version) -> seller = line.getWikiString()
        line.checkWikiKey("weight", version) -> weight = line.getWikiFloat()
        line.checkWikiKey("examine", version) -> examine = line.getWikiString()
        line.checkWikiKey("exchange", version) -> onExchange =
            !line.getWikiString().equals("No", ignoreCase = true)
        line.checkWikiKey("slot", version) -> slot = line.getWikiString()
        line.checkWikiKey("astab", version) -> attBonusStab = line.getWikiInt()
        line.checkWikiKey("aslash", version) -> attBonusSlash = line.getWikiInt()
        line.checkWikiKey("acrush", version) -> attBonusCrush = line.getWikiInt()
        line.checkWikiKey("amagic", version) -> attBonusMagic = line.getWikiInt()
        line.checkWikiKey("arange", version) -> attBonusRange = line.getWikiInt()
        line.checkWikiKey("dstab", version) -> defBonusStab = line.getWikiInt()
        line.checkWikiKey("dslash", version) -> defBonusSlash = line.getWikiInt()
        line.checkWikiKey("dcrush", version) -> defBonusCrush = line.getWikiInt()
        line.checkWikiKey("dmagic", version) -> defBonusMagic = line.getWikiInt()
        line.checkWikiKey("drange", version) -> defBonusRange = line.getWikiInt()
        line.checkWikiKey("str", version) -> strengthBonus = line.getWikiInt()
        line.checkWikiKey("rstr", version) -> rangeStrengthBonus = line.getWikiInt()
        line.checkWikiKey("mdmg", version) -> magicDamageBonus = line.getWikiInt()
        line.checkWikiKey("prayer", version) -> prayerBonus = line.getWikiInt()
        else -> { }
    }

    companion object : WikiConfigCompanion() {
        override val queryString = "item"
    }
}
