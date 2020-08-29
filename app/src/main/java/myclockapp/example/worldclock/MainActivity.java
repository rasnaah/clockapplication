package myclockapp.example.worldclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    Calendar current;
    Spinner spinner;
    TextView timezone, txtCurrentTime, txtTimeZoneTime;
    long miliSeconds;
    ArrayAdapter<String> idAdapter;
    SimpleDateFormat sdf;
    Date resultDate;

    private Chronometer chronometer;  //
    private boolean running;
    private long pauseOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                intent.putExtra(AlarmClock.EXTRA_HOUR, 10);
                intent.putExtra(AlarmClock.EXTRA_MINUTES, 20);
                startActivity(intent);
            }
        });


        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        //chronometer.setCountDown(true);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 10000){
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(MainActivity.this, "Loading⏳!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        spinner = (Spinner) findViewById(R.id.spinner);

        timezone = (TextView) findViewById(R.id.timezone);
        txtCurrentTime = (TextView) findViewById(R.id.txtCurrentTime);
        txtTimeZoneTime = (TextView) findViewById(R.id.txtTimeZoneTime);

        String[] idArray = TimeZone.getAvailableIDs();

        sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");

        idAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, idArray);
        idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(idAdapter);

        getGMTtime();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                getGMTtime();
                String selectedID = (String) (parent.getItemAtPosition(position));

                TimeZone timeZone = TimeZone.getTimeZone(selectedID);
                String TimeZoneName = timeZone.getDisplayName();

                int TimeZoneOffset = timeZone.getRawOffset() / (60 * 1000);

                int hrs = TimeZoneOffset / 60;
                int mins = TimeZoneOffset % 60;

                miliSeconds = miliSeconds + timeZone.getRawOffset();

                resultDate = new Date(miliSeconds);
                System.out.println(sdf.format(resultDate));

                timezone.setText(TimeZoneName + " : GMT " + hrs + "." + mins);
                txtTimeZoneTime.setText("" + sdf.format(resultDate));
                miliSeconds = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getGMTtime(){
        current = Calendar.getInstance();
        txtCurrentTime.setText("" + current.getTime());

        miliSeconds = current.getTimeInMillis();

        TimeZone tzCurrent = current.getTimeZone();
        int offset = tzCurrent.getRawOffset();
        if(tzCurrent.inDaylightTime(new Date())){
            offset = offset + tzCurrent.getDSTSavings();
        }

        miliSeconds = miliSeconds - offset;

        resultDate = new Date(miliSeconds);
        System.out.println(sdf.format(resultDate));

    }

    public void startChronometer(View v) {
        if(!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }
    public void pauseChronometer(View v) {
        if(running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }
    public void resetChronometer(View v) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;

    }
}

//    Button button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener(){
//@Override
//public void onClick(View v){
//        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
//        intent.putExtra(AlarmClock.EXTRA_HOUR, 10);
//        intent.putExtra(AlarmClock.EXTRA_MINUTES, 20);
//        startActivity(intent);
//        }
//        });

