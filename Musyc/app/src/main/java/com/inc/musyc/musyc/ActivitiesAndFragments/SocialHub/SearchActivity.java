package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.inc.musyc.musyc.R;

public class SearchActivity extends AppCompatActivity {

    private TextInputLayout mInput;
    private Button mSearchbt;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar=(Toolbar)findViewById(R.id.search_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  Search");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        mInput=(TextInputLayout)findViewById(R.id.search_input);

        mSearchbt=(Button)findViewById(R.id.search_bt);

        mSearchbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query=mInput.getEditText().getText().toString();
                if(query.length()<=0)return;
                Intent Ser=new Intent(SearchActivity.this,DemoActivity.class);
                Ser.putExtra("query",query);
                startActivity(Ser);
            }
        });
    }
}
