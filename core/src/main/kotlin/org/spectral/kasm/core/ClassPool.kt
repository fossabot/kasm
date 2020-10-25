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

import org.objectweb.asm.ClassReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * Represents a collection of [ClassFile] objects.
 */
class ClassPool {

    /**
     * The map of classes in this pool object.
     */
    private val classMap = LinkedHashMap<String, ClassFile>()

    /**
     * The number of classes in the pool.
     */
    val size: Int get() = classMap.size

    /**
     * Adds a class to the pool from the raw bytecode.
     *
     * @param bytes ByteArray
     * @return ClassFile
     */
    fun addClass(bytes: ByteArray): ClassFile {
        val reader = ClassReader(bytes)
        val classFile = ClassFile().apply { this.pool = this@ClassPool }

        reader.accept(classFile, ClassReader.SKIP_FRAMES)

        return addClass(classFile)
    }

    /**
     * Adds a class file to the pool.
     *
     * @param element ClassFile
     * @return ClassFile
     */
    fun addClass(element: ClassFile): ClassFile {
        element.pool = this
        classMap[element.name] = element
        return element
    }

    /**
     * Adds all the classes inside a jar file to the pool.
     *
     * @param file File
     */
    fun addJar(file: File) {
        if(!file.exists()) {
            throw FileNotFoundException("Unable to located Jar file: ${file.path}")
        }

       JarFile(file).use { jar ->
           jar.entries().asSequence()
                   .filter { it.name.endsWith(".class") }
                   .forEach { entry ->
                       this.addClass(jar.getInputStream(entry).readAllBytes())
                   }
       }
    }

    /**
     * Saves the current class pool to a Jar file.
     *
     * @param file File
     */
    fun saveJar(file: File) {
        if(file.exists()) {
            file.delete()
        }

        val jos = JarOutputStream(FileOutputStream(file))

        forEach { classFile ->
            val entry = JarEntry(classFile.name + ".class")
            val bytes = classFile.toByteCode()

            jos.putNextEntry(entry)
            jos.write(bytes)
            jos.closeEntry()
        }

        jos.close()
    }

    /**
     * Creates a class loader from the classes inside of the current
     * class pool.
     *
     * @return ClassLoader
     */
    fun createClassloader(): ClassLoader {
        val classloader = BytecodeClassLoader(ClassLoader.getSystemClassLoader())

        this.forEach { cls ->
            val bytes = cls.toByteCode()
            classloader.addClass(cls.name, bytes)
        }

        return classloader
    }

    /**
     * Gets a [ClassFile] given a name. Returns null if no class is found with the provided name.
     *
     * @param name String
     * @return ClassFile?
     */
    fun findClass(name: String): ClassFile? {
        return this.classMap[name]
    }

    /**
     * Invokes an action for each [ClassFile] in the pool.
     *
     * @param action Action to invoke
     */
    fun forEach(action: (ClassFile) -> Unit) {
        classMap.values.forEach(action)
    }

    /**
     * Maps each class file in the pool to a provided transform.
     *
     * @param transform Transform applied to each [ClassFile]
     * @return Transformed [List] of elements
     */
    fun <T> map(transform: (ClassFile) -> T): List<T> {
        return classMap.values.map(transform)
    }

    /**
     * Gets the first class file in the pool the predicate returns true for.
     *
     * @param predicate Predicate to match on each [ClassFile]
     * @return Matching [ClassFile] or null of none is found
     */
    fun firstOrNull(predicate: (ClassFile) -> Boolean): ClassFile? {
       return classMap.values.firstOrNull(predicate)
    }

    /**
     * Returns a list of filtered [ClassFile] from the pool which match a predicate.
     *
     * @param predicate Predicate to match
     * @return Filtered [List] of [ClassFile] objects.
     */
    fun filter(predicate: (ClassFile) -> Boolean): List<ClassFile> {
        return classMap.values.filter(predicate)
    }
}