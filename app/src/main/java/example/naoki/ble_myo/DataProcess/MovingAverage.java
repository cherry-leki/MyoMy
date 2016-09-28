package example.naoki.ble_myo.DataProcess;

import java.util.ArrayList;

/**
 * Created by Sabarada on 2016-07-13.
 */
public class MovingAverage {

    public ArrayList<Integer> averageData = new ArrayList<>();

    int judge = 0;
    int buf = 0;
    int degree;

    public void addAverageData(int[] emgData)
    {
        int average = 0;

        for(int i = 0; i < 16; i++)
        {
            average += Math.abs(emgData[i]);
        }

        averageData.add(average / 16);
    }

    public int applyMovingAverage()
    {
        judge = 0;

        if(averageData.size() < 20)
        {
            for(int i = 0; i < averageData.size(); i++)
            {
                judge += averageData.get(i);
            }

            return (judge / averageData.size());
        }
        else{
            for(int i = 0; i < 20; i++)
            {
                judge += averageData.get(i);
            }

            averageData.remove(0);

//            System.out.println(judge / 4);
            judge = (judge / 4);
            degree = (judge) - buf;

            return (buf = judge / 4);
        }
    }

    public int getDegree()
    {
        return degree;
    }
}
