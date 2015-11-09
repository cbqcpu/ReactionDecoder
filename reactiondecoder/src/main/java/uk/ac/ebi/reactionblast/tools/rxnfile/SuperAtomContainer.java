/* Copyright (C) 2011  Syed Asad Rahman <asad @ ebi.ac.uk>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package uk.ac.ebi.reactionblast.tools.rxnfile;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Syed Asad Rahman <asad @ ebi.ac.uk>
 */
public class SuperAtomContainer {

    private final List<Substructure> list;
    private int counter;

    public SuperAtomContainer(int size) {
        this.counter = 0;
        list = new ArrayList<>(size);
    }

    public Substructure getSubstructures(int groupIndex) {
        return list.get(groupIndex);
    }

    public void add(Substructure substructure) {
        list.add(counter++, substructure);
    }

    public int countSuperatoms() {
        return list.size();
    }

    void clear() {
        list.clear();
    }

    /**
     * @return the counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }
}