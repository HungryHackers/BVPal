package com.hungryhackers.bvpal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static com.hungryhackers.bvpal.sharedPreferencesConstants.BVPAL;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.FIRSTLOGIN;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.LIKES;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.ROLLNUMBER;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.USERBRANCH;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.USERNAME;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.USERSEM;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ImageView eventsImage, profileImage, timeTableImage, avatarImage;
    private LinearLayout eventSelect, profileSelect, timetableSelect;
    private ListView eventListView;
    private Animation slideOutLeft, slideOutRight, slideInLeft, slideInRight;
    private RelativeLayout profileLayout, avatarLayout, spinnerLayout, eventLayout, timeTableLayout;
    private TextView name, semester, branch, textViewRollnumber;

    private static final String NAME = "name";
    private static final String SEMESTER = "semester";
    private static final String BRANCH = "branch";

    private ArrayList<Map<String, String>> eventsArrayList;
    private ArrayList<String> likedEvents;

    private EventAdapter adapter;
    private boolean flag = true;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tabLayout = (TabLayout) findViewById(R.id.navigationBarTabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.event_icon), false);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.user_icon), true);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.table_icon), false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabIconColor = ContextCompat.getColor(ProfileActivity.this, R.color.blueAccent);
                tab.getIcon().setColorFilter(selectedTabIconColor, PorterDuff.Mode.SRC_IN);
                viewChange(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int unselectedTabIconColor = ContextCompat.getColor(ProfileActivity.this, R.color.lightGrey);
                tab.getIcon().setColorFilter(unselectedTabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        eventsArrayList = new ArrayList<>();
        likedEvents = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        eventListView = (ListView) findViewById(R.id.eventcards_list);
        adapter = new EventAdapter(this, eventsArrayList, likedEvents);
        eventListView.setAdapter(adapter);
        eventListView.setDivider(null);

        slideOutLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideoutleft);
        slideOutRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideoutright);
        slideInLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideinleft);
        slideInRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideinright);

        profileLayout = (RelativeLayout) findViewById(R.id.profile_layout);
        avatarLayout = (RelativeLayout) findViewById(R.id.avatarLayout);
        eventLayout = (RelativeLayout) findViewById(R.id.events_layout);
        spinnerLayout = (RelativeLayout) findViewById(R.id.spinnerLayout);
        timeTableLayout = (RelativeLayout) findViewById(R.id.timetable_layout);

        avatarImage = (ImageView) findViewById(R.id.avatar);

        name = (TextView) findViewById(R.id.name);
        semester = (TextView) findViewById(R.id.semester);
        branch = (TextView) findViewById(R.id.branch);
        textViewRollnumber = (TextView) findViewById(R.id.rollNumber);


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Profile...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        //so that layout does not show until data is fetched
        profileLayout.setVisibility(View.INVISIBLE);
        String rollnumber = "";

        sp = getSharedPreferences(BVPAL, MODE_PRIVATE);
        editor = sp.edit();
        rollnumber = sp.getString(ROLLNUMBER, "99999999999");

        textViewRollnumber.setText(rollnumber);

        DatabaseReference mDatabaseRef = databaseReference.child("database").child(rollnumber);

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                name.setText(map.get(NAME));
                semester.setText("Semester " + String.valueOf(map.get(SEMESTER)));
                branch.setText(map.get(BRANCH));
                progressDialog.dismiss();
                //Save details to sharedPreferences
                editor.putString(USERNAME, map.get(NAME));
                editor.putString(USERSEM, "Semester " + String.valueOf(map.get(SEMESTER)));
                editor.putString(USERBRANCH, map.get(BRANCH));
                editor.apply();
                //layout visible once data fetched
//                if(menuSelection == 0) {
                    profileLayout.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //get likes on first login and store in sharedPreferences.
        Boolean firstLogin = sp.getBoolean(FIRSTLOGIN, true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference likedEventRef = databaseReference.child("wishlist_database").child(user.getUid());

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String name = data.getKey();
                    likedEvents.add(name);
                    likedEventRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if (firstLogin){
            mDatabaseRef.addValueEventListener(dataListener);
            likedEventRef.addValueEventListener(listener);

            editor.putBoolean(FIRSTLOGIN, false);
            editor.commit();
        }else {
            String nameString = sp.getString(USERNAME,null);
            String branchString = sp.getString(USERBRANCH,null);
            String semString = sp.getString(USERSEM,null);

            name.setText(nameString);
            semester.setText(semString);
            branch.setText(branchString);
            progressDialog.dismiss();

//            if(tabLayout.getSelectedTabPosition() == 1) {
                profileLayout.setVisibility(View.VISIBLE);
//            }
        }
    }

    private void viewChange(int position) {

        switch (position){
            case 0:
                spinnerLayout.setVisibility(View.GONE);
                if (flag) {
                    spinnerLayout.setVisibility(View.VISIBLE);
                }
                eventLayout.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                timeTableLayout.setVisibility(View.GONE);
                inflateEvents();
                break;
            case 1:
                profileLayout.setVisibility(View.VISIBLE);
                timeTableLayout.setVisibility(View.GONE);
                eventLayout.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                break;
            case 2:
                spinnerLayout.setVisibility(View.VISIBLE);
                timeTableLayout.setVisibility(View.VISIBLE);
                eventLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                break;
        }

    }


    private void inflateEvents() {
        if (flag) {
            DatabaseReference mEventRef = databaseReference.child("events");
            mEventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String likesString = sp.getString(LIKES,"");
                    String[] likeName = likesString.split(":");
                    likedEvents.addAll(Arrays.asList(likeName));

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Map<String, String> map = (Map) child.getValue();

                        if (!eventsArrayList.contains(map)) {
                            eventsArrayList.add(map);
                            adapter.notifyDataSetChanged();
                            spinnerLayout.setVisibility(View.GONE);
                        }

                        if (map.get("ALERT").equals("1")){
                            //Show notification
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            flag = false;
        }
    }

    public void logout(View view){
        firebaseAuth.signOut();
        sp.edit().clear().commit();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void boy(View view){
        avatarImage.setImageResource(R.drawable.maleavatar);
        View v = findViewById(R.id.girl);
        v.setAlpha((float)00.5);
        view.setAlpha(1);
        // previously visible view
        final View myView = findViewById(R.id.avatarLayout);

        // get the center for the clipping circle
        int cx = 260;
        int cy = 260;

        // get the initial radius for the clipping circle
        float initialRadius = myView.getHeight();

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        anim.start();
    }
    public void girl(View view){
        avatarImage.setImageResource(R.drawable.femaleavatar);
        View v = findViewById(R.id.boy);
        v.setAlpha((float)00.5);
        view.setAlpha(1);
        // previously visible view
        final View myView = findViewById(R.id.avatarLayout);

        // get the center for the clipping circle
        int cx = 260;
        int cy = 260;

        // get the initial radius for the clipping circle
        float initialRadius = myView.getHeight();

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        anim.start();
    }
    public void avatarChange(View view){
       // avatarLayout.setVisibility(View.VISIBLE);
        // previously invisible view
        View myView = findViewById(R.id.avatarLayout);

        // get the center for the clipping circle
        int cx = 260;
        int cy = 260;

        // get the final radius for the clipping circle
        float finalRadius = myView.getHeight();

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }
}
