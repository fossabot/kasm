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

package org.spectral.kasm.core.code.instruction

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.util.Printer
import org.spectral.kasm.core.code.Code
import org.spectral.kasm.core.code.Instruction
import org.spectral.kasm.core.code.Label

class JumpInstruction(code: Code, opcode: Int, var label: Label) : Instruction(code, opcode) {

    override fun accept(visitor: MethodVisitor) {
        visitor.visitJumpInsn(opcode, label.label)
    }

    override fun toString(): String {
        return "${Printer.OPCODES[opcode]} $label"
    }
}