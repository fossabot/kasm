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

import java.io.File
import java.net.URL
import java.net.URLClassLoader


/**
 * Represents a JVM Classloader which can add classes from raw bytecode data.
 */
class BytecodeClassLoader(parent: ClassLoader) : URLClassLoader(
        buildClasspath(System.getProperty("java.class.path")).toTypedArray(),
        parent
) {

    private val classBytes = hashMapOf<String, ByteArray>()

    fun addClass(name: String, bytes: ByteArray) {
        this.classBytes[name] = bytes
    }

    override fun findClass(name: String): Class<*> {
        val bytes = this.classBytes[name]

        return if(bytes != null) {
            defineClass(name, bytes, 0, bytes.size)
        } else {
            super.findClass(name)
        }
    }

    companion object {

        private fun buildClasspath(classpath: String): List<URL> {
            if(classpath.trim().isEmpty()) return emptyList()

            val urls = mutableListOf<URL>()

            var pos: Int
            var cp = classpath
            while(cp.indexOf(File.pathSeparatorChar).also { pos = it } > -1) {
                val part = cp.substring(0, pos)

                if(part.trim().isNotEmpty()) {
                    urls.add(File(part).toURI().toURL())
                }

                cp = cp.substring(pos + 1)
            }

            if(cp.trim().isNotEmpty()) {
                urls.add(File(cp).toURI().toURL())
            }

            return urls.toList()
        }
    }
}