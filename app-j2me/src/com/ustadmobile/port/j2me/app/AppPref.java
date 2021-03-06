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
package com.ustadmobile.port.j2me.app;

import com.ustadmobile.core.util.UMUtil;
import java.io.IOException;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author varuna
 */
public class AppPref {
    
    //Default app settings.
    public static Hashtable appSettings;
    public static String appDataDir = null;
    static final String REC_STORE = "UstadMobileApp";
    
    //Constructor
    public AppPref() {
    }
    
    //set up default settings.
    private static void setDefaultPreferences() {
        Hashtable defaultAppSettings = new Hashtable();
        defaultAppSettings.put("umcloud", "http://umcloud1.ustadmobile.com");
        defaultAppSettings.put("tincan", 
                "http://umcloud1.ustadmobile.com/umlrs");
        defaultAppSettings.put("mboxsuffix", "@ustadmobile.com");
        defaultAppSettings.put("launched", 
                "http://adlnet.gov/expapi/verbs/launched");
        defaultAppSettings.put("opds", "http://umcloud1.ustadmobile.com/opds/");
        defaultAppSettings.put("opdspublic", 
                "http://umcloud1.ustadmobile.com/opds/public/");
        defaultAppSettings.put("lesson", 
                "http://adlnet.gov/expapi/activities/lesson");
        appSettings = new Hashtable();
        appSettings = defaultAppSettings;
    }
    
    public static void updateSetting(String key, String newValue){
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        
        //Get Current configuration
        Hashtable currentSettings = getAppSettings();
        if(currentSettings.containsKey(key)){
            currentSettings.remove(key);
            currentSettings.put(key, newValue);
            
            //Put it back in
            
            //Clear it, Close it
            appRms.deleteRMS();
            appRms.closeRMS();
            
            //Open it again
            appRms.openRMS();
            
            //Generate bytes
            byte[] newSettingsBytes = 
                    SerializedHashtable.hashTabletoStream(currentSettings);
            
            //Insert the data in.
            appRms.insertBytes(newSettingsBytes);
            
            
            
        }
        //close the app RMS
        appRms.closeRMS();
        
    }
    
    public static void addSetting(String key, String newValue){
        if(newValue == null){
            deleteSetting(key);
        }else{
            //Initiate app RMS..
            RMSUtils appRms = new RMSUtils(REC_STORE);

            //Get Current configuration
            Hashtable currentSettings = getAppSettings();
            if(currentSettings.containsKey(key)){
                currentSettings.remove(key);
                currentSettings.put(key, newValue);

                //Put it back in

                //Clear it, Close it
                appRms.deleteRMS();
                appRms.closeRMS();

                //Open it again
                appRms.openRMS();

                //Generate bytes
                byte[] newSettingsBytes = 
                        SerializedHashtable.hashTabletoStream(currentSettings);

                //Insert the data in.
                appRms.insertBytes(newSettingsBytes);



            }else{
                currentSettings.put(key, newValue);
                //Clear it, Close it
                appRms.deleteRMS();
                appRms.closeRMS();

                //Open it again
                appRms.openRMS();

                //Generate bytes
                byte[] newSettingsBytes = 
                        SerializedHashtable.hashTabletoStream(currentSettings);

                //Insert the data in.
                appRms.insertBytes(newSettingsBytes);

            }
            //close the app RMS
            appRms.closeRMS();
        }
    }
    
    public static void deleteSetting(String key){
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        
        //Get Current configuration
        Hashtable currentSettings = getAppSettings();
        if(currentSettings.containsKey(key)){
            currentSettings.remove(key);
            
            //Clear it, Close it
            appRms.deleteRMS();
            appRms.closeRMS();
            
            //Open it again
            appRms.openRMS();
            
            //Generate bytes
            byte[] newSettingsBytes = 
                    SerializedHashtable.hashTabletoStream(currentSettings);
            
            //Insert the data in.
            appRms.insertBytes(newSettingsBytes);
      
        }
        //close the app RMS
        appRms.closeRMS();
    }
    
    public static String getSetting(String key){
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        String value = null;
        //Get Current configuration
        Hashtable currentSettings = getAppSettings();
        if(currentSettings.containsKey(key)){
            value = currentSettings.get(key).toString();
        }
        //close the app RMS
        appRms.closeRMS();
        return value;
    }
    
    public static String[] getAllKeys(){
        RMSUtils appRms = new RMSUtils(REC_STORE);
        Hashtable curretSettings = getAppSettings();
        return UMUtil.enumerationToStringArray(curretSettings.keys());
    }
    
    public static Hashtable getAppSettings(){
        
        //getDefault values
        setDefaultPreferences();
        
        //Initiate app RMS..
        RMSUtils appRms = new RMSUtils(REC_STORE);
        
        //Check if there is anything..
        appRms.openRMS();
        byte[] appSettingsByteArrayRMS = appRms.readBytes();
        Hashtable appSettingsRMS = SerializedHashtable.streamToHashtable
                (appSettingsByteArrayRMS);
        //appRms.closeRMS();
        
        if (appSettingsRMS.isEmpty() || appSettingsRMS.size() < appSettings.size()){
            //wipe it.
            appRms.deleteRMS();
            
            //default hashtable to bytearray
            byte[] appSettingsByteArray = 
                SerializedHashtable.hashTabletoStream(appSettings);
            
            //load it with the default.
            appRms.insertBytes(appSettingsByteArray);
            
        }else{
            appSettings.clear();
            appSettings=appSettingsRMS;
            //appSettings.equals(appSettingsRMS);
        }
        
        //close the app RMS
        appRms.closeRMS();
        return appSettings;
    }
    
   
    
    public static String getPlatform(){
        return System.getProperty("microedition.platform");
    }
    
    public static String getLocale(){
        return System.getProperty("microedition.locale");
    }

}
