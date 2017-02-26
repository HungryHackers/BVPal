package com.hungryhackers.bvpal;

/**
 * Created by YourFather on 16-02-2017.
 */

public class EventData {
    String mEventName;
    String mDesc;
    String mImgLink;
    String mEventDate;
    String mVenue;
    String mTime;

    public EventData(String mEventName, String mDesc, String mImgLink, String mEventDate, String mVenue, String mTime) {
        this.mEventName = mEventName;
        this.mDesc = mDesc;
        this.mImgLink = mImgLink;
        this.mEventDate = mEventDate;
        this.mVenue = mVenue;
        this.mTime = mTime;
    }
}
