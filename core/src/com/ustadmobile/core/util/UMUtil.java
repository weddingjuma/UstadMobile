/*
    This file is part of Ustad Mobile.

    Ustad Mobile Copyright (C) 2011-2014 UstadMobile Inc.

    Ustad Mobile is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version with the following additional terms:

    All names, links, and logos of Ustad Mobile and Toughra Technologies FZ
    LLC must be kept as they are in the original distribution.  If any new
    screens are added you must include the Ustad Mobile logo as it has been
    used in the original distribution.  You may not create any new
    functionality whose purpose is to diminish or remove the Ustad Mobile
    Logo.  You must leave the Ustad Mobile logo as the logo for the
    application to be used with any launcher (e.g. the mobile app launcher).

    If you want a commercial license to remove the above restriction you must
    contact us.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Ustad Mobile is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

 */
package com.ustadmobile.core.util;

import java.util.Vector;

/**
 * Misc utility methods
 * 
 * @author mike
 */
public class UMUtil {
    
    /**
     * Gets the index of a particular item in an array
     * 
     * Needed because J2ME does not support the Collections framework
     * 
     * @param obj
     * @param arr
     * @return 
     */
    public static final int getIndexInArray(Object obj, Object[] arr) {
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] != null && arr[i].equals(obj)) {
                return i;
            }
        }
        
        return -1;
    }
    
    public static final String[] filterArrByPrefix(String[] arr, String prefix) {
        boolean[] matches = new boolean[arr.length];
        
        int i;
        int matchCount = 0;
        int arrayLen = arr.length;
        
        for(i = 0; i < arrayLen; i++) {
            if(arr[i] != null && arr[i].startsWith(prefix)) {
                matches[i] = true;
                matchCount++;
            }
        }
        
        String[] retVal = new String[matchCount];
        matchCount = 0;
        for(i = 0; i < arrayLen; i++) {
            if(matches[i]) {
                retVal[matchCount] = arr[i];
                matchCount++;
            }
        }
        
        return retVal;
    }
    
    /**
     * Utility method to fill boolean array with a set value
     * 
     * @param arr The boolean array
     * @param value Value to put in
     * @param from starting index (inclusive)
     * @param to  end index (exclusive)
     */
    public static void fillBooleanArray(boolean[] arr, boolean value, int from, int to) {
        for(int i = from; i < to; i++) {
            arr[i] = value;
        }
    }
    
    
}
