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

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type
import java.lang.reflect.Modifier

/**
 * Represents a Java field.
 *
 * @property owner ClassFile
 * @constructor
 */
class Field(val owner: ClassFile) : FieldVisitor(ASM9) {

    /**
     * The pool this field's owner belongs in.
     */
    lateinit var pool: ClassPool

    /**
     * The name of the field.
     */
    lateinit var name: String

    /**
     * The descriptor string of the field type
     */
    lateinit var desc: String

    /**
     * The bit-packed access flages for this field.
     */
    var accessFlags: Int = 0

    /**
     * The initialized value of this field.
     */
    var value: Any? = null

    /**
     * Creates and initializes values of a field object.
     *
     * @param owner ClassFile
     * @param access Int
     * @param name String
     * @param desc String
     * @param value Any?
     * @constructor
     */
    constructor(
            owner: ClassFile,
            access: Int,
            name: String,
            desc: String,
            value: Any?
    ) : this(owner) {
        this.pool = owner.pool
        this.accessFlags = access
        this.name = name
        this.desc = desc
        this.value = value
    }

    /**
     * The ASM [Type] of this field.
     */
    val type get() = Type.getType(desc)

    /**
     * Whether this field is static or not.
     */
    val isStatic: Boolean get() = Modifier.isStatic(accessFlags)

    /**
     * Whether this field is private.
     */
    val isPrivate: Boolean get() = Modifier.isPrivate(accessFlags)

    /*
     * VISITOR METHODS
     */

    override fun visitEnd() {
        /*
         * Nothing to do
         */
    }

    /**
     * Makes a given visitor visit this field object.
     *
     * @param visitor FieldVisitor
     */
    fun accept(visitor: ClassVisitor) {
        visitor.visitField(accessFlags, name, desc, null, value)
    }

    override fun toString(): String {
        return "$owner.$name"
    }
}