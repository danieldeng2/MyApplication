package com.troll.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;

public class CONCORD extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    boolean WMdownload = false, VLEdonwload = false;
    WebView engine; NavigationView navigationView;ProgressBar Pbar;SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concord);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        engine = (WebView) findViewById(R.id.webView);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Pbar = findViewById(R.id.pB1);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (!(sharedPreferences.contains("username") && sharedPreferences.contains("password"))) {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        }
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
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_site) {

            engine.loadUrl("http://www.concordcollegeuk.com/");
            engine.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });

        } else if (id == R.id.nav_wifi) {

            engine.loadUrl("https://192.168.64.1:10443/auth1.html");
            engine.setWebViewClient( new WebViewClient() {
                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                }

                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    final String username = sharedPreferences.getString("username", "");
                    final String password = sharedPreferences.getString("password", "");
                    final String js = "javascript:" +
                            "var uselessvar1 = document.getElementById('userName').value = '" + username + "';"+
                            "var inputs = document.getElementsByName('pwd')[0].value = '" + password + "' ;"
                            + "var use = document.getElementsByName('Submit')[0].click();"
                            + "document.standardPass.Submit.disabled = false;"
                            ;

                    if (Build.VERSION.SDK_INT >= 19) {
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                return;
                            }
                        });
                    } else {

                        view.loadUrl(js);
                    }
                }
            });

        } else if (id == R.id.nav_email_web) {
            engine.loadUrl("https://webmail.concordcollege.org.uk/owa/");
            engine.setWebViewClient( new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    final String username = sharedPreferences.getString("username", "");
                    final String password = sharedPreferences.getString("password", "");
                    final String js = "javascript:" +
                            "var uselessvar1 = document.getElementById('username').value = '" + username + "';"+
                            "var inputs = document.getElementById('password').value = '" + password + "' ;"+
                            "var elements = document.getElementsByClassName('btn')[0].click();"
                            ;

                    if (Build.VERSION.SDK_INT >= 19) {
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) { return;}
                        });
                    } else {
                        view.loadUrl(js);
                    }
                }
            });

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
            engine.loadUrl("https://ff.concordcollege.org.uk/");
            engine.setWebViewClient( new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    final String username = sharedPreferences.getString("username", "");
                    final String password = sharedPreferences.getString("password", "");
                    final String js = "javascript:" +
                            "var uselessvar1 = document.getElementById('username').value = '" + username + "';"+
                            "var inputs = document.getElementById('password').value = '" + password + "' ;"+
                            "var elements = document.getElementsByClassName('ff-login-submit')[0].click();"
                            ;

                    if (Build.VERSION.SDK_INT >= 19) {
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                return;
                            }
                        });
                    } else {
                        view.loadUrl(js);
                    }
                }
            });

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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void setShortcut(){
        ShortcutManager shortcutManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager.class);
            Intent sc = new Intent(this, CONCORD.class);
            sc.setAction(Intent.ACTION_VIEW);

            sc.putExtra("screen", 2);
            ShortcutInfo VLE = new ShortcutInfo.Builder(this, "id1")
                    .setShortLabel("VLE")
                    .setLongLabel("Firefly")
                    .setIcon(Icon.createWithResource(this, R.drawable.scvle))
                    .setIntent(sc)
                    .build();

            sc.putExtra("screen", 3);
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

            sc.putExtra("screen", 0);
            ShortcutInfo wifi = new ShortcutInfo.Builder(this, "id4")
                    .setShortLabel("Wifi")
                    .setLongLabel("Wifi")
                    .setIcon(Icon.createWithResource(this, R.drawable.scwifi))
                    .setIntent(sc)
                    .build();

            shortcutManager.setDynamicShortcuts(Arrays.asList(wifi, website, VLE, webmail));
        }
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
