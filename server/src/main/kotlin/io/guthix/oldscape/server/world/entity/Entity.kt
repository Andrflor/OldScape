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
package io.guthix.oldscape.server.world.entity

import io.guthix.oldscape.server.dimensions.tiles
import io.guthix.oldscape.server.world.mapsquare.zone.tile.Tile
import kotlin.reflect.KProperty

abstract class Entity(open val position: Tile, open val attributes: MutableMap<KProperty<*>, Any?>) {
    open val sizeX get() = size

    open val sizeY get() = size

    open var orientation: Int = 0

    val size = 1.tiles // TODO
}