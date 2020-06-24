package com.mnk.env;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class CardMenu extends AppCompatActivity {
    CardView menu1,menu2,menu3,menu4,menu5,menu6,menu7;
    BluetoothAdapter bt=null;
    static int flag_datasets=0;
    static BluetoothSocket socket=null;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_menu);

        menu1=(CardView) findViewById(R.id.menu1);
        menu2=(CardView) findViewById(R.id.menu2);
        menu3=(CardView) findViewById(R.id.menu3);
        menu4=(CardView) findViewById(R.id.menu4);
        menu5=(CardView) findViewById(R.id.menu5);
        menu6=(CardView) findViewById(R.id.menu6);
        menu7=(CardView) findViewById(R.id.menu7);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.SEND_SMS
            },10);
        }

        bt=BluetoothAdapter.getDefaultAdapter();
        //Toast.makeText(this, ""+socket, Toast.LENGTH_SHORT).show();
        if(!bt.isEnabled())
        {
            /*****first method to enable bluetooth*****/
            //enable bluetooth without pop-up any dialog box

            showBluetoothEnableDialog();

            /*****Second method to enable bluetooth*****/
            //Pop-up dialog box to confirm to enable bluetooth
     /*Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
     startActivity(i); */
            //Display blutooth device value on Toast
            Toast.makeText(this, "bluetooth found.."+bt, Toast.LENGTH_LONG).show();
        }
        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(socket!=null)
                {
                    Intent i = new Intent(CardMenu.this, Graphset.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(CardMenu.this, "No device found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(CardMenu.this, Mapset.class);
                startActivity(i);

            }
        });
        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(socket!=null) {
                    Intent i = new Intent(CardMenu.this, Datasets.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(CardMenu.this, "No device found", Toast.LENGTH_SHORT).show();
                }

            }
        });
        menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bt.isEnabled()) {
                    Intent i = new Intent(CardMenu.this, Bluetooth_set.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(CardMenu.this, "Bluetooth is Not Enabled", Toast.LENGTH_SHORT).show();
                    showBluetoothEnableDialog();
                }

            }
        });
        menu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(CardMenu.this, MainActivity.class);
                startActivity(i);

            }
        });
        menu6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(CardMenu.this, Settings.class);
                startActivity(i);

            }
        });
        menu7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(CardMenu.this, FetchActivity.class);
                startActivity(i);
            }
        });
    }

    private void showBluetoothEnableDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("App wants to turn Bluetooth On for the device");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                bt.enable();
            }
        });
        builder.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Toast.makeText(this, ""+socket, Toast.LENGTH_SHORT).show();
        int flag_datasets=0;

    }
}
