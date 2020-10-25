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

package org.spectral.kasm.core

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.ASM9

/**
 * Represents a Java class file's bytecode model.
 *
 * @constructor
 */
class ClassFile : ClassVisitor(ASM9) {

    /**
     * The pool this class file belongs to.
     */
    lateinit var pool: ClassPool

    /**
     * The class name.
     */
    lateinit var name: String

    /**
     * The bit-packed access flags.
     */
    var accessFlags: Int = 0

    /**
     * JVM version this class was compiled with.
     */
    var version: Int = 0

    /**
     * The name of the source class file.
     */
    lateinit var sourceFile: String

    /**
     * The Name of the extended class or parent class.
     */
    lateinit var superClass: String

    /**
     * The class names that this implements
     */
    var interfaces = mutableListOf<String>()

    /**
     * The ASM object [Type].
     */
    val type: Type get() = Type.getObjectType(name)

    /**
     * The methods contained in this class.
     */
    var methods = mutableListOf<Method>()

    /**
     * The fields contained in this class.
     */
    var fields = mutableListOf<Field>()

    /**
     * Finds a [Method] in this class by name and descriptor.
     *
     * @param name String
     * @param desc String
     * @return Method?
     */
    fun findMethod(name: String, desc: String): Method? {
        return this.methods.firstOrNull { it.name == name && it.desc == desc }
    }

    /**
     * Finds a [Field] in this class by name and descriptor.
     *
     * @param name String
     * @param desc String
     * @return Field?
     */
    fun findField(name: String, desc: String): Field? {
        return this.fields.firstOrNull { it.name == name && it.desc == desc }
    }

    /**
     * Converts this class file to bytecode as raw [ByteArray] data.
     *
     * @return ByteArray
     */
    fun toByteCode(): ByteArray {
        val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
        this.accept(writer)
        return writer.toByteArray()
    }

    /*
     * VISITOR METHODS
     */

    override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String,
            interfaces: Array<String>
    ) {
        this.version = version
        this.accessFlags = access
        this.name = name
        this.superClass = superName
        this.interfaces = interfaces.toMutableList()
    }

    override fun visitSource(source: String, debug: String?) {
        this.sourceFile = source
    }

    override fun visitMethod(
            access: Int,
            name: String,
            desc: String,
            signature: String?,
            exceptions: Array<String>?
    ): MethodVisitor {
        val method = Method(this, access, name, desc)
        methods.add(method)
        return method
    }

    override fun visitField(
            access: Int,
            name: String,
            desc: String,
            signature: String?,
            value: Any?
    ): FieldVisitor {
        val field = Field(this, access, name, desc, value)
        fields.add(field)
        return field
    }

    override fun visitEnd() {
        /*
         * Nothing to do
         */
    }

    /**
     * Makes a provided visitor visit this class.
     *
     * @param visitor ClassVisitor
     */
    fun accept(visitor: ClassVisitor) {
        /*
         * Visit the general information.
         */
        visitor.visit(this.version, this.accessFlags, this.name, null, this.superClass, this.interfaces.toTypedArray())

        /*
         * Visit the source file.
         */
        visitor.visitSource(this.sourceFile, null)

        /*
         * Visit each method.
         */
        methods.forEach { method ->
            val mv = visitor.visitMethod(method.accessFlags, method.name, method.desc, null, arrayOf())
            method.accept(mv)
        }

        /*
         * Visit each field
         */
        fields.forEach { field ->
            val fv = visitor.visitField(field.accessFlags, field.name, field.desc, null, field.value)
            field.accept(fv)
        }

        /*
         * Visit the end.
         */
        visitor.visitEnd()
    }

    override fun toString(): String {
        return name
    }
}