/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.api.blueprint.ObjectBlueprint
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import kotlin.reflect.KProperty

class Obj(
    override val position: Tile,
    val blueprint: ObjectBlueprint,
    override val attributes: MutableMap<KProperty<*>, Any?> = mutableMapOf()
) : Entity(position, attributes)