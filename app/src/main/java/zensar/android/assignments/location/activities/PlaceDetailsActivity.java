package zensar.android.assignments.location.activities;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import zensar.android.assignments.R;
import zensar.android.assignments.location.adapters.MyPlaceReviewAdapter;
import zensar.android.assignments.location.interactors.IPlaceDetails;
import zensar.android.assignments.location.interactors.ITransferApiResult;
import zensar.android.assignments.location.model.PlaceDetailResult;
import zensar.android.assignments.location.model.PlaceDetails;
import zensar.android.assignments.location.networking.ApiClass;
import zensar.android.assignments.location.utility.Constants;
import zensar.android.assignments.location.utility.Utils;

public class PlaceDetailsActivity extends AppCompatActivity implements IPlaceDetails, ITransferApiResult {

    AppCompatTextView mTvPlaceName;
    AppCompatTextView mTvPlaceAddress;
    AppCompatRatingBar mRtPlace;
    RecyclerView mRvReviews;
    FrameLayout mFlOverlay;
    AppCompatImageView mPlaceImage;
    Toolbar mToolbar;
    String placeId,type;
    ApiClass mApiClass;
    MyPlaceReviewAdapter adapter;
    CardView mCvReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        initViews();
        if(getIntent()!=null && getIntent().getBundleExtra(Constants.DATA)!=null){
            if(getIntent().getBundleExtra(Constants.DATA).getString(Constants.FROM).equalsIgnoreCase(Constants.GOOGLE_MAPS)){
                if (savedInstanceState == null && getIntent() != null) {
                    placeId = getIntent().getBundleExtra(Constants.DATA).getString(Constants.PLACE_ID);
                    type=getIntent().getBundleExtra(Constants.DATA).getString(Constants.TYPE);
                } else {
                    placeId = savedInstanceState.getString(Constants.PLACE_ID);
                    type=savedInstanceState.getString(Constants.TYPE);
                }
                fetchPlaceDetails();
            }
            else if(getIntent().getBundleExtra(Constants.DATA).getString(Constants.FROM).equalsIgnoreCase(Constants.CUSTOM_MAPS)){

            }

        }
        else{
            finish();
        }


    }
    @Override
    public void initViews() {
        mTvPlaceName = findViewById(R.id.txtPlaceName);
        mCvReviews=findViewById(R.id.cvReviews);
        mTvPlaceAddress = findViewById(R.id.txtPlaceAddress);
        mRtPlace = findViewById(R.id.rtPlace);
        mFlOverlay = findViewById(R.id.flOverlay);
        mPlaceImage = findViewById(R.id.imgPlace);
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
        mRvReviews = findViewById(R.id.rvReviews);
        if(getIntent().getBundleExtra(Constants.DATA).getString(Constants.FROM).equalsIgnoreCase(Constants.GOOGLE_MAPS)){
            mCvReviews.setVisibility(View.VISIBLE);
            mRvReviews.setLayoutManager(new LinearLayoutManager(this));
            DividerItemDecoration divider=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
            mRvReviews.addItemDecoration(divider);
            mRvReviews.setHasFixedSize(true);
            mRvReviews.setNestedScrollingEnabled(false);
            adapter = new MyPlaceReviewAdapter(this);
            mRvReviews.setAdapter(adapter);
        }
        else if(getIntent().getBundleExtra(Constants.DATA).getString(Constants.FROM).equalsIgnoreCase(Constants.CUSTOM_MAPS)){
             mCvReviews.setVisibility(View.GONE);
             mPlaceImage.setImageResource(R.drawable.bank);
             mTvPlaceName.setText(getIntent().getBundleExtra(Constants.DATA).getString(Constants.NAME));
             mTvPlaceAddress.setText(getIntent().getBundleExtra(Constants.DATA).getString(Constants.DESC));
             String rating=getIntent().getBundleExtra(Constants.DATA).getString(Constants.RATING);
             mRtPlace.setRating(Double.valueOf(rating).floatValue());
        }

    }

    @Override
    public void hideLoading() {
        mFlOverlay.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        mFlOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void fetchPlaceDetails() {
        if (Utils.checkConnectivity(PlaceDetailsActivity.this)) {
            mApiClass = new ApiClass(this);
            showLoading();
            mApiClass.getPlaceDetails(placeId,getString(R.string.google_api_key));
        } else {
            Utils.showToast(PlaceDetailsActivity.this, getString(R.string.network_error));
        }
    }

    @Override
    public void success(Object object) {
        hideLoading();
        if (object != null && ((PlaceDetails) object) != null && ((PlaceDetails) object).getResult() != null) {
            PlaceDetailResult result = ((PlaceDetails) object).getResult();
            Utils.loadImages(PlaceDetailsActivity.this,mPlaceImage,result.getIcon());
            mTvPlaceName.setText(result.getName());
            mTvPlaceAddress.setText(result.getVicinity());
            mRtPlace.setRating(result.getRating());
            if (result.getReviews() != null && result.getReviews().size() > 0) {
                adapter.notifyData(result.getReviews());
            }

        }
        else{
            Toast.makeText(this,getString(R.string.no_result_found),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void failure(String message) {
        hideLoading();
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(Constants.PLACE_ID, placeId);
        outState.putString(Constants.TYPE,type);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if(mApiClass!=null){
            mApiClass.cancelRequest();
        }
        super.onDestroy();
    }
}
