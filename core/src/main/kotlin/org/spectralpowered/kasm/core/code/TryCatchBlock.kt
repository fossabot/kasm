/*
 * Copyright (C) 2020 Kyle Escobar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.spectralpowered.kasm.core.code

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type

/**
 * Represents a Java try-catch block belonging to a method.
 *
 * @property code Code
 * @property start The label this block starts at.
 * @property end The label this block ends at.
 * @property handler The label the catch handler starts at.
 * @property type The ASM type of the exception being caught.
 * @constructor
 */
class TryCatchBlock(
    val code: Code,
    val start: Label,
    val end: Label,
    val handler: Label,
    val type: Type?
) {

    /**
     * Makes a provided visitor visit this try-catch block object.
     *
     * @param visitor MethodVisitor
     */
    fun accept(visitor: MethodVisitor) {
       visitor.visitTryCatchBlock(start.label, end.label, handler.label, type?.internalName)
    }

    override fun toString(): String {
        return "TRYCATCH"
    }
}