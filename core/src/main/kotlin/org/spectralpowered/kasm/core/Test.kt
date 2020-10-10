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

import java.applet.Applet
import java.applet.AppletContext
import java.applet.AppletStub
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import javax.swing.JFrame

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val pool = ClassPool()
        pool.addJar(File("gamepack.jar"))
        pool.saveJar(File("gamepack-out.jar"))

        TestClient.start(File("gamepack-out.jar"))
    }
}

object TestClient {

    fun start(file: File) {
        val frame = JFrame("Test Client")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setLocationRelativeTo(null)
        frame.layout = GridLayout(1, 0)
        frame.add(createApplet(file))
        frame.pack()
        frame.isVisible = true
    }

    private fun crawl(): Map<String, String> {
        val params = hashMapOf<String, String>()
        val lines = URL("http://oldschool1.runescape.com/jav_config.ws").readText().split("\n")

        lines.forEach {
            var line = it
            if(line.startsWith("param=")) {
                line = line.substring(6)
            }

            val idx = line.indexOf("=")
            if(idx >= 0) {
                params[line.substring(0, idx)] = line.substring(idx + 1)
            }
        }

        return params
    }

    private fun createApplet(file: File): Applet {
        val params = crawl()
        val classloader = URLClassLoader(arrayOf(file.toURI().toURL()))
        val main = params["initial_class"]!!.replace(".class", "")
        val applet = classloader.loadClass(main).getDeclaredConstructor().newInstance() as Applet
        applet.background = Color.BLACK
        applet.preferredSize = Dimension(params["applet_minwidth"]!!.toInt(), params["applet_minheight"]!!.toInt())
        applet.size = applet.preferredSize
        applet.layout = null
        applet.setStub(createStub(params, applet))
        applet.isVisible = true
        applet.init()
        return applet
    }

    private fun createStub(params: Map<String, String>, applet: Applet): AppletStub {
        return object : AppletStub {
            override fun getCodeBase(): URL = URL(params["codebase"])
            override fun getDocumentBase(): URL = URL(params["codebase"])
            override fun isActive(): Boolean = true
            override fun getParameter(name: String): String? = params[name]
            override fun getAppletContext(): AppletContext? = null
            override fun appletResize(width: Int, height: Int) {
                applet.size = Dimension(width, height)
            }
        }
    }
}