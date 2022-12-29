package com.example.rgbsensorandphilipshuelight;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.larswerkman.holocolorpicker.ColorPicker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ServiceConfigurationError;

public class MainActivity extends AppCompatActivity {

    TextView textview_r, textview_g,textview_b,textview_cct,textview_illum;
    ImageButton button_bulb, button_circle, button_strip, button_all;
    HttpURLConnection connection;
    HttpRequestGET thread;
    String result = "";
    String[] parsedData;
    Context context;
    View view;
    int brt;
    double x;
    double y;
    private HttpTask httpTask;
    int cct;
    String light="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        textview_r = (TextView) findViewById(R.id.textview_r);
        textview_g = (TextView) findViewById(R.id.textview_g);
        textview_b = (TextView) findViewById(R.id.textview_b);
        textview_cct = (TextView) findViewById(R.id.textview_cct);
        textview_illum = (TextView) findViewById(R.id.textview_illu);
        
        
        button_bulb = (ImageButton) findViewById(R.id.button_bulb);
        button_circle = (ImageButton) findViewById(R.id.button_circle);
        button_strip = (ImageButton) findViewById(R.id.button_strip);
        button_all = (ImageButton) findViewById(R.id.button_all_lights);
        
        thread = new HttpRequestGET(); // http 요청 스레드
        thread.start(); // 스레드 시작

        button_bulb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                light = "bulb";
                showDialogBulb();
            }
        });

        button_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                light = "circle";
                showDialogCircleStripAll();
            }
        });
        button_strip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                light = "strip";
                showDialogCircleStripAll();
            }
        });
        button_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                light = "all";
                showDialogCircleStripAll();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.setRunningState(false);
    }

    public void showDialogBulb(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Light Control");
        view = (View) View.inflate(context, R.layout.dialog, null);
        dialogBuilder.setView(view);
        dialogBuilder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        RadioButton radioButtonRgb = (RadioButton) view.findViewById(R.id.radio_rgb);
        RadioButton radioButtonCct = (RadioButton) view.findViewById(R.id.radio_cct);
        LinearLayout layoutRgb = (LinearLayout) view.findViewById(R.id.layout_rgb);
        LinearLayout layoutCct = (LinearLayout) view.findViewById(R.id.layout_cct);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_rgb:
                        layoutRgb.setVisibility(View.VISIBLE);
                        layoutCct.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radio_cct:
                        layoutRgb.setVisibility(View.INVISIBLE);
                        layoutCct.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        ColorPicker color_picker;
        Button button_color_picker;
        SeekBar seekBar;
        LabeledSwitch labeledSwitch;

        labeledSwitch = (LabeledSwitch) view.findViewById(R.id.switch_onoff);
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
               sendOnOff(isOn);
            }
        });

        color_picker = (ColorPicker) view.findViewById(R.id.color_picker);
        color_picker.setShowOldCenterColor(false);

        seekBar = (SeekBar) view.findViewById(R.id.seekbar_brt);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brt = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button_color_picker = (Button) view.findViewById(R.id.button_color_picker);
        button_color_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendColor(color_picker, brt);
                Toast.makeText(MainActivity.this,"색상 전송",Toast.LENGTH_SHORT).show();
            }
        });

        EditText editTextCct = (EditText) view.findViewById(R.id.edit_text_cct);
        Button buttonCct = (Button) view.findViewById(R.id.button_cct_send);
        buttonCct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cct = Integer.parseInt(editTextCct.getText().toString());
                sendCct(cct);
            }
        });


    };

    public void showDialogCircleStripAll(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Light Control");
        view = (View) View.inflate(context, R.layout.dialog_only_rgb, null);
        dialogBuilder.setView(view);
        dialogBuilder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        ColorPicker color_picker;
        Button button_color_picker;
        SeekBar seekBar;
        LabeledSwitch labeledSwitch;

        labeledSwitch = (LabeledSwitch) view.findViewById(R.id.switch_onoff_2);
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                sendOnOff(isOn);
            }
        });

        color_picker = (ColorPicker) view.findViewById(R.id.color_picker_2);
        color_picker.setShowOldCenterColor(false);

        seekBar = (SeekBar) view.findViewById(R.id.seekbar_brt_2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brt = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button_color_picker = (Button) view.findViewById(R.id.button_color_picker_2);
        button_color_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendColor(color_picker, brt);
                Toast.makeText(MainActivity.this,"색상 전송",Toast.LENGTH_SHORT).show();
            }
        });
    };

    public void sendOnOff(boolean on){
        httpTask = new HttpTask();
        httpTask.setLight(light);
        if(!on){
            httpTask.setOff();
        }else{
            httpTask.setOn();
        }
        httpTask.execute();
    }

    public void sendCct(int cct){
        httpTask = new HttpTask();
        httpTask.setLight(light);
        httpTask.setCct(brt, cct);
        httpTask.execute();
    }
    public void sendColor(ColorPicker picker, int brt){
        int color = picker.getColor();
        int r,g,b;
        r = Color.red(color);
        g = Color.green(color);
        b = Color.blue(color);
        RGBtoXY(r,g,b);

        httpTask = new HttpTask();
        httpTask.setLight(light);
        httpTask.setColor(brt, x, y);
        httpTask.execute();
    }
    public void RGBtoXY(int R, int G, int B){
        double X = (0.4887180*R) + (0.3106803*G) + (0.2006017*B);
        double Y = (0.1762044*R) + (0.8129847*G) + (0.0108109*B);
        double Z = (0.0*R) + (0.0102048*G) + (0.9897952*B);

        x = X/(X+Y+Z);
        y = Y/(X+Y+Z);

    }


    class HttpRequestGET extends Thread {
        private boolean isRunning = true;

        public void run() {
            while (isRunning) {
                try {
                    Thread.sleep(3000);
                    URL url = new URL("http://210.102.142.15:5000/rgbSensorValue");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET"); //전송방식
                    connection.setDoOutput(false);       //데이터를 쓸 지 설정
                    connection.setDoInput(true);        //데이터를 읽어올지 설정

                    Log.d("log", ">>>>>>>> GET 요청");
                    InputStream is = connection.getInputStream();
                    StringBuffer sb = new StringBuffer();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    result = sb.toString();
                    br.close();
                    Log.d("Log", ">>>>>>>> GET 완료 : " + result);
                    DataParsing dataParsing = new DataParsing(); // 데이터 파싱 객체 생성
                    parsedData = dataParsing.getParsedData(result); // 데이터 파싱

                    runOnUiThread(new Runnable() { // 텍스트뷰 텍스트 바꾸기
                        @Override
                        public void run() {
                            textview_r.setText(parsedData[0]);
                            textview_g.setText(parsedData[1]);
                            textview_b.setText(parsedData[2]);
                            textview_cct.setText(parsedData[3]+" K");
                            textview_illum.setText(parsedData[4]+" LUX");
                        }
                    });

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void setRunningState(boolean state){
            isRunning = state;
        }


    }
}