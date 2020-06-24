package com.mnk.env;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static ToneGenerator toneGenerator;
    Button listen, send, listDevices, upLoad;
    ListView listView;
    TextView status, msg_box;
    EditText writeMsg;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    static BluetoothDevice connectbt;
    File file;
    static BluetoothSocket socket;
    static final int STATE_LISTNING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final String APP_NAME = "Bluetooth1";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    SendReceive sendReceive;
    LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    LocationManager locationManager;
    LocationListener locationListener;
    double lng = 0;
    double lat = 0;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    int flag = 0;
    int snooze_count = 0;


    int sms_send_time = 0;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    // Write a message to the database

    // Write a message to the database


    String upLoadServerUri = null;

    /**********  File Path *************/
    final String uploadFilePath = "/BluetoothApp/";
    final String uploadFileName = "Filename.txt";
    // Write a message to the database
    FirebaseDatabase database;
    DatabaseReference Ref;
    String mydate;
    String dgas = null;

    TextView massage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // FirebaseApp.initializeApp(this);
//        database=FirebaseDatabase.getInstance();
//        Ref=database.getReference("Data");
        Calendar cal = Calendar.getInstance();
        mChart = (LineChart) findViewById(R.id.lineChart);
        // enable description text

        mChart.getDescription().setEnabled(true);
        massage = (TextView) findViewById(R.id.massage);
        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        checkPermissions();
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);

        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(true);

        feedMultiple();
        mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//
