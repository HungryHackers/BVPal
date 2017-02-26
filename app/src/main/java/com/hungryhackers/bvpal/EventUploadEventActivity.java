package com.hungryhackers.bvpal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventListener;
import java.util.Locale;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Images.Media.getBitmap;
import static android.text.TextUtils.isEmpty;
import static android.text.TextUtils.lastIndexOf;

public class EventUploadEventActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 1;
    private static final int RESULT_CROP_IMG = 2;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    int year, month, day, hour, minute;
    int screenWidth, screenHeight;

    boolean flag = true;
    Long size;

    TextView eventNameTextView, venueTextView, descriptionTextView, dateTextView, startTimeTextView, endTimeTextView;
    TextView posterNameTextView;
    ImageView posterImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_event_upload_event);

        eventNameTextView = (TextView) findViewById(R.id.registerEventName);
        venueTextView = (TextView) findViewById(R.id.registerVenue);
        descriptionTextView = (TextView) findViewById(R.id.registerDesc);
        dateTextView = (TextView) findViewById(R.id.registerDate);
        startTimeTextView = (TextView) findViewById(R.id.registerStartTime);
        endTimeTextView = (TextView) findViewById(R.id.registerEndTime);

        posterNameTextView = (TextView) findViewById(R.id.registerPosterName);

        posterImage = (ImageView) findViewById(R.id.posterImageView);

        Calendar newCalendar = Calendar.getInstance();
        year = newCalendar.get(Calendar.YEAR);
        month = newCalendar.get(Calendar.MONTH);
        day = newCalendar.get(Calendar.DAY_OF_MONTH);
        hour = newCalendar.get(Calendar.HOUR_OF_DAY);
        minute = newCalendar.get(Calendar.MINUTE);
    }

    public void cancel(View v){

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Confirm");
        b.setMessage("Are you sure you want exit?");
        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(new Intent(EventUploadEventActivity.this, LoginActivity.class));
            }
        });
        b.create();
        b.show();
    }

    public void submit(View v){

        final String name,date,venue,time,desc;

        name = eventNameTextView.getText().toString().trim();
        date = dateTextView.getText().toString().trim();
        venue = venueTextView.getText().toString().trim();
        time = startTimeTextView.getText().toString().trim() + " - " + endTimeTextView.getText().toString().trim();
        desc = descriptionTextView.getText().toString().trim();

        if (isEmpty(name)){
            Toast.makeText(this, "Please enter the event name", Toast.LENGTH_SHORT).show();
            return;
        }else if (isEmpty(date)){
            Toast.makeText(this, "Please select the event date", Toast.LENGTH_SHORT).show();
            return;
        }else if (isEmpty(startTimeTextView.getText().toString().trim())){
            Toast.makeText(this, "Please select the start time of the event", Toast.LENGTH_SHORT).show();
            return;
        }else if (isEmpty(endTimeTextView.getText().toString().trim())){
            Toast.makeText(this, "Please select the end time of the event", Toast.LENGTH_SHORT).show();
            return;
        }else if (isEmpty(desc)){
            Toast.makeText(this, "Please enter the description for the event", Toast.LENGTH_SHORT).show();
            return;
        }

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference eventRef = databaseReference.child("events");
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flag) {
                    size = dataSnapshot.getChildrenCount();
                    flag = false;
                }
                DatabaseReference newEventRef = eventRef.child(Long.toString(size));
                newEventRef.child("ALERT").setValue("0");
                newEventRef.child("name").setValue(name);
                newEventRef.child("date").setValue(date);
                newEventRef.child("venue").setValue(venue);
                newEventRef.child("time").setValue(time);
                newEventRef.child("description").setValue(desc);
                newEventRef.child("imglink").setValue("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Do you want exit or continue editing");
        b.setTitle("Event Registered");
        b.setCancelable(false);
        b.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        b.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel(null);
            }
        });
        b.create().show();

    }

    public void selectDate (View v){

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                EventUploadEventActivity.this.year = year;
                month = monthOfYear;
                day = dayOfMonth;
                dateTextView.setText(dateFormatter.format(newDate.getTime()));
            }

        }, year, month, day);

        datePickerDialog.show();
    }

    public void selectTime(View v){
        LinearLayout linearLayout = (LinearLayout) v;
        final TextView t = (TextView) linearLayout.getChildAt(1);

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String am_pm = " AM";
                int currentHour = hourOfDay;
                if (currentHour > 12){
                    currentHour = currentHour - 12;
                    am_pm = " PM";
                }
                hour = hourOfDay;
                EventUploadEventActivity.this.minute = minute;
                String time = Integer.toString(currentHour) + ":" + Integer.toString(minute) + am_pm;
                t.setText(time);
            }
        },hour, minute,false);
        timePickerDialog.show();

    }

    public void selectPoster (View v){

        screenWidth = v.getWidth();
        screenHeight = screenWidth*9/16;

        Intent i = new Intent();
        i.setAction(Intent.ACTION_PICK);
        i.setData(EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

                if (requestCode == RESULT_LOAD_IMG && data != null) {
                    Uri selectedImg = data.getData();
                    performCrop(selectedImg);
                }

                if (requestCode == RESULT_CROP_IMG && data != null){
                    //Uri selectedImg = data.getData();
                    Bundle extras = data.getExtras();
                    //Bitmap img = getBitmap(this.getContentResolver(), selectedImg);
                    Bitmap img = extras.getParcelable("data");
                    posterImage.setImageBitmap(img);
                }


        }
    }

    private void performCrop(Uri imgUri){
        Intent i = new Intent();
        i.setAction("com.android.camera.action.CROP");
        i.setDataAndType(imgUri, "image/*");

        i.putExtra("crop", true);

        i.putExtra("aspectX", 16);
        i.putExtra("aspectY", 9);

        i.putExtra("outputX", screenWidth);
        i.putExtra("outputY", screenHeight);

        i.putExtra("return-data", true);

        startActivityForResult(i, RESULT_CROP_IMG);
    }
}
