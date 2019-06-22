package com.troll.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.CaptivePortal;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;


import static android.view.KeyEvent.ACTION_DOWN;

public class CONCORD extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Network net;Bitmap bmp;
    private CaptivePortal captivePortal;
    WebView engine; NavigationView navigationView;ProgressBar Pbar;SharedPreferences sharedPreferences;
    ImageView imageView;

    String username = "", password = "", ssid = "";
    boolean WMdownload = false, VLEdonwload = false, UpdateImage = false;



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

        setNavbarColor(Color.parseColor("#202020"), Color.parseColor("#737373"));
        setShortcut();
        setEngine();
        CookieManager.getInstance().setAcceptCookie(true);

        navigationView.setNavigationItemSelectedListener(this);
        TextView Account = navigationView.getHeaderView(0).findViewById(R.id.Account);
        Account.setText(sharedPreferences.getString("username", "id")+"@concordcollege.org.uk");
        TextView AccountName = navigationView.getHeaderView(0).findViewById(R.id.AccountName);
        AccountName.setText(sharedPreferences.getString("AccountName", "Account"));
        imageView = navigationView.getHeaderView(0).findViewById(R.id.ProfilePic);

        setClickListeners();

        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f=new File( directory, "profile.jpg");
            bmp = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(bmp);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        Intent intent = getIntent();
        Bundle scdata = intent.getExtras();
        int scvalue = (scdata==null) ? 0:scdata.getInt("screen");
        navigationView.getMenu().getItem(scvalue).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(scvalue));
        processPortalIntent(intent);
    }

    public void setClickListeners(){
        TextView Account = navigationView.getHeaderView(0).findViewById(R.id.Account);
        TextView AccountName = navigationView.getHeaderView(0).findViewById(R.id.AccountName);
        Button footer_help = findViewById(R.id.footer_help);
        Button footer_settings = findViewById(R.id.footer_settings);
        footer_settings.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CONCORD.this, SettingsActivity.class));
            }
        });

        footer_help.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CONCORD.this, Help.class));
            }
        });
        Account.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CONCORD.this, SettingsActivity.class));
            }
        });
        AccountName.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CONCORD.this, SettingsActivity.class));
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CONCORD.this, SettingsActivity.class));
            }
        });
    }

    @SuppressLint("JavascriptInterface")
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
            ////yeah, lets do this.
            engine.loadUrl("http://www.concordcollegeuk.com/");
            engine.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });

        } else if (id == R.id.nav_sharepoint) {

            FillForm("https://concorduk.sharepoint.com/sites/ConcordCollege",
                    "javascript:" +
                            "document.getElementsByName('loginfmt')[0].value = '" + username + "@concordcollege.org.uk_';"
                            +"document.getElementsByName('passwd')[0].value = '" + password + "_';"
                            +"document.getElementById('idSIButton9').click();");


            //because Microsoft does not allow autofill, the following code edits the field by manually deleting the additional '_' and presses enter.
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    BaseInputConnection  mInputConnection = new BaseInputConnection(engine, true);
                    mInputConnection.sendKeyEvent(new KeyEvent( ACTION_DOWN,KeyEvent.KEYCODE_DEL));
                    mInputConnection.sendKeyEvent(new KeyEvent( ACTION_DOWN,KeyEvent.KEYCODE_ENTER));

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            BaseInputConnection  mInputConnection = new BaseInputConnection(engine, true);
                            mInputConnection.sendKeyEvent(new KeyEvent( ACTION_DOWN,KeyEvent.KEYCODE_DEL));
                            mInputConnection.sendKeyEvent(new KeyEvent( ACTION_DOWN,KeyEvent.KEYCODE_ENTER));
                        }
                    }, 1500);

                }
            }, 1500);

            engine.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });

        } else if (id == R.id.nav_wifi) {

             FillForm("https://web64.concordcollege.org.uk/auth1.html",
                     "javascript:" +
                            "document.getElementById('userName').value = '" + username + "';"+
                            "document.getElementsByName('pwd')[0].value = '" + password + "';"+
                            "document.getElementsByName('Submit')[0].click();"
             );

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    engine.loadUrl("https://web64.concordcollege.org.uk/dynUserLogin.html?loginDone=1");

                    engine.setWebViewClient(new WebViewClient() {
                        public void onPageFinished(WebView view, String url) {
                            if (net != null && captivePortal != null && (ssid.equalsIgnoreCase("Student Wireless") || ssid.equalsIgnoreCase("\"Student Wireless\""))) {
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        captivePortal.reportCaptivePortalDismissed();
                                    }
                                }, 3000);
                            }
                        }
                    });

                }
            }, 1000);

        } else if (id == R.id.nav_email_web) {

            engine.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36");
            engine.getSettings().setLoadWithOverviewMode(true);
            engine.getSettings().setUseWideViewPort(true);
            engine.getSettings().setSupportZoom(true);
            engine.getSettings().setBuiltInZoomControls(true);
            engine.getSettings().setDisplayZoomControls(false);

            FillForm("https://webmail.concordcollege.org.uk/owa/",
                    "javascript:" +
                            "document.getElementById('username').value = '" + username + "';"+
                            "document.getElementById('password').value = '" + password + "';"+
                            "document.getElementsByClassName('btn')[0].click();"
            );

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

        } else if (id == R.id.nav_vle) {



            FillForm("https://ff.concordcollege.org.uk/",
                    "javascript:" +
                            "document.getElementById('username').value = '" + username + "';"+
                            "document.getElementById('password').value = '" + password + "';"+
                            "document.getElementsByClassName('ff-login-submit')[0].click();"
            );

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


            new Handler().postDelayed(new Runnable() {
                public void run() {
                    engine.evaluateJavascript("document.getElementsByTagName('em')[0].innerHTML", new ValueCallback<String>() {
                        @Override public void onReceiveValue(String value) {

                            value = value.replaceAll("\"", "");
                            if(!value.equalsIgnoreCase("null")){
                                sharedPreferences.edit().putString("AccountName", value).apply();
                                TextView AccountName = navigationView.getHeaderView(0).findViewById(R.id.AccountName);
                                AccountName.setText(value);
                                Log.v("value ", value);
                            }


                        }
                    });

                    engine.evaluateJavascript("document.getElementsByName('ff:userGuid')[0].getAttribute('content')", new ValueCallback<String>() {
                        @Override public void onReceiveValue(String value) {
                            value = value.replaceAll("\"", "");
                            new LoadImage().execute(value);
                        }
                    });

                }
            }, 5000);

        } else if (id == R.id.nav_email_app) {

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
            } else{
                intent = new Intent (Intent.ACTION_VIEW );
                intent.setData(Uri.parse("https://www.microsoft.com/en-gb/outlook-com/"));
                startActivity(intent);
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        final SpannableString s = new SpannableString(message);
        final AlertDialog d = new AlertDialog.Builder(CONCORD.this)
                .setMessage(s)
                .setPositiveButton("OK", okListener)
                .create();
        d.show();
    }

    public void processPortalIntent(Intent intent){
        if (ConnectivityManager.ACTION_CAPTIVE_PORTAL_SIGN_IN.equals(intent.getAction())) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(CONCORD.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                showMessageOKCancel("Please allow location permission to identify whether the WiFi connected is Student Wireless. ",
                    new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(CONCORD.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });
        }
        net = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK);
        captivePortal = intent.getParcelableExtra(ConnectivityManager.EXTRA_CAPTIVE_PORTAL);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        ssid = info.getSSID();
        if(ssid != "<unknown ssid>") {
            if (!(ssid.equalsIgnoreCase("Student Wireless") || ssid.equalsIgnoreCase("\"Student Wireless\""))) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://connectivitycheck.gstatic.com/generate_204")));
            }
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }
    }
}

    class LoadImage extends AsyncTask<String, Void, Void> {


        protected Void doInBackground(String...value) {
            try {

                URL url = new URL("https://ff.concordcollege.org.uk/profilepic.aspx?guid="+value[0]+"&size=regular");

                URLConnection connection = url.openConnection();
                connection.setRequestProperty("Cookie",CookieManager.getInstance().getCookie("https://ff.concordcollege.org.uk/dashboard/"));
                InputStream response = connection.getInputStream();
                UpdateImage = ("https://ff.concordcollege.org.uk/profilepic.aspx?guid="+value[0]+"&size=regular").equalsIgnoreCase(connection.getURL().toString());
                bmp = BitmapFactory.decodeStream(response);

            }catch(Exception e){e.printStackTrace();}
            return null;
        }

        protected void onPostExecute(Void v) {
            if(UpdateImage){
                saveToInternalStorage(bmp);
                imageView.setImageBitmap(bmp);
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,"profile.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
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

    public void FillForm(String url, final String jscode){
        engine.loadUrl(url);
        engine.setWebViewClient( new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(!username.equals("") && !password.equals("")) view.loadUrl(jscode);
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

    private void setNavbarColor(int navDefaultTextColor,int navDefaultIconColor) {
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
        if (id == R.id.action_browser){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(engine.getUrl()) ) );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
