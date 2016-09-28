package example.naoki.ble_myo.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.echo.holographlibrary.LineGraph;

import java.util.HashMap;

import example.naoki.ble_myo.Interface.GetSetupData;
import example.naoki.ble_myo.R;
import example.naoki.ble_myo.view.SetupDialog;
import example.naoki.ble_myo.view.TextProgressBar;

/**
 * Created by Sabarada on 2016-09-21.
 */
public class MainFragment extends Fragment {

    private TextView gestureText;
    private TextView timeTextView;
    private TextView healthTextView;

    private boolean onEMG = false;

    private LineGraph graph;
    private SetupDialog setupDialog;

    private Button startButton;
    private Button setupButton;

    private TextProgressBar counterProgressBar;
    private TextProgressBar setProgressBar;
    private ProgressBar timePorgressBar;

    public int valueHours = 0;
    public int valueMinutes = 0;
    public int valueSeconds = 0;

    public Context context;
    public MainFragmentListener mainFragmentListener;
    HashMap<String, View> views = new HashMap<String, View>();

    public interface MainFragmentListener {
        void startButtonClick(HashMap<String, View> views);
        void initialButtonClick();
    }

    public void setMainFragmentListener(MainFragmentListener mainFragmentListener) {
        this.mainFragmentListener = mainFragmentListener;
    }

    public MainFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        graph = (LineGraph) view.findViewById(R.id.holo_graph_view);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.graph);
        BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
        graph.setBackground(bd);

        gestureText = (TextView) view.findViewById(R.id.gestureTextView);

        startButton = (Button) view.findViewById(R.id.start_button);
        startButton.setOnClickListener(startButtonClick);

        setupButton = (Button) view.findViewById(R.id.setup_button);
        setupButton.setOnClickListener(setupButtonClick);

        counterProgressBar = (TextProgressBar) view.findViewById(R.id.counterProgressBar);
        setProgressBar = (TextProgressBar) view.findViewById(R.id.setProgressBar);
        timeTextView = (TextView) view.findViewById(R.id.timeText);
        healthTextView = (TextView) view.findViewById(R.id.healthStatus);


        // 크기 수정 필요.
        counterProgressBar.setScaleY(2f);
        setProgressBar.setScaleY(2f);

        counterProgressBar.setText("설정 필요");
        setProgressBar.setText("설정 필요");

        timePorgressBar = (ProgressBar) view.findViewById(R.id.timeProgressBar);

        Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
        an.setFillAfter(true);
        timePorgressBar.startAnimation(an);

        setupDialog = new SetupDialog(context);
        setupDialog.setSetupData(new GetSetupData() {
            @Override
            public void getCountData(int counter, int set) {
                counterProgressBar.setMax(counter);
                counterProgressBar.setText(String.format("0 / %d", counter));

                setProgressBar.setMax(set);
                setProgressBar.setText(String.format("0 / %d", set));
            }

            @Override
            public void getTimeData(int hour, int minute, int second) {
                timePorgressBar.setProgress(second);

                valueHours = hour;
                valueMinutes = minute;
                valueSeconds = second;
            }
        });
        //put GraphView
        views.put("graph", graph);
        views.put("gestureText", gestureText);
        views.put("healthTextView", healthTextView);
        views.put("timeTextView", timeTextView);
        views.put("counterProgressBar", counterProgressBar);
        views.put("setProgressBar", setProgressBar);
        views.put("timeProgressBar", timePorgressBar);



        return view;
    }

    private View.OnClickListener startButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (startButton.getText().equals("시작")) {
                startButton.setText("중지");
                setupButton.setClickable(false);
                mainFragmentListener.startButtonClick(views);

            } else {
                startButton.setText("시작");
                setupButton.setClickable(true);
            }
        }
    };

    private View.OnClickListener setupButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setupDialog.showDialog();
        }
    };
}
