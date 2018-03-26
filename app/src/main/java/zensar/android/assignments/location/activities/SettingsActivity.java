package zensar.android.assignments.location.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import zensar.android.assignments.location.interactors.ISettingsView;
import zensar.android.assignments.location.utility.Constants;
import zensar.android.assignments.R;

public class SettingsActivity extends AppCompatActivity implements ISettingsView, SeekBar.OnSeekBarChangeListener, View.OnClickListener {


    Toolbar mToolBar;
    AppCompatTextView mTvRadius;
    AppCompatSeekBar mSbRadiusValue;
    AppCompatTextView mTvSetRadius;
    Button mBtnSetRadius;
    int radius = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
    }


    /**
     * Intialize views here
     */
    @Override
    public void initViews() {
        if (getIntent() != null) {
            radius = getIntent().getBundleExtra(Constants.DATA).getInt(Constants.RADIUS);
        }

        mTvRadius = findViewById(R.id.tvRadius);
        Spannable word = new SpannableString(getString(R.string.set_radius));
        word.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SettingsActivity.this,R.color.disco)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvRadius.setText(word);
        Spannable wordTwo = new SpannableString(getString(R.string.in_km));

        wordTwo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SettingsActivity.this,R.color.disco_sixtyper_opac)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvRadius.append(wordTwo);
        mTvSetRadius=findViewById(R.id.tvSetRadius);
        mTvSetRadius.setText(radius+"");
        mSbRadiusValue = findViewById(R.id.sbRadiusValue);
        mSbRadiusValue.setProgress(radius);
        mSbRadiusValue.setOnSeekBarChangeListener(this);
        mBtnSetRadius = findViewById(R.id.btnSetRadius);
        mBtnSetRadius.setOnClickListener(this);
        mToolBar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    /**
     * Set listeners here
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSetRadius) {
            Intent intent = new Intent();
            intent.putExtra(Constants.RADIUS, radius);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        radius = progress;
        mTvSetRadius.setText(progress+"");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


}
