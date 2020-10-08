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

import java.util.HashSet;
import java.util.Set;

abstract public class Walrus {

    private String name;

    private Set<WalrusFood> stomach = new HashSet<WalrusFood>();

    public Walrus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Boolean hasEaten(WalrusFood food) {
        return stomach.contains(food);
    }

    public void eat(Food food) {
        if(!(food instanceof WalrusFood)) {
            this.puke();
        } else {
            stomach.add((WalrusFood) food);
        }
    }

    public void puke() {
        stomach.clear();
    }
}
