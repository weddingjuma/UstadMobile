package com.ustadmobile.port.sharedse.network;

/**
 * Created by kileha3 on 05/03/2017.
 */

public interface P2PFileDownloadListener {

    void onDownloadStatusChange(boolean isDownloadCompleted,String reason);
}