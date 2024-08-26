package org.baktra.dtblib;

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
    public boolean isCompatible(DOS2Binary dtb);

}
