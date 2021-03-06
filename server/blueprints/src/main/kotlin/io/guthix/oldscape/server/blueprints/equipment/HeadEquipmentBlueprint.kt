/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.blueprints.equipment

import io.guthix.oldscape.cache.config.ObjectConfig

class ExtraHeadConfig(
    ids: List<Int>,
    weight: Float,
    examine: String,
    val coversFace: Boolean = false,
    val coversHair: Boolean = false,
    equipment: EquipmentBlueprint.Equipment
) : ExtraEquipmentConfig(ids, weight, examine, equipment)

class HeadEquipmentBlueprint(
    cacheConfig: ObjectConfig,
    override val extraConfig: ExtraHeadConfig
) : EquipmentBlueprint(cacheConfig, extraConfig) {
    val coversHair get() = extraConfig.coversHair
    val coversFace get() = extraConfig.coversFace
}