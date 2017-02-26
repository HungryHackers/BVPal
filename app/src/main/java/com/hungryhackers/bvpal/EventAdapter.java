package com.hungryhackers.bvpal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import static com.hungryhackers.bvpal.sharedPreferencesConstants.BVPAL;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.LIKES;

/**
 * Created by YourFather on 17-02-2017.
 */

public class EventAdapter extends ArrayAdapter<Map<String, String>> {
    Context mContext;
    ArrayList<Map<String, String>> mObjects;
    ArrayList<String> mLlikedEvents;
    Animation mBounceAnim;
    MyBounceInterpolator mBounceInterpolator;

    public EventAdapter(Context context, ArrayList<Map<String, String>> objects, ArrayList<String> likedObjects) {
        super(context, 0, objects);
        mContext = context;
        mObjects = objects;
        mLlikedEvents = likedObjects;
        mBounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        mBounceInterpolator = new MyBounceInterpolator(0.2, 20);
    }

    static class EventViewHolder{
        TextView nameTextView;
        TextView dateTextView;
        TextView venueTextView;
        TextView descTextView;
        TextView timeTextView;
        ImageView likeImageView;

        public EventViewHolder(TextView nameTextView, TextView dateTextView, TextView venueTextView, TextView descTextView, TextView timeTextView, ImageView likeImageView) {
            this.nameTextView = nameTextView;
            this.dateTextView = dateTextView;
            this.venueTextView = venueTextView;
            this.descTextView = descTextView;
            this.timeTextView = timeTextView;
            this.likeImageView = likeImageView;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.card_layout, null);
            
            TextView nameTextView = (TextView) convertView.findViewById(R.id.eventName);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.eventDate);
            TextView venueTextView = (TextView) convertView.findViewById(R.id.eventVenue);
            TextView descTextView = (TextView) convertView.findViewById(R.id.eventDesc);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.eventTime);
            ImageView like = (ImageView) convertView.findViewById(R.id.likeHeart_cardView);
            
            EventViewHolder eventViewHolder = new EventViewHolder(nameTextView, dateTextView, venueTextView, descTextView, timeTextView, like);
            convertView.setTag(eventViewHolder);
        }

        final EventViewHolder eventViewHolder = (EventViewHolder) convertView.getTag();

        final Map<String, String> data = mObjects.get(position);
        final String name = data.get("name");


        eventViewHolder.nameTextView.setText(data.get("name"));
        eventViewHolder.dateTextView.setText(data.get("date"));
        eventViewHolder.descTextView.setText(data.get("description"));
        eventViewHolder.venueTextView.setText(data.get("venue"));
        eventViewHolder.timeTextView.setText(data.get("time"));

        if (mLlikedEvents.contains(name)){
                        eventViewHolder.likeImageView.setImageResource(R.drawable.heart_selected);
        }else {
            eventViewHolder.likeImageView.setImageResource(R.drawable.heart_unselected);
        }

        eventViewHolder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("wishlist_database").child(user.getUid());
                if (mLlikedEvents.contains(name)){
                    eventViewHolder.likeImageView.setImageResource(R.drawable.heart_unselected);
                    mLlikedEvents.remove(name);
                    db.child(name).removeValue();

                }else {
                    setReminder(eventViewHolder.likeImageView,name);
                }

                SharedPreferences sp = mContext.getSharedPreferences(BVPAL, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                String likesString = "";
                for (String string : mLlikedEvents) {
                    likesString = likesString + ":" + string;
                }
                editor.putString(LIKES, likesString);
                editor.commit();
            }
        });

        return convertView;
    }

    private void setReminder(ImageView like, String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("wishlist_database").child(user.getUid());

        mBounceAnim.setInterpolator(mBounceInterpolator);
        like.setImageResource(R.drawable.heart_selected);
        like.startAnimation(mBounceAnim);

        mLlikedEvents.add(name);

        db.child(name).setValue("LIKED");

        //do set reminder stuff
    }


}
