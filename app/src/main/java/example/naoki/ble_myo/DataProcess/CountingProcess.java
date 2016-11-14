package example.naoki.ble_myo.DataProcess;

import java.util.ArrayList;

/**
 * Created by Sabarada on 2016-07-20.
 */
public class CountingProcess {

    ArrayList<Integer> list = new ArrayList<>();

    private boolean judge;
    private boolean onTest;
    boolean onMain;
    private boolean onStart;

    private int standard;
    private int nowProcess;
    private int exerciseType;

    private long mStartTime;
    private long startTime;
    private long processTime;

    private int minute = 0;
    private int hour = 0;
    private int type = 0;

    public final static int NONE = 0;
    public final static int TEST_OFF = 1;
    public final static int TEST_ON = 2;
    public final static int MAIN = 3;

    CountingListener countingListener;

    public interface CountingListener
    {
        void addCount();
        void setTime(String time);
    }

    public void setAddCount(CountingListener countingListener)
    {
        this.countingListener = countingListener;
    }


    ArrayList<Integer> standardSum = new ArrayList<>();

    public CountingProcess() {
        standard = 15;
        nowProcess = NONE;
        onMain = false;
        onTest = true;
        judge = false;
        onStart= false;
    }

    public void onTest(int value)
    {
        if (onTest) {
            startTime = System.currentTimeMillis() / 1000;
            onTest = false;
            this.nowProcess = TEST_ON;
            return;
        }

        processTime = (System.currentTimeMillis() / 1000) - startTime;
        if ( processTime  > 8) {
            onMain = true;
            for(int i = 0; i < standardSum.size(); i++)
            {
                standard += standardSum.get(i);
            }

            standard = standard / standardSum.size();
            this.nowProcess = TEST_OFF;
            return;
        }

        standardSum.add(value);
        return;
    }

    boolean timeBoolean = true;
    int second;

    public void judgeDumbbellCounting(int value) {

        if(onStart) {

            if (judge && value < standard) {
                judge = false;
                this.nowProcess = MAIN;
                if(this.exerciseType == 1) countingListener.addCount();

                timeBoolean = true;
            }

            else if (value > standard) {
                String total;
                String secondString;
                String mSecondString;
                String minuteString;

                if (timeBoolean) {
                    mStartTime = System.currentTimeMillis() / 10;       // 밀리초
                    timeBoolean = false;
                }

                long mSecond = (((System.currentTimeMillis() / 10) - mStartTime) % 100);
                long second = (((System.currentTimeMillis() / 10) - mStartTime) / 100);

                if(second > 0)
                {
                    mStartTime = (System.currentTimeMillis() / 10);
                    this.second++;
                }

                if( this.second > 59)
                {
                    minute++;
                    this.second = 0;
                }

                if(minute > 59)
                {
                    hour++;
                    minute = 0;
                }

                if(mSecond < 10) mSecondString = String.format("0%d", mSecond); else mSecondString = String.format("%d", mSecond);
                if(minute < 10) minuteString = String.format("0%d", minute); else minuteString = String.format("%d", minute);
                if(this.second < 10) secondString = String.format("0%d",  this.second); else secondString = String.format("%d", this.second);
                total = String.format("%d:%s:%s.%s", hour, minuteString, secondString, mSecondString);

                if(this.exerciseType == 0) countingListener.setTime(total);
                if (!judge) {
                    judge = true;
                }
            }
        }
    }

    public void setStart()
    {
        onStart = !onStart;
    }

    public long getTime() {
        return (8 - this.processTime);
    }
    public int getState()
    {
        return this.nowProcess;
    }
    public int getStandard()
    {
        return standard;
    }
    public void setExerciseType(int type)
    {
        this.exerciseType = type;
    }
    public void setType(int type)
    {
        this.type = type;
    }
}
