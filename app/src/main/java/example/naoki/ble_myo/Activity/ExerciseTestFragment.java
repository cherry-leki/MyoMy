package example.naoki.ble_myo.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.echo.holographlibrary.LineGraph;

import example.naoki.ble_myo.R;


/**
 * Created by Sabarada on 2016-08-24.
 */
public class ExerciseTestFragment extends Fragment {

    public ButtonInterface buttonInterface;
    public Button startButton;
    public TextView testTimer;
    public LineGraph lineGraph;
    public Handler readyHandler;

    public int counter = 0;
    public int initCounter = 10;

    public ExerciseTestFragment(Handler readyHandler)
    {
        this.readyHandler = readyHandler;
    }

    public interface ButtonInterface
    {
        void switchPrevScreen();
        void startButtonClick(LineGraph lineGraph);
        void nextActivity();
    }

    public void setSwitchScreen(ButtonInterface buttonInterface)
    {
        this.buttonInterface = buttonInterface;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exercisetest, null);

        lineGraph = (LineGraph)view.findViewById(R.id.testGraph);
        startButton = (Button)view.findViewById(R.id.findMyoButton);
        testTimer = (TextView)view.findViewById(R.id.testTimer);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonInterface.startButtonClick(lineGraph);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true)
                        {
                            try {
                                Thread.sleep(1000);
                                initCounter = initCounter - 1;

                                readyHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        testTimer.setText(initCounter + " 초");
                                    }
                                });

                                if(initCounter == 0) break;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        buttonInterface.nextActivity();
                    }
                }).start();
            }
        });

        return view;
    }
}
