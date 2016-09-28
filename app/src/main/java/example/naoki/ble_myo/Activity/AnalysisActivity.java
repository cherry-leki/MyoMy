package example.naoki.ble_myo.Activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

import example.naoki.ble_myo.view.FileDialog;
import example.naoki.ble_myo.view.SampleGraph;
import example.naoki.ble_myo.R;

public class AnalysisActivity extends ActionBarActivity {
    SampleGraph graph;
    int count;
    String txtfile;
    boolean timer_switch=false;

    Button btn_start;
    Button btn_stop;
    TextView txt_avg;
    TextView txt_max;
    TextView txt_min;
    TextView txt_count;
    TextView txt_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        graph=(SampleGraph)findViewById(R.id.cv);

        btn_start=(Button)findViewById(R.id.smaple_start);
        btn_stop=(Button)findViewById(R.id.sample_stop);
        txt_avg=(TextView)findViewById(R.id.txt_avg2);
        txt_max=(TextView)findViewById(R.id.txt_max2);
        txt_min=(TextView)findViewById(R.id.txt_min2);
        txt_count=(TextView)findViewById(R.id.txt_count2);
        txt_time=(TextView)findViewById(R.id.txt_time2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_sample,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_txt:

                new FileDialog(this).setFileListener(new FileDialog.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) {

                        txtfile= load(file.getAbsolutePath());

                        timer_switch=false;
                        count=0;
                        graph.init();
                        txt_avg.setText("null");
                        txt_max.setText("null");
                        txt_min.setText("null");
                        txt_count.setText("null");
                        txt_time.setText("null");
                        graph.invalidate();
                    }
                }).showDialog();

                return true;

            case R.id.action_analysis:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    CountDownTimer timer=new CountDownTimer(1000*1000,30) {
        @Override
        public void onTick(long millisUntilFinished) {
            if(graph.state==true)
            {
                average();
                graph.invalidate();
            }
        }

        @Override
        public void onFinish() {

        }
    };

    public void average()
    {
        String emg_sig[]=new String[2];
        String sig[]=new String[16];
        int temp,result=0;
        int i;
        try{
            emg_sig[0]=txtfile.toString();
            emg_sig[1]=emg_sig[0].split("\n")[count];

            for(i=0;i<16;i++) sig[i]=emg_sig[1].split(",")[i];
            for(i=0;i<16;i++)
            {
                temp=Integer.parseInt(sig[i]);
                if(temp<0) temp*=-1;
                result+=temp;
            }
            result/=16;
            graph.emg_sig.add(result);
            if(graph.emg_capacity==graph.MAX_CAPACITY)
            {
                graph.emg_sig.remove(0);
            }
            else graph.emg_capacity++;

            if(result>graph.max_emg){
                graph.max_emg=result;
            }
            if(result<graph.min_emg){
                graph.min_emg=result;
            }

            graph.maf();
            count++;
            graph.count++;
            graph.time_emg+=0.03;

            graph.average_all=graph.average_all*(float)(count-1)/(float)count+(float)result/(float)count;

            if(graph.average_all< Integer.parseInt(graph.maf_sig.get(graph.maf_capacity-1).toString()))
            {
                if(graph.motion_state==false) graph.motion_state=true;
            }
            else
            {
                if(graph.motion_state==true)
                {
                    graph.motion_state=false;
                    graph.motion_counting++;
                }
            }

            txt_avg.setText(Float.toString(graph.average_all));
            txt_max.setText(Integer.toString(graph.max_emg));
            txt_min.setText(Integer.toString(graph.min_emg));
            txt_count.setText(Integer.toString(graph.motion_counting));
            txt_time.setText(Float.toString(graph.time_emg)+"s");
        }catch (Exception e){
            timer_switch=false;
            //start_stop.setText("다시시작");
            timer.cancel();
        }
    }

    public void sample_startButtonClick(View v){
        if(timer_switch==false){
            timer_switch=true;
            timer.start();
        }
    }

    public void sample_stopButtonClick(View v){
        if(timer_switch==true) {
            timer_switch=false;
            timer.cancel();
        }
    }

    public static String load(String path)
    {
        String sdPath;
        String externalState= Environment.getExternalStorageState();
        if(externalState.equals(Environment.MEDIA_MOUNTED))
        {
            sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        else
        {
            sdPath=Environment.MEDIA_UNMOUNTED;
        }

        String result="";

        try{
            String dir=path;
            File file=new File(dir);
            FileInputStream fis=new FileInputStream(file);
            byte[] buffer=new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            result=new String(buffer);
        }catch(Exception e){}

        return result;
    }

}
