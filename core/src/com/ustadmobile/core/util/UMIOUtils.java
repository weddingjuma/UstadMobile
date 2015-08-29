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

 */package com.ustadmobile.core.util;

import com.ustadmobile.core.impl.ZipFileHandle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author mike
 */
public class UMIOUtils {
    
    /**
     * Close the given input stream if not null
     * 
     * @param in An input stream to close
     */
    public static final void closeInputStream(InputStream in) {
        try {
            if(in != null) {
                in.close();
            }
        }catch(IOException e) {
        }
    }
    
    /**
     * Close the given output stream if not null
     * 
     * @param out An input stream to close
     */
    public static final void closeOutputStream(OutputStream out) {
        try {
            if(out != null) {
                out.close();
            }
        }catch(IOException e) {
            
        }
    }
    
    /**
     * Close the given ZipHandle
     */
    public static final void closeZipFileHandle(ZipFileHandle zip) {
        
    }
    
    
}