/*
 * Copyright (C) 2007-2017 Syed Asad Rahman <asad @ ebi.ac.uk>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.ebi.reactionblast.signature;

import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import org.openscience.cdk.interfaces.IAtomContainer;
import static uk.ac.ebi.reactionblast.tools.labelling.AtomContainerAtomPermutor.permute;
import uk.ac.ebi.reactionblast.tools.labelling.ICanonicalMoleculeLabeller;

/**
 *
 * @author maclean
 *
 */
public class RBlastAtomSignatureLabellingAdaptor
        implements ICanonicalMoleculeLabeller {

    private static final Logger LOG = getLogger(RBlastAtomSignatureLabellingAdaptor.class.getName());

    private int atomIndex;
    private int height;

    /**
     *
     * @param atomIndex
     */
    public RBlastAtomSignatureLabellingAdaptor(int atomIndex) {
        this.atomIndex = atomIndex;
        this.height = -1;
    }

    /**
     *
     * @param atomIndex
     * @param height
     */
    public RBlastAtomSignatureLabellingAdaptor(int atomIndex, int height) {
        this.atomIndex = atomIndex;
        this.height = height;
    }

    /**
     *
     * @param container
     * @return
     */
    @Override
    public IAtomContainer getCanonicalMolecule(IAtomContainer container) {
        return permute(
                getCanonicalPermutation(container), container);
    }

    /**
     *
     * @param container
     * @return
     */
    @Override
    public int[] getCanonicalPermutation(IAtomContainer container) {
        RBlastMoleculeSignature molSig
                = new RBlastMoleculeSignature(container);
        if (height == -1) {
            return molSig.getAtomSignature(atomIndex)
                    .getCanonicalLabelling(container.getAtomCount());
        } else {
            return molSig.getAtomSignature(atomIndex, height)
                    .getCanonicalLabelling(container.getAtomCount());
        }
    }
}
