package org.baktra.dtblib;


class DTBUtils {

    static int[] getAsIntArray(byte[] byteArray) {
        return getAsIntArray(byteArray, byteArray.length);
    }

    static int[] getAsIntArray(byte[] byteArray, int numBytes) {

        int[] intArray = new int[numBytes];

        byte b;

        for (int i = 0; i < numBytes; i++) {
            /*Orezani znamenka*/
            b = byteArray[i];
            intArray[i] = (b < 0) ? b + 256 : b;
        }

        return intArray;
    }
    
}
