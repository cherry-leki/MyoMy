package example.naoki.ble_myo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SampleGraph extends View {
    public SampleGraph(Context context){
        super(context);
    }

    public SampleGraph(Context context, AttributeSet att){
        super(context,att);
    }

    public SampleGraph(Context context, AttributeSet att, int ref){
        super(context,att,ref);
    }

    public int count=0;
    public int motion_counting=0;
    public float average_all=0;
    public float time_emg=0;
    public int max_emg=0;
    public int min_emg=0;
    public int emg_capacity=0;
    public int maf_capacity=0;
    public int MAX_CAPACITY=330;
    public int AMP_Y=8;
    public int AMP_X=2;
    public int X_SPAN=40;
    public int Y_SPAN=12;
    public int sec_cal;
    public int sec_slide;
    public List emg_sig=new ArrayList();
    public List maf_sig=new ArrayList();
    public boolean state=true;
    public boolean motion_state=false;

    @Override
    public void onDraw(Canvas c){
        int x=c.getWidth();
        int y=c.getHeight();
        if(state){
            Paint paint=new Paint();
            Paint backLine=new Paint();
            Paint line1=new Paint();
            Paint line2=new Paint();
            paint.setColor(Color.rgb(0,0,0));
            backLine.setColor(Color.rgb(150,150,150));
            line1.setColor(Color.rgb(255,0,0));
            line2.setColor(Color.rgb(0,0,255));
            c.drawLine(40,0,40,y-Y_SPAN,paint);
            c.drawLine(40,y-Y_SPAN,700,y-Y_SPAN,paint);

            if(count<=330)
            {
                sec_cal = 0;
                sec_slide=0;
            }
            else
            {
                sec_cal=count-330;
                sec_cal/=33;
                sec_slide=count%33;
            }

            for(int i=1;i<8;i++)
            {
                c.drawLine(X_SPAN, y-i*10*AMP_Y-Y_SPAN, X_SPAN+330*AMP_X, y-i*10*AMP_Y-Y_SPAN,backLine);
                c.drawText(Integer.toString(i*10),20,y-i*10*AMP_Y-Y_SPAN+5,paint);
            }
            for(int i=1;i<11;i++)
            {
                c.drawLine(i*33*AMP_X+X_SPAN-sec_slide*AMP_X, y-Y_SPAN, i*33*AMP_X+X_SPAN-sec_slide*AMP_X, 0,backLine);
                c.drawText(Integer.toString(i+sec_cal),i*33*AMP_X+X_SPAN-2-sec_slide*AMP_X,y,paint);
            }

            for(int i=0;i<emg_capacity;i++){
                if(i==0)
                {
                    c.drawLine(X_SPAN, y- Integer.parseInt(emg_sig.get(i).toString())*AMP_Y-Y_SPAN, 1*AMP_X+X_SPAN, y- Integer.parseInt(emg_sig.get(i).toString())*AMP_Y-Y_SPAN,line2);
                }
                else
                {
                    c.drawLine(i*AMP_X-1*AMP_X+X_SPAN, y- Integer.parseInt(emg_sig.get(i-1).toString())*AMP_Y-Y_SPAN, i*AMP_X+X_SPAN,y- Integer.parseInt(emg_sig.get(i).toString())*AMP_Y-Y_SPAN,line2);
                }
            }
        }
    }

    public void maf() //이동평균
    {
        int sum=0;
        if(emg_capacity<20)
        {
            for(int i=0;i<emg_capacity;i++)
            {
                //sum+=emg_sig[i];
                sum+= Integer.parseInt(emg_sig.get(i).toString());
            }
            sum/=emg_capacity;
            //maf_sig[count]=sum;
            maf_sig.add(sum);
        }
        else
        {
            for(int i=emg_capacity-20;i<emg_capacity;i++)
            {
                //sum+=emg_sig[i];
                sum+= Integer.parseInt(emg_sig.get(i).toString());
            }
            sum/=20;
            //maf_sig[count]=sum;
            maf_sig.add(sum);
        }

        if(sum>50) AMP_Y=4;
        else if(sum>30) AMP_Y=6;

        if(maf_capacity==MAX_CAPACITY)
        {
            maf_sig.remove(0);
        }
        else maf_capacity++;
    }

    public void init()
    {
        count=0;
        motion_counting=0;
        emg_capacity=0;
        maf_capacity=0;
        average_all=0;
        time_emg=0;
        max_emg=0;
        min_emg=10000;
        emg_sig.clear();
        maf_sig.clear();
    }
}
