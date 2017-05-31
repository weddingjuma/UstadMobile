package com.ustadmobile.port.sharedse.persistence.proxy;
/**
 * Created by varuna on 5/29/2017.
 */

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * This is an intermediary class for School<->Holiday m2m relationship
 */
interface SchoolHolidays extends NanoLrsModel {
    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    void setUUID(String uuid);

    //TODO: this
}