package com.demo.simpletimer.simpletimerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.ben.timer.SimpleTimer;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class MainActivity extends Activity {
    private Handler mHandle = new Handler(Looper.getMainLooper());
    private CheckBox mUITimerCheckbox;
    private StringAdapter mLogAdapter;
    private SparseArray<Integer> mTimerConfig;
    private ArrayList<SimpleTimer> mTimerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUITimerCheckbox = (CheckBox) findViewById(R.id.cb_run_in_ui);
        findViewById(R.id.start_timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTimer(mUITimerCheckbox.isChecked());
            }
        });

        findViewById(R.id.stop_timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SimpleTimer timer :
                        mTimerList) {
                    timer.stop();
                }
                mTimerList.clear();
            }
        });

        findViewById(R.id.refresh_timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SimpleTimer timer :
                        mTimerList) {
                    timer.start();
                }
                mLogAdapter.clear();
            }
        });


        mLogAdapter = new StringAdapter(this);
        ListView logView = (ListView) findViewById(R.id.log_list_view);
        logView.setAdapter(mLogAdapter);

        mTimerConfig = new SparseArray<>();
        mTimerConfig.put(0, 0);
        mTimerConfig.put(1000, 0);
        mTimerConfig.put(1000, -1);
        mTimerConfig.put(3000, 3);
        mTimerConfig.put(-1000, 1);
        mTimerConfig.put(-3000, 3);

        mTimerList = new ArrayList<>();
    }

    private void runTimer( boolean runInUiThread ){
        for(int i = 0; i < mTimerConfig.size(); i++) {
            int delay = mTimerConfig.keyAt(i);
            int repeats = mTimerConfig.valueAt(i);

            SimpleTimer tmp = new SimpleTimer(delay, repeats,new TimerRunnable( delay, repeats, runInUiThread ))
                    .runInUIThread(runInUiThread);

            tmp.start();
            mTimerList.add(tmp);
        }
    }

    private void appendLog(int second, int repeats, int runTimes, boolean inUiThread) {
        StringBuilder builder = new StringBuilder((inUiThread ? "【main】" : "【thread】") + "run timer: ");
        String log = builder.append(second/1000).append("second ").append(runTimes + "/" + repeats).append("repeats").toString();
        mLogAdapter.appLog(log);
    }

    private boolean inUIThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private class TimerRunnable implements Runnable{
        int second;
        int repeats;
        boolean inUiThread;
        int runTimes;
        public TimerRunnable( int second, int repeats, boolean inUiThread  ){
            this.repeats = repeats;
            this.second = second;
            this.inUiThread = inUiThread;
            runTimes = 0;
        }

        @Override
        public void run() {
            ++runTimes;
            if ( inUIThread() ){
                appendLog(second, repeats, runTimes, inUiThread);
            }
            else {
                mHandle.post(this);
            }
        }
    }
}
