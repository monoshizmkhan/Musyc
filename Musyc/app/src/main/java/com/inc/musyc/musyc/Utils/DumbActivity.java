package com.inc.musyc.musyc.Utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.inc.musyc.musyc.R;

public class DumbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dumb);
        finish();
    }
}
