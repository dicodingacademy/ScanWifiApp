package com.nbs.scanwifiapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String NETWROK_ADDITIONAL_SECURITY_TKIP = "TKIP";

    public static final String NETWROK_ADDITIONAL_SECURITY_AES = "AES";

    public static final String NETWROK_ADDITIONAL_SECURITY_WEP = "WEP";

    public static final String NETWROK_ADDITIONAL_SECURITY_NONE = "NONE";

    public static final String SSID = "Andoid Jedi";
    public static final String PASSWORD = "keenanifah";
    public static final String SECURITY_PARAM = "WPA";

//    public static final String SSID = "NBS WORKS";
//    public static final String PASSWORD = "indonesiaindah";
//    public static final String SECURITY_PARAM = "WPA";

    public static final String SECURITY_WEP = "WEP";
    public static final String SECURITY_NONE = "NONE";
    public static final String SECURITY_WPA = "WPA";
    public static final String SECURITY_WPA2 = "WPA2";
    public static final String SECURITY_WPAWPA2PSK = "WPA/WPA2 PSK";

    private BroadcastReceiver scanWifiReceiver;

    private final String BACKSLASH = "\"";

    private String TAG = "ScanWifiApp";

    private WifiManager mWifiManager;

    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        scanWifi();

        btnConnect = findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWifiConfig(SSID, PASSWORD, SECURITY_PARAM, NETWROK_ADDITIONAL_SECURITY_AES);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try{
            unregisterReceiver(scanWifiReceiver);
        }catch (Exception e){
            Log.d(TAG, "Already unregistered");
        }
    }

    private void scanWifi() {
        if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

            // register WiFi scan results receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            initWifiReceiver(mWifiManager);

            registerReceiver(scanWifiReceiver, filter);

            // start WiFi Scan
            mWifiManager.startScan();
        }else{
            Toast.makeText(this, "Wifi disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void initWifiReceiver(final WifiManager mWifiManager){
        scanWifiReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                List<ScanResult> results = mWifiManager.getScanResults();

                if (!results.isEmpty()){

                    Log.d(TAG, "Wi-Fi Scan Results ... Count:" + results.size());
                    for(int i = 0; i < results.size(); i++) {
                        Log.v(TAG, "  BSSID       =" + results.get(i).BSSID);
                        Log.v(TAG, "  SSID        =" + results.get(i).SSID);
                        Log.v(TAG, "  Capabilities=" + results.get(i).capabilities);
                        Log.v(TAG, "  Frequency   =" + results.get(i).frequency);
                        Log.v(TAG, "  Level       =" + results.get(i).level);
                        Log.v(TAG, "---------------");
                    }
                }else{
                    Log.d(TAG, "No wifi detected");
                }
            }
        };
    }

    public void addWifiConfig(String ssid, String password, String securityParam, String securityDetailParam) {
        Toast.makeText(this, "Connecting....", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Inside addWifiConfig...");
        if (ssid == null) {
            throw new IllegalArgumentException(
                    "Required parameters can not be NULL #");
        }

        String wifiName = ssid;
        WifiConfiguration conf = new WifiConfiguration();
        // On devices with version Kitkat and below, We need to send SSID name
        // with double quotes. On devices with version Lollipop, We need to send
        // SSID name without double quotes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            conf.SSID = wifiName;
        } else {
            conf.SSID = BACKSLASH + wifiName + BACKSLASH;
        }
        String security = securityParam;
        Log.d(TAG, "Security Type :: " + security);
        if (security.equalsIgnoreCase(SECURITY_WEP)) {
            conf.wepKeys[0] = password;
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (security
                .equalsIgnoreCase(SECURITY_NONE)) {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (SECURITY_WPA
                .equalsIgnoreCase(security)
                || SECURITY_WPA2
                .equalsIgnoreCase(security)
                || SECURITY_WPAWPA2PSK
                .equalsIgnoreCase(security)) {
            // appropriate ciper is need to set according to security type used,
            // ifcase of not added it will not be able to connect
            conf.preSharedKey = BACKSLASH
                    + password + BACKSLASH;
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        }
        String securityDetails = securityDetailParam;
        if (securityDetails
                .equalsIgnoreCase(NETWROK_ADDITIONAL_SECURITY_TKIP)) {
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
        } else if (securityDetails
                .equalsIgnoreCase(NETWROK_ADDITIONAL_SECURITY_AES)) {
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
        } else if (securityDetails
                .equalsIgnoreCase(NETWROK_ADDITIONAL_SECURITY_WEP)) {
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (securityDetails
                .equalsIgnoreCase(NETWROK_ADDITIONAL_SECURITY_NONE)) {
            conf.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.NONE);
        }

        int newNetworkId = mWifiManager.addNetwork(conf);
        mWifiManager.enableNetwork(newNetworkId, true);
        mWifiManager.saveConfiguration();
        mWifiManager.setWifiEnabled(true);
        mWifiManager.disconnect();
        mWifiManager.reconnect();
    }
}
