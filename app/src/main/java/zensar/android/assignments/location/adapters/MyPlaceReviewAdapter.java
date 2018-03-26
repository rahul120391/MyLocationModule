package zensar.android.assignments.location.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import zensar.android.assignments.R;
import zensar.android.assignments.location.model.Review;
import zensar.android.assignments.location.utility.Utils;

/**
 * Created by RK51670 on 20-02-2018.
 */

public class MyPlaceReviewAdapter extends RecyclerView.Adapter<MyPlaceReviewAdapter.MyViewHolder> {

    List<Review> list=new ArrayList<>();
    private Context context;
    public MyPlaceReviewAdapter(Context context){
        this.context=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.place_detail_row_item,null,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Utils.loadImages(context,holder.mImgAuthor,list.get(position).getProfilePhotoUrl());
        holder.mTvAuthorName.setText(list.get(position).getAuthorName());
        holder.mTvReview.setText(list.get(position).getText());
        holder.mRtBar.setRating(list.get(position).getRating());
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView mImgAuthor;
        AppCompatTextView mTvAuthorName;
        AppCompatRatingBar mRtBar;
        AppCompatTextView mTvReview;
        public MyViewHolder(View itemView) {
            super(itemView);
            mImgAuthor=itemView.findViewById(R.id.imgAuthor);
            mTvAuthorName=itemView.findViewById(R.id.txtAuthorName);
            mRtBar=itemView.findViewById(R.id.rtPlace);
            mTvReview=itemView.findViewById(R.id.txtReview);
        }
    }

    public void notifyData(List<Review> list){
        this.list=list;
        notifyDataSetChanged();
    }
}
