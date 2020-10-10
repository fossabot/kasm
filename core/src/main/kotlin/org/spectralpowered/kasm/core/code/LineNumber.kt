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
import org.objectweb.asm.Label as AsmLabel

/**
 * Represents a line number instruction in a method.
 */
class LineNumber(code: Code, val line: Int, val label: Label) : Instruction(code, -1) {

    override fun accept(visitor: MethodVisitor) {
        visitor.visitLineNumber(line, label.label)
    }

    override fun toString(): String {
        return "LINENUMBER:$line"
    }
}