//        Ref.push().setValue(new DataModel(mydate,"s","d","s","d","f","dd","f","d","s","d","d"));

        //updateChildrenAsync(hopperUpdates);

        findViewByIds();
        msg_box.setMovementMethod(new ScrollingMovementMethod());
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        upLoadServerUri = "http://rohitekka.000webhostapp.com/test1/UploadToServer.php";

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mSettingsClient = LocationServices.getSettingsClient(this);
//
//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//
//                mCurrentLocation = locationResult.getLastLocation();
//                lng = mCurrentLocation.getLongitude();
//                lat=   mCurrentLocation.getLatitude();
//                Toast.makeText(MainActivity.this, ""+lat, Toast.LENGTH_SHORT).show();
//            }
//        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET, Manifest.permission.BLUETOOTH,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SEND_SMS
                }, 10);
                return;
            }
        } else {
            configurButton();
        }

        implementListeners();

    }

    private void implementListeners() {
        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(arrayAdapter);
                }

            }
        });
        /*listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClass serverClass=new ServerClass();
                serverClass.start();
            }
        });
        */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectbt = btArray[position];
                ClientClass clientClass = new ClientClass(btArray[position]);
                clientClass.start();
                status.setText("Connecting");
            }
        });

        /*send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string= String.valueOf(writeMsg.getText());
                sendReceive.write(string.getBytes());
            }
        });
        */
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
                    //byte[] readBuff= (byte[]) msg.obj;
                    String readBuffr = (String) msg.obj;
                    //int bytes=msg.arg1;
                    String tempMsg = readBuffr;

                    String path = Environment.getExternalStorageDirectory() + "/BluetoothApp/";
                    file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    RandomAccessFile raf;
                    try {
                        raf = new RandomAccessFile(file + "/Filename.txt", "rw");
                        raf.seek(raf.length());
                        raf.write(tempMsg.getBytes());
                        raf.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String[] arr_data = tempMsg.split(",");
                    //Toast.makeText(MainActivity.this, "data"+arr_data[9], Toast.LENGTH_SHORT).show();
                    // Ref=database.getReference("Data");
                    // Ref.push().setValue(new DataModel(mydate,String.valueOf(lat),String.valueOf(lng).trim(),arr_data[0].trim(),arr_data[1].trim(),arr_data[2].trim(),arr_data[3].trim(),arr_data[4].trim(),arr_data[5].trim(),arr_data[6].trim(),arr_data[7].trim(),arr_data[8].trim(),arr_data[9].trim()));
                    AQI aqi = new AQI();
                    String worning_msg = "";
                    // pm10
                    worning_msg += aqi.aqiTest((float) Double.parseDouble(arr_data[4].trim()), 0, 50, 51, 100, 101, 250, 251, 350, 351, 430, "PM10");
                    // pm2.5
                    //   aqi.aqiTest(Float.parseFloat(arr_data[3].trim()),0,30,31,60,61,90,91,120,121,250,"PM2.5")
                    // worning_msg+=aqi.aqiTest(Float.parseFloat(arr_data[5].trim()),0,40,41,80,81,180,181,280,281,400,"NO2");
                    worning_msg += aqi.aqiTest(Float.parseFloat(arr_data[7].trim()), 0.0f, 1.0f, 1.1f, 2.0f, 2.1f, 10.0f, 10.0f, 17.0f, 17.0f, 34.0f, "CO");
                    worning_msg += aqi.aqiTest(Float.parseFloat(arr_data[6].trim()), 0, 40, 41, 80, 81, 180, 181, 280, 281, 400, "CO2");
                    if (worning_msg.trim() != null && worning_msg.trim() != "" && snooze_count == 0) {
                        massage.setText(worning_msg);
                        //Toast.makeText(MainActivity.this, "" + worning_msg, Toast.LENGTH_SHORT).show();
                        playTone();
                        if (sms_send_time == 120) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage("+919434789009", null, "alert sms:" + worning_msg, null, null);
                            sms_send_time = 0;

                        } else {
                            sms_send_time++;

                        }

                    }
                    if (worning_msg == "" || worning_msg == null) {
                        massage.setText("");

                    }
                    if (snooze_count != 0) {
                        snooze_count--;
                    }


                    if (flag == 1) {
                        // Toast.makeText(MainActivity.this, "dj", Toast.LENGTH_SHORT).show();
                        if (dgas == "pm1") {
                            Log.e("A", "pm1");

                            //Toast.makeText(MainActivity.this, "pm1", Toast.LENGTH_SHORT).show();
                            addEntry(Integer.parseInt(arr_data[2].trim()));
                        } else if (dgas == "pm25") {
                            Log.e("A", "pm25");


                            addEntry(Integer.parseInt(arr_data[3].trim()));
                        } else if (dgas == "pm10") {
                            Log.e("A", "pm10");

                            addEntry(Integer.parseInt(arr_data[4].trim()));

                        } else if (dgas == "no2") {
                            Log.e("A", "no2");

                            addEntry(Integer.parseInt(arr_data[5].trim()));

                        } else if (dgas == "co") {
                            Log.e("A", "co");


                            addEntry(Integer.parseInt(arr_data[7].trim()));
                        } else if (dgas == "co2") {
                            Log.e("A", "co2");


                            addEntry(Integer.parseInt(arr_data[6].trim()));
                        }
                        plotData = false;
                    }

                    msg_box.append(tempMsg);
                    break;
            }
            return true;
        }
    });

    private static void playTone() {

        try {

            if (toneGenerator == null) {
                toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            }
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 900);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (toneGenerator != null) {
                        //Log.d(TAG, "ToneGenerator released");
                        toneGenerator.release();
                        toneGenerator = null;
                    }
                }

            }, 900);
        } catch (Exception e) {
            Log.d("ex", "Exception while playing sound:" + e);
        }
    }

    private void findViewByIds() {

        listen = (Button) findViewById(R.id.listen);
        listDevices = (Button) findViewById(R.id.listDevices);
        //send=(Button)findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.listView);
        msg_box = (TextView) findViewById(R.id.msg);
        status = (TextView) findViewById(R.id.status);
        //writeMsg=(EditText)findViewById(R.id.writeMsg);
        //  upLoad=(Button)findViewById(R.id.uploadbtn);


    }

    private void appendTextAndScroll(String text) {
        if (msg_box != null) {
            msg_box.append(text + "\n");
            final Layout layout = msg_box.getLayout();
            if (layout != null) {
                int scrollDelta = layout.getLineBottom(msg_box.getLineCount() - 1)
                        - msg_box.getScrollY() - msg_box.getHeight();
                if (scrollDelta > 0)
                    msg_box.scrollBy(0, scrollDelta);
            }
        }
    }

    public void btngraph(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_gas);
        dialog.setTitle("Select gas");
        Button btn1 = (Button) dialog.findViewById(R.id.pm1);
        Button btn2 = (Button) dialog.findViewById(R.id.pm25);
        Button btn3 = (Button) dialog.findViewById(R.id.pm10);
        Button btn4 = (Button) dialog.findViewById(R.id.no2);
        Button btn5 = (Button) dialog.findViewById(R.id.co);
        Button btn6 = (Button) dialog.findViewById(R.id.co2);
        flag = 1;
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas = "pm1";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);

                // add empty data
                mChart.setData(data);

                dialog.dismiss();

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas = "pm2.5";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);

                // add empty data
                mChart.setData(data);
                dialog.dismiss();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas = "pm10";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);

                // add empty data
                mChart.setData(data);

                dialog.dismiss();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas = "no2";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);
                dialog.dismiss();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas = "co";
                //data = new LineData();
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);

                dialog.dismiss();
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas = "co2";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    public void btnsnooze(View view) {
        snooze_count = 60;
    }

    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }
                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;

        public ClientClass(BluetoothDevice device1) {
            device = device1;


            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);

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
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            try {
                tempIn = bluetoothSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;

        }

        public void run() {
            byte[] buffer = new byte[1024];
            final byte delimiter = 10;
            int bytes;
            int readBufferPosition = 0;
            while (true) {
                try {

                    int bytesAvailable = inputStream.available();

                    if (bytesAvailable > 0) {
                        byte[] packetByte = new byte[bytesAvailable];
                        inputStream.read(packetByte);
                        for (int i = 0; i < bytesAvailable; i++) {
                            byte b = packetByte[i];
                            if (b == delimiter) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(buffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes);
                                readBufferPosition = 0;
                                handler.obtainMessage(STATE_MESSAGE_RECEIVED, data).sendToTarget();


                            } else {
                                buffer[readBufferPosition++] = b;
                            }
                        }

                    }

                    //bytes = inputStream.read(buffer);
                    //handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configurButton();
        }
    }

    private void configurButton() {
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);

            }
        });

    }

    public void uploadFile(String sourceFileUri) {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        String pathToOurFile = sourceFileUri;
        String urlServer = "http://145.14.145.121:21/test1/UploadToServer.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = connection.getResponseCode();
            //serverResponseMessage = connection.getResponseMessage();

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex)
        {
            //Exception handling
        }

    }

    private void addEntry(int a) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(),  a), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(100);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }


    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
}