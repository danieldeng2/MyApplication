package com.troll.myapplication;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    Button change_input; EditText name_input, password_input; TextView link,copy1,QR;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        change_input = findViewById(R.id.change_input);
        name_input = findViewById(R.id.name_input);
        password_input = findViewById(R.id.password_input);
        link = findViewById(R.id.link);
        copy1 = findViewById(R.id.copy);
        QR = findViewById(R.id.qr);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        name_input.setText(sharedPreferences.getString("username", ""));
        password_input.setText(sharedPreferences.getString("password", ""));

        change_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_input.onEditorAction(EditorInfo.IME_ACTION_DONE);
                sharedPreferences.edit().putString("username", name_input.getText().toString()).apply();
                password_input.onEditorAction(EditorInfo.IME_ACTION_DONE);
                sharedPreferences.edit().putString("password", password_input.getText().toString()).apply();

                startActivity(new Intent(getApplicationContext(), CONCORD.class));
                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    AutofillManager am = getSystemService(AutofillManager.class);
                    am.commit();
                }
            }
        });
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://goo.gl/MnKaUV")));
            }
        });
        copy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", "https://goo.gl/MnKaUV");
                clipboard.setPrimaryClip(clip);
                Snackbar.make(findViewById(R.id.ContentView), "Link Copied!", Snackbar.LENGTH_LONG).show();
            }
        });

        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog builder = new Dialog(SettingsActivity.this);
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {}
                });

                ImageView imageView = new ImageView(SettingsActivity.this);
                Uri imgUri=Uri.parse("android.resource://com.troll.myapplication/"+R.drawable.qr);
                imageView.setImageURI(null);
                imageView.setImageURI(imgUri);
                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                builder.show();
            }
        });
    }

}
