package com.mnk.env;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.mnk.env.Bluetooth_set.STATE_CONNECTED;
import static com.mnk.env.Bluetooth_set.STATE_CONNECTING;
import static com.mnk.env.Bluetooth_set.STATE_CONNECTION_FAILED;
import static com.mnk.env.Bluetooth_set.STATE_LISTNING;
import static com.mnk.env.Bluetooth_set.STATE_MESSAGE_RECEIVED;
import static com.mnk.env.Bluetooth_set.connectbt;


public class Graphset extends AppCompatActivity {

    //BluetoothSocket socket;
    Graphset.SendReceive sendReceive;
    private Thread thread;
    //private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    File file;
    int flag = 0;
    String mydate;
    int snooze_count = 0;

    int sms_send_time = 0;
    //private TextView massage;
    //private boolean plotData = true;
    String dgas = null;
    //private TextView msg_box;
    private TextView status;
    private LineChart mChart;
    Button btn1,btn2,btn3,btn4,btn5,btn6;
    boolean con=true;

    private ArrayList<String> xAxisDate;
    private String stringValue;

    private static final float TOTAL_MEMORY = 16.0f;
    private static final float LIMIT_MAX_MEMORY = 12.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphset);
        mChart = (LineChart) findViewById(R.id.lineChart);
        btn1 = (Button) findViewById(R.id.pm1);
        btn2 = (Button) findViewById(R.id.pm25);
        btn3 = (Button) findViewById(R.id.pm10);
        btn4 = (Button) findViewById(R.id.no2);
        btn5 = (Button) findViewById(R.id.co);
        btn6 = (Button) findViewById(R.id.co2);
        status=(TextView) findViewById(R.id.status_graph);
        //massage=(TextView) findViewById(R.id.massage);

        xAxisDate = new ArrayList<>();
        button();
        flag=1;

        setUpChart();
        setUpAxes();
        setUpData();
        setLegend();

        feedMultiple();
        mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        Graphset.ClientClass clientClass=new Graphset.ClientClass(connectbt);
        clientClass.start();
        status.setText("Connecting");
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String currentDate = sdf.format(new Date());
            String filename=currentDate;
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
                    xAxisDate.add(arr_data[1]);
                    //Toast.makeText(MainActivity.this, "data"+arr_data[9], Toast.LENGTH_SHORT).show();
                    // Ref=database.getReference("Data");
                    // Ref.push().setValue(new DataModel(mydate,String.valueOf(lat),String.valueOf(lng).trim(),arr_data[0].trim(),arr_data[1].trim(),arr_data[2].trim(),arr_data[3].trim(),arr_data[4].trim(),arr_data[5].trim(),arr_data[6].trim(),arr_data[7].trim(),arr_data[8].trim(),arr_data[9].trim()));
                    AQI aqi = new AQI();
                    String worning_msg = "";
                    if (arr_data.length > 7) {
                        // pm10
                        worning_msg += aqi.aqiTest((float) Double.parseDouble(arr_data[4].trim()), 0, 50, 51, 100, 101, 250, 251, 350, 351, 430, "PM10");
                        // pm2.5
                        //   aqi.aqiTest(Float.parseFloat(arr_data[3].trim()),0,30,31,60,61,90,91,120,121,250,"PM2.5")
                        // worning_msg+=aqi.aqiTest(Float.parseFloat(arr_data[5].trim()),0,40,41,80,81,180,181,280,281,400,"NO2");
                        worning_msg += aqi.aqiTest(Float.parseFloat(arr_data[7].trim()), 0.0f, 1.0f, 1.1f, 2.0f, 2.1f, 10.0f, 10.0f, 17.0f, 17.0f, 34.0f, "CO");
                        worning_msg += aqi.aqiTest(Float.parseFloat(arr_data[6].trim()), 0, 40, 41, 80, 81, 180, 181, 280, 281, 400, "CO2");
                        if (worning_msg.trim() != null && worning_msg.trim() != "" && snooze_count == 0) {
                            //massage.setText(worning_msg);
                            //Toast.makeText(MainActivity.this, "" + worning_msg, Toast.LENGTH_SHORT).show();
                            //  playTone();
                            if (sms_send_time == 120) {
                                //    SmsManager smsManager = SmsManager.getDefault();
                                //  smsManager.sendTextMessage("+919434789009", null, "alert sms:"+worning_msg, null, null);
                                // sms_send_time=0;

                            } else {
                                sms_send_time++;

                            }

                        }
                        if (worning_msg == "" || worning_msg == null) {
                            // massage.setText("");

                        }
                        if (snooze_count != 0) {
                            snooze_count--;
                        }

                        Toast.makeText(Graphset.this, arr_data[2], Toast.LENGTH_SHORT).show();

                        if (flag == 1) {
                            // Toast.makeText(MainActivity.this, "dj", Toast.LENGTH_SHORT).show();
                            if (dgas == "pm1") {
                                Log.e("A", "pm1");

                                //Toast.makeText(MainActivity.this, "pm1", Toast.LENGTH_SHORT).show();
                                addEntry(Integer.parseInt(arr_data[2].trim()));
                            } else if (dgas == "pm2.5") {
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
                            //plotData = false;
                        }

//                        msg_box.append(tempMsg);

                    }
            }
            return true;
        }
    });


    private void feedMultiple() {
        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    //plotData = true;
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

    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1){
            device=device1;
            socket = FunctionalitiesActivity.bluetoothSocket;
        }

        public void run(){
            Message message=Message.obtain();
            message.what=STATE_CONNECTED;
            handler.sendMessage(message);
            sendReceive=new Graphset.SendReceive(socket);
            sendReceive.start();
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        public SendReceive(BluetoothSocket socket){
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;
            try {
                tempIn=bluetoothSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream=tempIn;
            outputStream=tempOut;

        }

        public void run()
        {
            byte [] buffer=new byte[1024];
            final byte delimiter = 10;
            int bytes;
            int readBufferPosition = 0;
            while(con){
                try{

                    int bytesAvailable=inputStream.available();

                    if(bytesAvailable>0){
                        byte[] packetByte=new byte[bytesAvailable];
                        inputStream.read(packetByte);
                        for(int i=0;i<bytesAvailable;i++){
                            byte b=packetByte[i];
                            if(b==delimiter){
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(buffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes);
                                readBufferPosition = 0;
                                handler.obtainMessage(STATE_MESSAGE_RECEIVED,data).sendToTarget();
                            }
                            else
                            {
                                buffer[readBufferPosition++] = b;
                            }
                        }

                    }
                    //bytes = inputStream.read(buffer);
                    //handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e){e.printStackTrace();}
            }
        }

        public void write(byte[] bytes){
            try{
                outputStream.write(bytes);
            } catch (IOException e){e.printStackTrace();}
        }
    }

    private void setUpChart() {
        // enable/disable description text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(true);
        mChart.setHighlightPerDragEnabled(true);
        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);
        mChart.setDrawBorders(true);
    }

    private void setUpAxes() {
        //ssetUp Axis
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setAxisMinimum(-1f);
        //xl.setSpaceMin(100f);
        //xl.setSpaceMax(100f);
        xl.setLabelRotationAngle(-90);
        xl.setValueFormatter(new IAxisValueFormatter() {
            SimpleDateFormat mFormat = new SimpleDateFormat("dd.MM");
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (xAxisDate.size() >= 0 && value >= 0) {
                    if (value < xAxisDate.size()) {
                        stringValue = xAxisDate.get((int)value);
                    } else {
                        stringValue = "";
                    }
                } else {
                    stringValue = "";
                }
                return stringValue;
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        //leftAxis.setAxisMaximum(TOTAL_MEMORY);
        //leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        /*
        // Add a limit line
        LimitLine ll = new LimitLine(LIMIT_MAX_MEMORY, "Upper Limit");
        ll.setLineWidth(2f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll.setTextSize(10f);
        ll.setTextColor(Color.WHITE);
        // reset all limit lines to avoid overlapping lines
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
         */
    }

    private void setUpData() {
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        // add empty data
        mChart.setData(data);
    }

    private void setLegend() {
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(Color.WHITE);
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

            // let the chart know it's data has changed
            data.notifyDataChanged();
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
        set.setColor(Color.MAGENTA);
        //set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setCircleColor(Color.RED);
        set.setLineWidth(1f);
        set.setCircleRadius(4f);
        set.setValueTextColor(Color.BLUE);
        set.setValueTextSize(10f);
        //set.setHighlightEnabled(true);
        set.setDrawValues(true);
        //set.setDrawCircles(true);
        //set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //set.setCubicIntensity(0.2f);
        return set;
    }

    void button() {
        flag=1;
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas="pm1";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);

                // add empty data
                mChart.setData(data);



            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas="pm2.5";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);

                // add empty data
                mChart.setData(data);

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas="pm10";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);

                // add empty data
                mChart.setData(data);


            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas="no2";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);

            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas="co";
                //data = new LineData();
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);


            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgas="co2";
                mChart.clear();
                mChart = (LineChart) findViewById(R.id.lineChart);
                LineData data = new LineData();
                data.setValueTextColor(Color.WHITE);
                mChart.setData(data);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        con=false;
    }
}
