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


/**
 * Represents a JVM Classloader which can add classes from raw bytecode data.
 */
class BytecodeClassLoader(parent: ClassLoader) : ClassLoader(parent) {

    private val byteDataMap = hashMapOf<String, ByteArray>()

    fun addClass(name: String, bytes: ByteArray) {
        byteDataMap[name] = bytes
    }

    override fun loadClass(name: String): Class<*> {
        if(byteDataMap.isEmpty()) {
            throw ClassNotFoundException()
        }

        val fileName = name.replace("\\.", "/") + ".class"
        val bytes = byteDataMap[fileName] ?: throw ClassNotFoundException("Class data for name $fileName not found.")

        return defineClass(name, bytes, 0, bytes.size)
    }
}