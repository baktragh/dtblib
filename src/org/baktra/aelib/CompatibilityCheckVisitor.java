package org.baktra.aelib;

/**
 *
 * @author  
 */
public interface CompatibilityCheckVisitor {

    /**
     *
     * @param dtb
     * @return
     */
    public boolean isCompatible(AtariExecutable dtb);

}
