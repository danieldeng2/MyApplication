package com.troll.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.CaptivePortal;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CONCORD extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    boolean WMdownload = false, VLEdonwload = false;
    private Network net;
    private CaptivePortal captivePortal;
    WebView engine; NavigationView navigationView;ProgressBar Pbar;SharedPreferences sharedPreferences;Boolean ArrivedFromPortal = false;
    String username = "", password = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concord);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        engine = findViewById(R.id.webView);
        navigationView = findViewById(R.id.nav_view);
        Pbar = findViewById(R.id.pB1);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (!(sharedPreferences.contains("username") && sharedPreferences.contains("password")))
            startActivity(new Intent(this, SettingsActivity.class));

        setNavbarcolor(Color.parseColor("#202020"), Color.parseColor("#737373"));
        setShortcut();
        setEngine();

        navigationView.setNavigationItemSelectedListener(this);
        Bundle scdata = getIntent().getExtras();
        int scvalue = (scdata==null) ? 0:scdata.getInt("screen");
        navigationView.getMenu().getItem(scvalue).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(scvalue));
        TextView Account = navigationView.getHeaderView(0).findViewById(R.id.Account);
        Account.setText(sharedPreferences.getString("username", "")+"@concordcollege.org.uk");

        Intent intent = getIntent();
        if (ConnectivityManager.ACTION_CAPTIVE_PORTAL_SIGN_IN.equals(intent.getAction())) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(CONCORD.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            net = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK);
            captivePortal = intent.getParcelableExtra(ConnectivityManager.EXTRA_CAPTIVE_PORTAL);

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo ();
            String ssid  = info.getSSID();
            if(ssid != "<unknown ssid>") {
                if (!(ssid.equalsIgnoreCase("Student Wireless") || ssid.equalsIgnoreCase("\"Student Wireless\""))) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://connectivitycheck.gstatic.com/generate_204")));
                }
                ArrivedFromPortal = true;
                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().getItem(0));
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        engine.getSettings().setUserAgentString(System.getProperty("http.agent"));
        engine.getSettings().setLoadWithOverviewMode(false);
        engine.getSettings().setUseWideViewPort(false);
        engine.getSettings().setSupportZoom(false);
        engine.getSettings().setBuiltInZoomControls(false);
        engine.getSettings().setDisplayZoomControls(true);

        username = sharedPreferences.getString("username", "");
        password = sharedPreferences.getString("password", "");
        if (id == R.id.nav_site) {

            engine.loadUrl("http://www.concordcollegeuk.com/");
            engine.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });

        } else if (id == R.id.nav_sharepoint) {

            engine.loadUrl("https://concorduk.sharepoint.com/sites/ConcordCollege");
            engine.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });

        } else if (id == R.id.nav_wifi) {
            loadSSLCertificates();
             FillForm("https://192.168.64.1:10443/auth1.html",
                    "javascript:" +
                    "var uselessvar1 = document.getElementById('userName').value = '" + username + "';"+
                    "var inputs = document.getElementsByName('pwd')[0].value = '" + password + "' ;"
                    + "var use = document.getElementsByName('Submit')[0].click();"
                    + "document.standardPass.Submit.disabled = false;");

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    engine.loadUrl("http://192.168.64.1/dynUserLogin.html?loginDone=1");
                    if(ArrivedFromPortal){
                        finish();
                        Toast.makeText(getBaseContext(), "Logged into Student Wireless! ", Toast.LENGTH_LONG).show();
                        ArrivedFromPortal = false;
                    }
                }
            }, 1000);


            if (net!=null && captivePortal != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    captivePortal.reportCaptivePortalDismissed();
                }
            }

        } else if (id == R.id.nav_email_web) {

            engine.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36");
            engine.getSettings().setLoadWithOverviewMode(true);
            engine.getSettings().setUseWideViewPort(true);
            engine.getSettings().setSupportZoom(true);
            engine.getSettings().setBuiltInZoomControls(true);
            engine.getSettings().setDisplayZoomControls(false);

            FillForm("https://webmail.concordcollege.org.uk/owa/",
                    "javascript:" +
                    "var uselessvar1 = document.getElementById('username').value = '" + username + "';"+
                    "var inputs = document.getElementById('password').value = '" + password + "' ;"+
                    "var elements = document.getElementsByClassName('btn')[0].click();");


            engine.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent,
                                            String contentDisposition, String mimetype,
                                            long contentLength) {

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    if (!WMdownload){
                        WMdownload = true;
                        Toast.makeText(getBaseContext(), "Log in to download. If fails, try again. ", Toast.LENGTH_LONG).show();
                        i.setData(Uri.parse("https://webmail.concordcollege.org.uk/owa/auth/logon.aspx?url="+url));
                        startActivity(i);
                    }else{
                        Toast.makeText(getBaseContext(), "If fails again, better luck in life. ", Toast.LENGTH_LONG).show();
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }

                }
            });

        }

        else if (id == R.id.nav_vle) {

            FillForm("https://ff.concordcollege.org.uk/",
                    "javascript:" +
                    "var uselessvar1 = document.getElementById('username').value = '" + username + "';"+
                    "var inputs = document.getElementById('password').value = '" + password + "' ;"+
                    "var elements = document.getElementsByClassName('ff-login-submit')[0].click();");

            engine.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent,
                                            String contentDisposition, String mimetype,
                                            long contentLength) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    if (!VLEdonwload){
                        VLEdonwload = true;
                        Toast.makeText(getBaseContext(), "Log in to download. If fails, try again!", Toast.LENGTH_LONG).show();
                        i.setData(Uri.parse("https://ff.concordcollege.org.uk/login/login.aspx?prelogin="+url));
                        startActivity(i);
                    }else{
                        Toast.makeText(getBaseContext(), "If fails again, better luck in life!", Toast.LENGTH_LONG).show();
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }

                }
            });
        }
        else if (id == R.id.nav_email_app) {

            Intent intent = getPackageManager().getLaunchIntentForPackage("com.microsoft.office.outlook");
            Intent intent1 = getPackageManager().getLaunchIntentForPackage("com.android.vending");
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if(intent1 != null) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + "com.microsoft.office.outlook"));
                startActivity(intent);
            }
            else{
                intent = new Intent (Intent.ACTION_VIEW );
                intent.setData(Uri.parse("https://www.microsoft.com/en-gb/outlook-com/"));
                startActivity(intent);
            }

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void setShortcut(){
        ShortcutManager shortcutManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager.class);
            Intent sc = new Intent(this, CONCORD.class);
            sc.setAction(Intent.ACTION_VIEW);

            sc.putExtra("screen", 3);
            ShortcutInfo VLE = new ShortcutInfo.Builder(this, "id1")
                    .setShortLabel("VLE")
                    .setLongLabel("Firefly")
                    .setIcon(Icon.createWithResource(this, R.drawable.scvle))
                    .setIntent(sc)
                    .build();

            sc.putExtra("screen", 4);
            ShortcutInfo webmail = new ShortcutInfo.Builder(this, "id2")
                    .setShortLabel("Email")
                    .setLongLabel("Webmail")
                    .setIcon(Icon.createWithResource(this, R.drawable.scmail))
                    .setIntent(sc)
                    .build();

            sc.putExtra("screen", 1);
            ShortcutInfo website = new ShortcutInfo.Builder(this, "id3")
                    .setShortLabel("Website")
                    .setLongLabel("Website")
                    .setIcon(Icon.createWithResource(this, R.drawable.scweb))
                    .setIntent(sc)
                    .build();

            sc.putExtra("screen", 2);
            ShortcutInfo sharepoint = new ShortcutInfo.Builder(this, "id5")
                    .setShortLabel("SharePoint")
                    .setLongLabel("SharePoint")
                    .setIcon(Icon.createWithResource(this, R.drawable.scsharepoint))
                    .setIntent(sc)
                    .build();

            sc.putExtra("screen", 0);
            ShortcutInfo wifi = new ShortcutInfo.Builder(this, "id4")
                    .setShortLabel("Wifi")
                    .setLongLabel("Wifi")
                    .setIcon(Icon.createWithResource(this, R.drawable.scwifi))
                    .setIntent(sc)
                    .build();

            shortcutManager.setDynamicShortcuts(Arrays.asList(wifi,sharepoint, VLE, webmail));
        }
    }
    private static final int[] CERTIFICATES = {
            R.raw.concordcollege,
    };
    private ArrayList<SslCertificate> certificates = new ArrayList<>();
    private void loadSSLCertificates() {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            for (int rawId : CERTIFICATES) {
                InputStream inputStream = getResources().openRawResource(rawId);
                InputStream certificateInput = new BufferedInputStream(inputStream);
                try {
                    Certificate certificate = certificateFactory.generateCertificate(certificateInput);
                    if (certificate instanceof X509Certificate) {
                        X509Certificate x509Certificate = (X509Certificate) certificate;
                        SslCertificate sslCertificate = new SslCertificate(x509Certificate);
                        certificates.add(sslCertificate);
                        Log.w("Cert: ", ""+rawId);
                    } else {
                        Log.w("Cert: ", "Wrong Certificate format: " + rawId);
                    }
                } catch (CertificateException exception) {
                    Log.w("Cert: ", "Cannot read certificate: " + rawId);
                } finally {
                    try {
                        certificateInput.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    public void FillForm(String url, final String jscode){
        engine.loadUrl(url);
        engine.setWebViewClient( new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                SslCertificate serverCertificate = error.getCertificate();
                Bundle serverBundle = SslCertificate.saveState(serverCertificate);
                for (SslCertificate appCertificate : certificates) {
                    if (TextUtils.equals(serverCertificate.toString(), appCertificate.toString())) { // First fast check
                        Bundle appBundle = SslCertificate.saveState(appCertificate);
                        Set<String> keySet = appBundle.keySet();
                        boolean matches = true;
                        for (String key : keySet) {
                            Object serverObj = serverBundle.get(key);
                            Object appObj = appBundle.get(key);
                            if (serverObj instanceof byte[] && appObj instanceof byte[]) {     // key "x509-certificate"
                                if (!Arrays.equals((byte[]) serverObj, (byte[]) appObj)) {
                                    matches = false;
                                    break;
                                }
                            } else if ((serverObj != null) && !serverObj.equals(appObj)) {
                                matches = false;
                                break;
                            }
                        }
                        if (matches) {
                            handler.proceed();
                            return;
                        }
                    }
                }

                handler.cancel();
                String message = "SSL Error " + error.getPrimaryError();
                Log.w("Cert: ", message);

            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(username != "" && password != "" && jscode != "") view.loadUrl(jscode);
            }
        });
    }

    public void setEngine() {
        engine.getSettings().setJavaScriptEnabled(true);
        engine.getSettings().setDomStorageEnabled(true);
        engine.getSettings().setAllowFileAccess(true);
        engine.getSettings().setAllowContentAccess(true);
        engine.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        engine.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                if(progress < 100 && Pbar.getVisibility() == ProgressBar.GONE){
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                }
                Pbar.setProgress(progress);
                if(progress == 100) {
                    Pbar.setVisibility(ProgressBar.GONE);
                }
            }
        });
    }
    private void setNavbarcolor(int navDefaultTextColor,int navDefaultIconColor) {
        //Defining ColorStateList for menu item Text
        ColorStateList navMenuTextList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        Color.parseColor("#962b3c"),
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor
                }
        );
        //Defining ColorStateList for menu item Icon
        ColorStateList navMenuIconList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        Color.parseColor("#962b3c"),
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor
                }
        );

        navigationView.setItemTextColor(navMenuTextList);
        navigationView.setItemIconTintList(navMenuIconList);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            engine.goBack();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.concord, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_help){
            startActivity(new Intent(this, Help.class));
            return true;
        }
        else if (id == R.id.action_browser){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(engine.getUrl()) ) );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
