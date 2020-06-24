package com.mnk.env;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class Settings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
    public void indoor(View view)
    {
        startActivity(new Intent(this,Indoor.class));
    }
    public void outdoor(View view)
    {
        startActivity(new Intent(this,Outdoor.class));
    }
}
