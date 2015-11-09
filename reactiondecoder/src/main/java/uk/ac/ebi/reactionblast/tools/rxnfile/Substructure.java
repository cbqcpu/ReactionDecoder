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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.openscience.cdk.interfaces.IBond;

/**
 *
 * @author Syed Asad Rahman <asad @ ebi.ac.uk>
 */
public class Substructure {

    private final String typeID;
    private int sgroupIdentifier;
    private final int groupIndex;
    private final List<SuperAtoms> superAtoms;
    private final List<SuperBonds> superBonds;
    private int count;
    private int superBondIndex;

    /**
     *
     * @param groupIndex index of the super atom group type
     * @param typeID eg SUP
     */
    public Substructure(int groupIndex, String typeID) {
        this.typeID = typeID;
        this.groupIndex = groupIndex;
        this.superAtoms = new ArrayList<>();
        this.superBonds = new ArrayList<>();
        this.count = 0;
        this.superBondIndex = 0;
    }

    public void add(SuperAtoms superAtom) {
        this.superAtoms.add(count++, superAtom);
    }

    public boolean remove(SuperAtoms superAtoms) {
        return this.superAtoms.remove(superAtoms);
    }

    public int getSuperAtomCount() {
        return this.superAtoms.size();
    }

    public int getSuperBondCount() {
        return this.superBonds.size();
    }

    /**
     * @return the sgroupIdentifier
     */
    public String getTypeID() {
        return typeID;
    }

    /**
     * @return the groupIndex
     */
    public int getIndex() {
        return groupIndex;
    }

    void addCrossingBond(int bondIndex, IBond bond) {
        superBonds.add(superBondIndex++, new SuperBonds(bondIndex, bond));
    }

    /**
     * super atoms.
     *
     * @return super atoms
     */
    public Collection<SuperAtoms> atoms() {
        return Collections.unmodifiableCollection(superAtoms);
    }

    /**
     *
     * @return
     */
    public Collection<SuperBonds> bonds() {
        return Collections.unmodifiableCollection(superBonds);
    }

    public SuperBonds getSuperBond(int i) {
        return superBonds.get(i);
    }

    public SuperAtoms getSuperAtom(int i) {
        return superAtoms.get(i);
    }

    boolean setCrossingVector(IBond superBond, double x, double y) {

        boolean contains = false;

        for (SuperBonds sb : superBonds) {
            if (sb.getSuperBond().equals(superBond)) {
                sb.setX(x);
                sb.setY(y);
                contains = true;
            }
        }
        return contains;
    }

    /**
     * @return the sgroupIdentifier
     */
    public int getSGroupIdentifier() {
        return sgroupIdentifier;
    }

    /**
     * @param sgroupIdentifier the sgroupIdentifier to set
     */
    public void setSGroupIdentifier(int sgroupIdentifier) {
        this.sgroupIdentifier = sgroupIdentifier;
    }
}