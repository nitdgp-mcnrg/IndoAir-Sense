package com.mnk.env;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.mnk.env.Bluetooth_set.STATE_CONNECTED;
import static com.mnk.env.Bluetooth_set.STATE_CONNECTING;
import static com.mnk.env.Bluetooth_set.STATE_CONNECTION_FAILED;
import static com.mnk.env.Bluetooth_set.STATE_LISTNING;
import static com.mnk.env.Bluetooth_set.STATE_MESSAGE_RECEIVED;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class Datasets extends AppCompatActivity implements View.OnClickListener {

    BluetoothSocket socket;
    Datasets.SendReceive sendReceive, sendReceive1;
    BluetoothAdapter bluetoothAdapter;
    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String APP_NAME = "IndoAir";
    private File file;

    private ToneGenerator toneGenerator;
    int snooze_count = 0;

    private FirebaseDatabase database;
    private DatabaseReference pointToHead;
    private DatabaseReference Ref;
    private DatabaseReference classInfoRef;
    private AlertDialog alertDialog;
    private LinearLayout manualClockSyncLayout;

    private Constants constants = null;
    int sms_send_time = 0;
    private TextView data_msg, alertmsg;
    private String InstituteName, ClassRoomId, Occupants, AC, Fans, Window, Doors, StartTime, EndTime;
    private String date, pathToCapturedFile;
    private static int REQUEST_CODE = 1;
    private TextView status;
    private EditText manualSyncClockEditText, intervalDelayEditText;
    //private SharedPreferences sharedPreferences;
    private String DeviceID, locationType, placeName, roomName, currentManualSyncType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datasets);

        date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        pointToHead = database.getReference();
        setDataFirebaseReference();

        findViewByIds();

        data_msg.setMovementMethod(new ScrollingMovementMethod());
        constants = new Constants();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        findViewById(R.id.imageCaptureButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraToCaptureImage();
            }
        });
        //sendReceive1 = new Datasets.SendReceive(Bluetooth_set.socket);
        //sendReceive1.start();
        Datasets.ClientClass clientClass = new Datasets.ClientClass(Bluetooth_set.connectbt);
        clientClass.start();
    }

    private void openCameraToCaptureImage() {
        // 1. define an intent to capture an image of action type as ACTION_IMAGE_CAPTURE
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 2. check whether any app is available or not to capture an image
        if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
            // 3. create an file which store our captured image
            File capturedFile = createImageFile();
            //4. check whether file is created successfully or not
            if (capturedFile != null) {
                // 5. take the captured image absolute path and create an Uri for that image file
                pathToCapturedFile = capturedFile.getAbsolutePath();
                Uri capturedImageUri = FileProvider.getUriForFile(getApplicationContext(), "com.mnk.env.fileprovider", capturedFile);
                // 6. put that Uri in the Intent and start the intent for Result
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                startActivityForResult(captureImageIntent, REQUEST_CODE);
            }
        }
    }

    private File createImageFile() {
        // set image file name as the current date and time
        String capturedFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //the below method create a file directory made that file to accessible for all the apps in the phone
        File storageDirectory = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // now create a file of image type
        File imageFile = null;

        try {
            imageFile = File.createTempFile(capturedFileName, ".jpg", storageDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: "+ e.toString(), Toast.LENGTH_SHORT).show();
        }
        // image file created successfully
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                // 7. decode the file from the stored path and set it to the imageView
                Bitmap bitmap = BitmapFactory.decodeFile(pathToCapturedFile);

                // 8. make a provider in the manifest file
                /*
                1. name as FileProvider
                2. authorities should be same as in the Uri activity
                3. exported false means file provider is not publicly accessible
                4. grantUriPermissions true means our file can be accessible by other apps with same permissions as our app has
                5. create meta data for the FileProvider
                    5(a). name as anything
                    5(b). resource as an xml file with root element as paths and make an entry in it of external paths with name as you want and the path of the image file in android phone

                 */
            }
        }
    }

    private void setDataFirebaseReference() {
        SharedPreferences sharedPreferences = getSharedPreferences("Location_Data", MODE_PRIVATE);
        DeviceID = sharedPreferences.getString("DeviceId", "No_ID");
        locationType = sharedPreferences.getString("Place_Type", "LOCATION_TYPE");
        placeName = sharedPreferences.getString("Place_Name", "PLACE_NAME");
        roomName = sharedPreferences.getString("Room_Name", "ROOM_NAME");

        if (locationType.equals("OUTDOOR")) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            Ref = FirebaseDatabase.getInstance().getReference("Air_Related_Data").child(locationType).child(placeName).child(date).child("Pollution_Data");
        } else {
            Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
            Ref = FirebaseDatabase.getInstance().getReference("Air_Related_Data").child(locationType).child(placeName).child(roomName).child(date).child("Pollution_Data");
        }
    }

    private void findViewByIds() {
        data_msg = findViewById(R.id.data_msg);
        alertmsg = findViewById(R.id.alertmsg);
        status = findViewById(R.id.status);

        findViewById(R.id.start_time).setOnClickListener(this);
        findViewById(R.id.change_time).setOnClickListener(this);
        findViewById(R.id.end_time).setOnClickListener(this);
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
                    String readBuffr = (String) msg.obj;

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
                    String dispaly_data = data_msg.getText().toString() + "\n\n" + tempMsg;

                    data_msg.setText(dispaly_data);


                    AQI aqi = new AQI();
                    String worning_msg = "";
                    if (arr_data.length > 9) {
                        String _id = Ref.push().getKey();
                        Ref.child(_id).setValue(new DataModel(_id,
                                java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
                                String.valueOf(0.00), String.valueOf(0.00).trim(),
                                arr_data[2].trim(), arr_data[3].trim(), arr_data[4].trim(), arr_data[5].trim(),
                                arr_data[6].trim(), arr_data[7].trim(), arr_data[8].trim(), arr_data[9].trim()));

                        worning_msg += aqi.aqiTest((float) Double.parseDouble(arr_data[4].trim()), 0, 50, 51, 100, 101, 250, 251, 350, 351, 430, "PM10");
                        worning_msg += aqi.aqiTest(Float.parseFloat(arr_data[3].trim()), 0, 30, 31, 60, 61, 90, 91, 120, 121, 250, "PM2.5");
                        worning_msg += aqi.aqiTest(Float.parseFloat(arr_data[7].trim()), 0.0f, 1.0f, 1.1f, 2.0f, 2.1f, 10.0f, 10.0f, 17.0f, 17.0f, 34.0f, "CO");
                        worning_msg += aqi.aqiTest(Float.parseFloat(arr_data[6].trim()), 0, 40, 41, 80, 81, 180, 181, 280, 281, 400, "CO2");
                        if (worning_msg.trim() != null && worning_msg.trim() != "" && snooze_count == 0) {
                            alertmsg.setText(worning_msg);
                            //Toast.makeText(MainActivity.this, "" + worning_msg, Toast.LENGTH_SHORT).show();
                            playTone();
                            if (sms_send_time == 120) {
                                //SmsManager smsManager = SmsManager.getDefault();
                                //smsManager.sendTextMessage("+919434789009", null, "alert sms:"+worning_msg, null, null);
                                //  sms_send_time=0;

                            } else {
                                sms_send_time++;

                            }

                            if (worning_msg == "" || worning_msg == null) {
                                alertmsg.setText("");

                            }
                            if (snooze_count != 0) {
                                snooze_count--;
                            }
                        }
                    }
            }
            return true;
        }
    });

    void playTone() {

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
                        toneGenerator.release();
                        toneGenerator = null;
                    }
                }

            }, 900);
        } catch (Exception e) {
            Log.d("ex", "Exception while playing sound:" + e);
        }
    }

    String class_status = null;

    @Override
    public void onClick(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.starttime_dialog, null);
        dialogBuilder.setView(dialogView);
        final TextView textViewTime = dialogView.findViewById(R.id.textViewTime);
        final EditText editTextOccupants = dialogView.findViewById(R.id.occupants);
        final EditText editTextAC = dialogView.findViewById(R.id.ac);
        final EditText editTextFans = dialogView.findViewById(R.id.fan);
        final EditText editTextWindow = dialogView.findViewById(R.id.window);
        final EditText editTextDoors = dialogView.findViewById(R.id.door);
        final EditText editTextStartTime = dialogView.findViewById(R.id.start_time);
        final Button saveClassInfo = dialogView.findViewById(R.id.saveClassInfo);

        switch (v.getId()) {
            case R.id.start_time:
                textViewTime.setText(R.string.class_started);
                class_status = "Class Started";
                break;

            case R.id.change_time:
                textViewTime.setText(R.string.class_middle);
                class_status = "Changes in Middle of Class";
                break;

            case R.id.end_time:
                textViewTime.setText(R.string.class_ended);
                class_status = "Class Ended";
                break;
        }
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        saveClassInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Occupants = editTextOccupants.getText().toString().trim();
                AC = editTextAC.getText().toString().trim();
                Fans = editTextFans.getText().toString().trim();
                Window = editTextWindow.getText().toString().trim();
                Doors = editTextDoors.getText().toString().trim();
                StartTime = editTextStartTime.getText().toString().trim();
                String s = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                EndTime = null;
                switch (v.getId()) {
                    case R.id.change_time:
                        StartTime = null;
                        EndTime = null;
                        break;

                    case R.id.end_time:
                        StartTime = null;
                        EndTime = StartTime;
                        break;
                }

                classInfoRef = database.getReference("Air Related Data").child(locationType).child(placeName).child(roomName).child(date).child("Classroom Data");
                ClassRoomData classRoomInfo = new ClassRoomData(class_status, Doors, AC, StartTime, EndTime, Window, Occupants, Fans);
                classInfoRef.push().setValue(classRoomInfo);

                alertDialog.dismiss();
            }
        });
    }

    public void syncClock(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.sync_clock, null);

        manualClockSyncLayout = customLayout.findViewById(R.id.manualClockSyncLayout);
        manualSyncClockEditText = customLayout.findViewById(R.id.manualClockEditText);

        customLayout.findViewById(R.id.currentClockSyncRadioButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manualClockSyncLayout.setVisibility(View.GONE);
                currentManualSyncType = "Current";
            }
        });

        customLayout.findViewById(R.id.manualClockSyncRadioButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manualClockSyncLayout.setVisibility(View.VISIBLE);
                currentManualSyncType = "Manual";
            }
        });

        customLayout.findViewById(R.id.currentManualClockSync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String manualDateTime = "setdtime ";
                if (currentManualSyncType.equals("Current")) {
                    //sync to the current date and time
                    Date currentDate = Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy hhmmss");
                    manualDateTime = manualDateTime + simpleDateFormat.format(currentDate);
                } else if (currentManualSyncType.equals("Manual")) {
                    //sync to the manual date and time
                    manualDateTime = manualDateTime + manualSyncClockEditText.getText().toString().trim();
                }
                sendReceive.write(manualDateTime);
                alertDialog.dismiss();
            }
        });


        builder.setView(customLayout);
        alertDialog = builder.create();
        //alertDialog.setCanceledOnTouchOutside(false);
        //alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void setIntervalDelay(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.set_delay, null);

        intervalDelayEditText = customLayout.findViewById(R.id.intervalDelayEditText);

        customLayout.findViewById(R.id.setIntervalDelayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String setDelayMessage = "setdelay ";
                setDelayMessage = setDelayMessage + intervalDelayEditText.getText().toString().trim();
                sendReceive.write(setDelayMessage);
                alertDialog.dismiss();
            }
        });

        builder.setView(customLayout);
        alertDialog = builder.create();
        //alertDialog.setCanceledOnTouchOutside(false);
        //alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private class ClientClass extends Thread {
        BluetoothDevice device;

        public ClientClass(BluetoothDevice device1) {
            device = device1;
            socket = Bluetooth_set.socket;
        }

        public void run() {
            Message message = Message.obtain();
            message.what = STATE_CONNECTED;
            handler.sendMessage(message);
            sendReceive = new Datasets.SendReceive(Bluetooth_set.socket);
            sendReceive.start();
        }
    }

    private class Serverclass extends Thread {
        private BluetoothServerSocket serverSocket;

        public Serverclass() {
            try {
                serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, Bluetooth_set.MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message1 = Message.obtain();
                    message1.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message1);
                }
                if (socket != null) {
                    Message message1 = Message.obtain();
                    message1.what = STATE_CONNECTING;
                    handler.sendMessage(message1);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    public void btnsnooze(View view) {
        snooze_count = 60;
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        InputStream inputStream;
        OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;

            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[2048];
            final byte delimiter = 10;
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(String str) {
            try {
                outputStream.write(str.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}


/*
            sharedPreferences = getSharedPreferences("Location_Data", MODE_PRIVATE);
            DeviceID = sharedPreferences.getString("DeviceId", "");
            Toast.makeText(this, DeviceID, Toast.LENGTH_SHORT).show();

            FirebaseApp.initializeApp(this);
            database=FirebaseDatabase.getInstance();
            Bundle bundle = getIntent().getExtras();
            if(bundle != null)
            {
                InstituteName = bundle.getString(constants.INSTITUTE_ID);
                ClassRoomId = bundle.getString(constants.CLASSROOM_ID);
                Occupants = bundle.getString(constants.OCCUPANTS_ID);
                AC = bundle.getString(co nstants.AC_ID);
                Fans = bundle.getString(constants.FANS_ID);
                Doors = bundle.getString(constants.DOORS_ID);
                Window = bundle.getString(constants.WINDOW_ID);
                StartTime = bundle.getString(constants.START_TIME_ID);
                EndTime = bundle.getString(constants.END_TIME_ID);

                //Toast.makeText(this, "H: "+StartTime, Toast.LENGTH_SHORT).show();
                //String date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                classInfoRef=database.getReference(DeviceID).child("Indoor").child(InstituteName)
                        .child(ClassRoomId).child("Classroom Data")
                        .child(date);
                String timeStamp = DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
                Toast.makeText(this, "Time: "+timeStamp, Toast.LENGTH_SHORT).show();

                if (Occupants == null)
                    class_status = "Class Not Started";
                if(StartTime==null)
                    StartTime=timeStamp;
                classInfoRef.push().setValue(new ClassRoomData(class_status, Doors, AC, StartTime, EndTime, Window,Occupants, Fans));
            }
         */