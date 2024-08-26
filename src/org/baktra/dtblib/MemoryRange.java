package org.baktra.dtblib;

/**
 *
 * DOS 2 Binary File memory range, immutable
 */
public class MemoryRange {
    
    public final int first;
    public final int last;
    
    public MemoryRange(int first,int last) {
        this.first=first;
        this.last=last;
    }
    
}
