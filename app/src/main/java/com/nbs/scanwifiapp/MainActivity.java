package com.nbs.scanwifiapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver scanWifiReceiver;

    private String TAG = "ScanWifiApp";

    private WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        scanWifi();
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
}
