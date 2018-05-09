//package com.troll.myapplication;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.net.wifi.WifiManager;
//import android.util.Log;
//import android.widget.Toast;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URI;
//import java.net.URL;
//
//public class onStateChange extends BroadcastReceiver {
//    private static final String SSID = "Student Wireless";
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Log.d("receiver", "onReceive");
//        NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//        if (!netInfo.isConnected()) {
//            Log.d("receiver", "not connected");
//            return;
//        }
//        String ssid;
//        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        try {
//            ssid = wifi.getConnectionInfo().getSSID();
//            Log.d(ssid, SSID);
//        }
//        catch (NullPointerException e) {
//
//            ssid = null;
//
//        }
//
//        if (!(SSID.equalsIgnoreCase(ssid) || ("\"" + SSID + "\"").equalsIgnoreCase(ssid))) {
//            return;
//        }
//        Intent i = new Intent(context, CONCORD.class);
//        context.startActivity(i);
//    }
//}
