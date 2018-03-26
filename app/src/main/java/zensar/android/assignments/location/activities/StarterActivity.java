package zensar.android.assignments.location.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import zensar.android.assignments.location.utility.Utils;
import zensar.android.assignments.R;

public class StarterActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private CardView mCvGooglePlaces;
    private CardView mCvCustomPlaces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        initViews();
        setListeners();
    }


    private void initViews() {
        mCvGooglePlaces=findViewById(R.id.cvGooglePlaces);
        mCvCustomPlaces=findViewById(R.id.cvCustomPlaces);
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void setListeners() {
        mCvGooglePlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Utils.startAcivity(StarterActivity.this,GoogleMapsAndPlacesActivity.class);
            }
        });

        mCvCustomPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startAcivity(StarterActivity.this,CustomPlacesActivity.class);
            }
        });
    }
}
