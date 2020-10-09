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

import org.spectralpowered.kasm.core.Method
import org.objectweb.asm.Label as AsmLabel

/**
 * Represents the bytecode instructions which belong in a method.
 *
 * @property method Method
 * @constructor
 */
class Code(val method: Method) : Iterable<Instruction> {

    /**
     * The cached list of instruction to speed up getting instruction indexes.
     */
    private var cache = listOf<Instruction>()

    /**
     * A map to ASM labels for the spectral kasm labels.
     */
    private val labelMap = mutableMapOf<AsmLabel, Label>()

    /**
     * The maximum number of stack entries in the code object.
     */
    var maxStack = 0

    /**
     * The maximum number of entries in the local variable table
     */
    var maxLocals = 0

    /**
     * The number of instructions in this code object list.
     */
    var size: Int = 0
        private set

    /**
     * The first instruction in the code.
     */
    lateinit var first: Instruction

    /**
     * The last instruction in the code.
     */
    lateinit var last: Instruction

    /**
     * Gets a [List] of [Instruction] objects contained in this code object.
     *
     * @return List<Instruction>
     */
    fun toList(): List<Instruction> {
        val list = mutableListOf<Instruction>()
        var index = 0
        var current: Instruction? = first
        while(current != null) {
            list[index] = current
            current.index = ++index
            current = current.next
        }

        return list
    }

    /**
     * Gets the index of a provided [Instruction].
     *
     * @param insn Instruction
     * @return Int
     */
    fun indexOf(insn: Instruction): Int {
        if(cache.isEmpty()) {
            cache = this.toList()
        }

        return insn.index
    }

    /**
     * Gets whether this code object contains a provided instruction.
     *
     * @param insn Instruction
     * @return Boolean
     */
    fun contains(insn: Instruction): Boolean {
        var current: Instruction? = first
        while(current != null && current != insn) {
            current = current.next
        }

        return current == insn
    }

    /**
     * Gets an instruction at a given index in this code object.
     *
     * @param index Int
     * @return Instruction
     */
    operator fun get(index: Int): Instruction {
        if(index < 0 || index >= size) {
            throw IndexOutOfBoundsException()
        }

        if(cache.isEmpty()) {
            cache = this.toList()
        }

        return cache[index]
    }

    /**
     * Gets the list iterator of this object.
     *
     * @return Iterator<Instruction>
     */
    override fun iterator(): Iterator<Instruction> {
        return toList().iterator()
    }

    /**
     * Adds an instruction to the end of the code list.
     *
     * @param insn Instruction
     */
    fun add(insn: Instruction) {
       ++size

        if(!::last.isInitialized) {
            first = insn
            last = insn
        } else {
            last.next = insn
            insn.prev = last
        }

        last = insn
        cache = listOf()
        insn.index = 0
    }

    /**
     * Inserts an instruction at the start of the code list.
     *
     * @param insn Instruction
     */
    fun insert(insn: Instruction) {
        ++size

        if(!::first.isInitialized) {
            first = insn
            last = insn
        } else {
            first.prev = insn
            insn.next = first
        }

        first = insn
        cache = listOf()
        insn.index = 0
    }

    /**
     * Removes a given [Instruction] from the code list.
     *
     * @param insn Instruction
     */
    fun remove(insn: Instruction) {
        --size
        val next = insn.next
        val prev = insn.prev
        if(next == null) {
            if(prev != null) {
                prev.next = null
                last = prev
            }
        } else {
            if(prev == null) {
                first = next
                next.prev = null
            } else {
                prev.next = next
                next.prev = prev
            }
        }

        cache = listOf()
        insn.index = -1
        insn.prev = null
        insn.next = null
    }

    /**
     * Perform an action for each instruction in the code object list.
     *
     * @param action Function1<Instruction, Unit>
     */
    fun forEach(action: (Instruction) -> Unit) {
        val it = this.iterator()
        while(it.hasNext()) {
            action(it.next())
        }
    }

    /**
     * Finds a [Label] from the label map. if nothing is found, a new label instance is created.
     *
     * @param label Label
     * @return Label
     */
    fun findLabel(label: AsmLabel): Label {
        var found = labelMap[label]
        if(found == null) {
            found = Label(this, label)
            labelMap[label] = found
        }

        /*
         * Update the label id
         */
        val index = labelMap.values.indexOf(found)
        found.id = index

        return found
    }
}