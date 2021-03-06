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
package com.ustadmobile.core.view;

import com.ustadmobile.core.controller.ContainerController;

/**
 *
 * @author mike
 */
public interface ContainerView extends UstadView {

    public static final String VIEW_NAME = "Container";

    public void setController(ContainerController controller);
        
    public void setContainerTitle(String containerTitle);

    void setPageTitle(String pageTitle);

    /**
     * The content is an EPUB - show the EPUB
     */
    public void showEPUB();
    
    /**
     * Orders the view to look again at the URLs of each page.  This is used for
     * instance when the registration is changed... thus changing the URL of each
     * page.
     * 
     * @return true if successfully executed, false otherwise
     */
    public boolean refreshURLs();


    
}
