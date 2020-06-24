package com.mnk.env;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class Bluetooth_set extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter;
    ListView listView;
    static final int STATE_LISTNING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static BluetoothSocket socket;
    static BluetoothServerSocket serverSocket;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String APP_NAME = "IndoAir";
    BluetoothDevice[] btArray;
    TextView status;
    static BluetoothDevice connectbt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_set);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listView = findViewById(R.id.listView);
        status = findViewById(R.id.status);
        implementListeners();

        sharedPreferences = getSharedPreferences("Location_Data", Context.MODE_PRIVATE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectbt = btArray[position];

                //get the device Name and HardwareAddress as the Device ID
                String deviceHardwareAddress = connectbt.getName() + " -> " + connectbt.getAddress();

                //Store it in SharedPreference so that it can be used in Datasets Class as DeviceId in firebase Dtatabase
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("DeviceId", deviceHardwareAddress);
                editor.apply();

                Toast.makeText(Bluetooth_set.this, "" + deviceHardwareAddress, Toast.LENGTH_SHORT).show();

                Bluetooth_set.ClientClass clientClass = new Bluetooth_set.ClientClass(btArray[position]);
                clientClass.start();

                status.setText("Connecting");
            }
        });
    }

    private void implementListeners() {
        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
        String[] strings = new String[bt.size()];
        btArray = new BluetoothDevice[bt.size()];
        int index = 0;
        if (bt.size() > 0) {
            for (BluetoothDevice device : bt) {
                btArray[index] = device;
                strings[index] = device.getName();
                index++;
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_single_choice, strings);
            listView.setAdapter(arrayAdapter);
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;

        public ClientClass(BluetoothDevice device1) {
            device = device1;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                FunctionalitiesActivity.bluetoothSocket = socket;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String currentDate = sdf.format(new Date());
            String filename = currentDate;
            switch (msg.what) {
                case STATE_LISTNING:
                    status.setText("Listning");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection_Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    status.setText("State_massage");
                    break;
            }
            return true;
        }
    });
}
