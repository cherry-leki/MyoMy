package example.naoki.ble_myo.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import example.naoki.ble_myo.DataProcess.ExerciseSelectAdapter;
import example.naoki.ble_myo.DataProcess.ExerciseSelectItem;
import example.naoki.ble_myo.R;

/**
 * Created by Sabarada on 2016-08-10.
 */
public class MenuActivity extends ActionBarActivity {

    public static int screenWidth;
    public static int screenHeight;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;

    private ListView todayExerciseList;
    private ExerciseSelectAdapter exerciseSelectAdapter;

    private TextView yearTextView;
    private TextView monthDayTextView;
    private TextView headerName;

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        String headername = intent.getStringExtra("name");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        toolbar.setTitle("OMyHealth");
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        exerciseSelectAdapter = new ExerciseSelectAdapter();
        navigationView =(NavigationView)findViewById(R.id.drawer);
        todayExerciseList = (ListView) findViewById(R.id.todayExerciseList);
        todayExerciseList.setAdapter(exerciseSelectAdapter);
        yearTextView = (TextView)findViewById(R.id.menuYear);
        monthDayTextView = (TextView)findViewById(R.id.menuMonthDay);

        exerciseSelectAdapter.addItem("팔굽혀펴기", "팔운동");
        exerciseSelectAdapter.addItem("아령운동", "팔운동");
        exerciseSelectAdapter.addItem("사이클", "뜬금다리운동");
        exerciseSelectAdapter.addItem("팔굽혀펴기", "팔운동");
        exerciseSelectAdapter.addItem("아령운동", "팔운동");
        exerciseSelectAdapter.addItem("사이클", "뜬금다리운동");

        View headerView = navigationView.getHeaderView(0);
        headerName = (TextView)headerView.findViewById(R.id.headerName);
        headerName.setText(headername + "님 환영합니다.");

        Date currentTime = new Date();

        yearTextView.setText(new SimpleDateFormat("yyyy").format(currentTime));
        monthDayTextView.setText(new SimpleDateFormat("MM/dd").format(currentTime));

        todayExerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ExerciseSelectItem item = (ExerciseSelectItem) parent.getItemAtPosition(position);

                String titleStr = item.getTitle();
                String descStr = item.getDesc();
            }
        });

        isIntro();
    }

    public void exerciseButtonClick(View v)
    {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void AnalysisButtonClick(View vIew)
    {
        Intent intent = new Intent(MenuActivity.this, AnalysisActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        int id = item.getItemId();
        if(id == R.id.poweroff) {
            new AlertDialog.Builder(this)
                    .setTitle("종료")
                    .setMessage("종료하시겠습니까?")
                    .setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            finish();
                        }
                    })
                    .setPositiveButton("아니오", null).show();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void isIntro() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            } else {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                screenWidth = metrics.widthPixels;
                screenHeight = metrics.heightPixels;
            }
        }
    }
}
