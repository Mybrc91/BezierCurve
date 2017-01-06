package com.wbq.beziercurve;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
private BourceView bourceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bourceView= (BourceView) findViewById(R.id.bourceView);

    }

    public void start(View v){
        bourceView.startTotalAnimator();
    }

}
