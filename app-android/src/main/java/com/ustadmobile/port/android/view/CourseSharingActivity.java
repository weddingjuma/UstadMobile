package com.ustadmobile.port.android.view;

import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.toughra.ustadmobile.R;
import com.ustadmobile.core.MessageIDConstants;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.networkmanager.AcquisitionListener;
import com.ustadmobile.core.networkmanager.AcquisitionTaskStatus;
import com.ustadmobile.core.opds.UstadJSOPDSFeed;
import com.ustadmobile.core.view.CourseSharingView;
import com.ustadmobile.port.android.netwokmanager.NetworkManagerAndroid;
import com.ustadmobile.port.android.netwokmanager.WifiDirectAutoAccept;
import com.ustadmobile.port.sharedse.networkmanager.NetworkManagerListener;
import com.ustadmobile.port.sharedse.networkmanager.NetworkNode;
import com.ustadmobile.port.sharedse.networkmanager.NetworkTask;
import com.ustadmobile.port.sharedse.networkmanager.ResumableHttpDownload;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CourseSharingActivity extends UstadBaseActivity  implements CourseSharingView,NetworkManagerListener, CompoundButton.OnCheckedChangeListener, AcquisitionListener {

    private RecyclerView deviceRecyclerView;

    private DeviceAdapter deviceAdapter;

    private ProgressBar progressBarWaiting;

    private NetworkManagerAndroid managerAndroid;

    private TextView toolbarTitle,connectedNetworkName,connectedDeviceAddress,receivingFileNameView;

    private CardView connectedDeviceView;

    private ProgressBar fileReceivingProgressBar;

    private UstadMobileSystemImpl impl;

    private static final String DEFAULT_SERVER_IP_ADDRESS ="192.168.49.1";

    private static final String TEMP_FILE_NAME="acquire.opds";

    private File tempFeedFile=null;

    private NetworkNode connectedNode=null;

    private boolean isSharingFiles=false;

    private boolean isSharedFileDialogShown=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_sharing);
        setUMToolbar(R.id.course_sharing_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        impl=UstadMobileSystemImpl.getInstance();

        managerAndroid=((NetworkManagerAndroid)impl.getNetworkManager());

        SwitchCompat switchCompatReceive = (SwitchCompat) findViewById(R.id.receive_share_content);
        deviceRecyclerView = (RecyclerView) findViewById(R.id.all_found_nodes_to_share_course);
        fileReceivingProgressBar = (ProgressBar) findViewById(R.id.fileReceivingProgressBar);
        progressBarWaiting= (ProgressBar) findViewById(R.id.progressBarWaiting);


        toolbarTitle= (TextView) findViewById(R.id.toolbarTitle);
        connectedDeviceAddress= (TextView) findViewById(R.id.device_address);
        connectedNetworkName= (TextView) findViewById(R.id.network_name);
        receivingFileNameView= (TextView) findViewById(R.id.receivingFileNameView);
        connectedDeviceView = (CardView) findViewById(R.id.connected_device_details_holder);

        deviceAdapter=new DeviceAdapter();


        LinearLayoutManager mLayoutManager=new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deviceRecyclerView.setLayoutManager(mLayoutManager);
        deviceRecyclerView.setAdapter(deviceAdapter);
        deviceAdapter.setNodeList(new ArrayList<NetworkNode>());
        managerAndroid.addNetworkManagerListener(this);
        switchCompatReceive.setOnCheckedChangeListener(this);
        managerAndroid.setSharingContent(true);
        switchCompatReceive.setChecked(true);
        new WifiDirectAutoAccept(this).intercept(true);

    }


    @Override
    public void onDestroy() {
        if(managerAndroid!=null){
            managerAndroid.setSharingContent(false);
            if(managerAndroid.isSuperNodeEnabled()){
               managerAndroid.setSuperNodeEnabled(this,true);
            }
        }
        super.onDestroy();
    }

    private void refreshDeviceList(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<NetworkNode> deviceList= new ArrayList<>();
                for(NetworkNode device: managerAndroid.getKnownNodes() ){
                    if(device.getDeviceName()!=null && !deviceList.contains(device)){
                        deviceList.add(device);
                    }
                }
                int visibility=deviceList.size()>0 ? View.GONE: View.VISIBLE;
                progressBarWaiting.setVisibility(visibility);
                deviceAdapter.setNodeList(deviceList);
                deviceAdapter.notifyDataSetChanged();
                deviceRecyclerView.invalidate();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        handleSwitchSelectionChange(isChecked);
    }



    private void handleSwitchSelectionChange(boolean isReceivingContent){

        if(isReceivingContent){
            refreshDeviceList();
        }
        managerAndroid.startContentSharing(impl.getDeviceName(this),isReceivingContent);
        this.isSharingFiles=isReceivingContent;
        String title=isReceivingContent ? impl.getString(MessageIDConstants.chooseDeviceToShareWith):
                impl.getString(MessageIDConstants.receiveSharedCourse);
        int deviceListVisibility=isReceivingContent ? View.VISIBLE: View.GONE;
        deviceRecyclerView.setVisibility(deviceListVisibility);
        managerAndroid.setSuperNodeEnabled(this,!isReceivingContent);
        managerAndroid.setSharingContent(true);
        setToolbarTitle(title);
    }

    @Override
    public void acquisitionProgressUpdate(String entryId, AcquisitionTaskStatus status) {
        int progress=(int)((status.getDownloadedSoFar()*100)/ status.getTotalSize());
        if(status.getDownloadedSoFar()>0){
            setFileTitle(managerAndroid.getEntryAcquisitionTaskMap().get(entryId).entryTitle);
        }
        setReceivingProgress(progress);
    }

    @Override
    public void acquisitionStatusChanged(String entryId, AcquisitionTaskStatus status) {

    }

    @Override
    public void setToolbarTitle(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    public void setFileTitle(String title) {
        receivingFileNameView.setText(title);
    }


    @Override
    public void setViewsVisibility(boolean connected) {
        //connected device view
        int deviceViewVisibility=connected ? View.VISIBLE: View.GONE;
        connectedDeviceView.setVisibility(deviceViewVisibility);

        //Discovered device list
        int deviceListVisibility=connected ? View.GONE:View.VISIBLE;
        deviceRecyclerView.setVisibility(deviceListVisibility);


    }



    @Override
    public void setReceivingProgress(int progress) {
        int visibility=(progress > 0 && progress<=100)? View.VISIBLE : View.GONE;
        fileReceivingProgressBar.setVisibility(visibility);
        receivingFileNameView.setVisibility(visibility);
        fileReceivingProgressBar.setProgress(progress);
    }

    @Override
    public void setConnectedDeviceInfo(String deviceName, String deviceAddress) {
        connectedDeviceAddress.setText(deviceAddress);
        connectedNetworkName.setText(deviceName);
    }

    @Override
    public void handleUserCancelDownloadTask() {
        managerAndroid.getWifiDirectHandler().removeGroup();
    }

    @Override
    public void handleUserStartReceivingTask(UstadJSOPDSFeed feed) {
        managerAndroid.requestAcquisition(feed,this,false,false,true);
    }

    @Override
    public void handleAcquireOPDSFeed() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try{
                    String fileUrl = "http://" + connectedNode.getDeviceIpAddress() + ":"
                            + connectedNode.getPort()+"/catalog/"+TEMP_FILE_NAME;
                    File cDir = getBaseContext().getCacheDir();
                    tempFeedFile = new File(cDir.getPath(),TEMP_FILE_NAME) ;
                    return new ResumableHttpDownload(fileUrl,tempFeedFile.getAbsolutePath()).download();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean isCompleted) {
                super.onPostExecute(isCompleted);
                UstadJSOPDSFeed opdsFeed=null;
                try{
                    InputStream catalogIn = impl.openFileInputStream(tempFeedFile.getAbsolutePath());
                    XmlPullParser parser = impl.newPullParser();
                    parser.setInput(catalogIn, "UTF-8");
                    opdsFeed = new UstadJSOPDSFeed();
                    opdsFeed.loadFromXpp(parser);

                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
                if(isCompleted){
                    showConfirmationDialog(opdsFeed);
                }
            }
        }.execute();
    }

    @Override
    public void showConfirmationDialog(final UstadJSOPDSFeed opdsFeed) {
        String filesList="";
        String message=opdsFeed.entries.length>0? opdsFeed.entries.length+" courses":opdsFeed.entries.length+" course";
        if(opdsFeed.entries.length>1){
            for(int position=0;position<opdsFeed.entries.length;position++){

                if(position==0){
                    filesList="<br/>"+(position+1)+". "+opdsFeed.entries[position].title;
                }
                if(position>0 && position<opdsFeed.entries.length){
                    filesList=filesList+"<br/>"+(position+1)+". "+opdsFeed.entries[position].title;
                }
            }

        }else{
            filesList=opdsFeed.entries[0].title;
        }
        filesList="<b>"+filesList+"</b>";
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(impl.getString(MessageIDConstants.shareDialogChoiceTitle));
        builder.setMessage(Html.fromHtml(String.format(impl.getString(MessageIDConstants.shareDialogChoiceMessage)
                ,message,connectedNode.getDeviceName()+";-<br/>",filesList+"<br/><br/>")));
        builder.setNegativeButton(impl.getString(MessageIDConstants.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                handleUserCancelDownloadTask();
            }
        });
        builder.setPositiveButton(impl.getString(MessageIDConstants.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleUserStartReceivingTask(opdsFeed);
            }
        });
        builder.show();
    }


    @Override
    public void fileStatusCheckInformationAvailable(List<String> fileIds) {

    }

    @Override
    public void entryStatusCheckCompleted(NetworkTask task) {

    }

    @Override
    public void networkNodeDiscovered(NetworkNode node) {
        refreshDeviceList();
    }

    @Override
    public void networkNodeUpdated(NetworkNode node) {

    }

    @Override
    public void fileAcquisitionInformationAvailable(String entryId, long downloadId, int downloadSource) {

    }

    @Override
    public void wifiConnectionChanged(String ssid) {

    }

    @Override
    public void wifiDirectConnected(boolean isDeviceConnected) {
        String deviceName=null,deviceAddress=null;
        if(isDeviceConnected ){
            WifiP2pInfo wifiP2pInfo=managerAndroid.getWifiDirectHandler().getWifiP2pInfo();
            if(wifiP2pInfo.isGroupOwner && isSharingFiles){
                for(WifiP2pDevice p2pDevice: managerAndroid.getWifiDirectHandler().getWifiP2pGroup().getClientList()){
                    deviceName=p2pDevice.deviceName;
                    deviceAddress=impl.getString(MessageIDConstants.deviceAddressLabel)+" "+p2pDevice.deviceAddress;
                }

            }else{
                WifiP2pDevice groupOwner=managerAndroid.getWifiDirectHandler().getWifiP2pGroup().getOwner();
                if(groupOwner!=null){
                    deviceName=groupOwner.deviceName;
                    deviceAddress=impl.getString(MessageIDConstants.deviceAddressLabel)+" "+groupOwner.deviceAddress;
                    connectedNode=new NetworkNode(groupOwner.deviceAddress,DEFAULT_SERVER_IP_ADDRESS);
                    connectedNode.setPort(managerAndroid.getHttpListeningPort());
                    connectedNode.setDeviceName(deviceName);
                    managerAndroid.setP2PConnectedNode(connectedNode);
                    if(!isSharedFileDialogShown){
                        isSharedFileDialogShown=true;
                        handleAcquireOPDSFeed();
                    }
                }
            }

        }
        String title= isDeviceConnected ? impl.getString(MessageIDConstants.deviceConnectedTo) :
                (managerAndroid.isSuperNodeEnabled() ? impl.getString(MessageIDConstants.receiveSharedCourse)
                        : impl.getString(MessageIDConstants.chooseDeviceToShareWith));

        if(deviceAddress!=null && deviceName!=null){
            setConnectedDeviceInfo(deviceName,deviceAddress);
        }
        setToolbarTitle(title);
        setViewsVisibility(isDeviceConnected);
        refreshDeviceList();
    }


    /**
     * Class which handle displaying all peer to peer sharing discovered devices.
     */
    private class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder>{
        List<NetworkNode> nodeList;
        void setNodeList(List<NetworkNode> nodeList) {
            this.nodeList = nodeList;
        }

        @Override
        public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new DeviceViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_device_to_share_content_with_view,parent,false));
        }

        @Override
        public void onBindViewHolder(final DeviceViewHolder holder, int position) {
            holder.deviceName.setText(nodeList.get(holder.getAdapterPosition()).getDeviceName().toUpperCase());
            String deviceAddress=impl.getString(MessageIDConstants.deviceAddressLabel)
                    +" "+nodeList.get(holder.getAdapterPosition()).getDeviceWifiDirectMacAddress();
            holder.deviceAddress.setText(deviceAddress);

            holder.deviceHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    managerAndroid.setP2PConnectedNode(nodeList.get(holder.getAdapterPosition()));
                    managerAndroid.connectWifiDirect(nodeList.get(holder.getAdapterPosition()).getDeviceWifiDirectMacAddress());
                }
            });

        }

        @Override
        public int getItemCount() {
            return nodeList.size();
        }


    }

    class DeviceViewHolder extends RecyclerView.ViewHolder{
        private TextView deviceName,deviceAddress;
        private FrameLayout deviceHolder;
        DeviceViewHolder(View itemView) {
            super(itemView);
            deviceName= (TextView) itemView.findViewById(R.id.deviceName);
            deviceAddress= (TextView) itemView.findViewById(R.id.deviceMacAddress);
            deviceHolder= (FrameLayout) itemView.findViewById(R.id.deviceHolder);
        }
    }

}