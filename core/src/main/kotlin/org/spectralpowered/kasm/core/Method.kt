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

package org.spectralpowered.kasm.core

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type
import org.spectralpowered.kasm.core.code.*
import org.spectralpowered.kasm.core.code.instruction.FieldInstruction
import org.spectralpowered.kasm.core.code.instruction.IncInstruction
import org.spectralpowered.kasm.core.code.instruction.IntInstruction
import org.objectweb.asm.Label as AsmLabel

/**
 * Represents a Java method which belongs in a class file.
 *
 * @property pool ClassPool
 */
class Method(val owner: ClassFile) : MethodVisitor(ASM9) {

    /**
     * The pool this method's owner belongs in.
     */
    lateinit var pool: ClassPool

    /**
     * The name of the methods.
     */
    lateinit var name: String

    /**
     * The descriptor of the method.
     */
    lateinit var desc: String

    /**
     * The bit-packed access flags of this method.
     */
    var accessFlags: Int = 0

    /**
     * Primary constructor.
     *
     * @param owner ClassFile
     * @param access Int
     * @param name String
     * @param desc String
     * @constructor
     */
    constructor(
            owner: ClassFile,
            access: Int,
            name: String,
            desc: String
    ) : this(owner) {
        this.pool = owner.pool
        this.accessFlags = access
        this.name = name
        this.desc = desc
    }

    /**
     * The code block for the method.
     */
    var code: Code = Code(this)

    /**
     * The ASM [Type] of this method.
     */
    val type get() = Type.getMethodType(desc)

    /**
     * The return ASM [Type] of this object.
     */
    val returnType get() = type.returnType

    /**
     * The method argument ASM [Type]s
     */
    val argTypes get() = type.argumentTypes

    /*
     * VISITOR METHODS
     */

    override fun visitCode() {
        /*
         * Nothing to do.
         */
    }

    override fun visitTryCatchBlock(
        start: AsmLabel,
        end: AsmLabel,
        handler: AsmLabel,
        type: String?
    ) {
        code.tryCatchBlocks.add(TryCatchBlock(
            code,
            code.findLabel(start),
            code.findLabel(end),
            code.findLabel(handler),
            type?.let { Type.getObjectType(it) }
        ))
    }

    override fun visitLabel(label: AsmLabel) {
        code.add(code.findLabel(label))
    }

    override fun visitLineNumber(line: Int, start: AsmLabel) {
        code.add(LineNumber(code, line, code.findLabel(start)))
    }

    override fun visitInsn(opcode: Int) {
        code.add(Instruction(code, opcode))
    }

    override fun visitIntInsn(opcode: Int, operand: Int) {
        code.add(IntInstruction(code, opcode, operand))
    }

    override fun visitIincInsn(slot: Int, increment: Int) {
        code.add(IncInstruction(code, slot, increment))
    }

    override fun visitFieldInsn(opcode: Int, owner: String, name: String, desc: String) {
        code.add(FieldInstruction(code, opcode, owner, name, desc))
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        code.maxStack = maxStack
        code.maxLocals = maxLocals
    }

    override fun visitEnd() {
        /*
         * Nothing to do.
         */
    }

    /**
     * Makes a provided visitor visit this method.
     *
     * @param visitor MethodVisitor
     */
    fun accept(visitor: MethodVisitor) {
        /*
         * Visit the code object
         */
        code.accept(visitor)

        /*
         * Visit the end
         */
        visitor.visitEnd()
    }

    override fun toString(): String {
        return "$owner.$name$desc"
    }
}