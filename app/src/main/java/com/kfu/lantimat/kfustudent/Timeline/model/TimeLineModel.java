package com.kfu.lantimat.kfustudent.Timeline.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineModel implements Parcelable {

    private String mMessage;
    private String mDate;
    String title;
    String place;
    String format;
    private OrderStatus mStatus;

    public TimeLineModel(String date, String title, String place, String format, OrderStatus mStatus) {
        this.mDate = date;
        this.title = title;
        this.place = place;
        this.format = format;
        this.mStatus = mStatus;
    }

    public TimeLineModel(String str, OrderStatus mStatus) {

        Pattern pattern = Pattern.compile("\"eventItem-date\">(.*)<\\/div>\n" +
                "                        <div class=\"eventItem-title\">(.*)<\\/div>\n" +
                "                        <div class=\"eventItem-place\">(.*)<\\/div>");

        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            mDate = matcher.group(1);
            mMessage = matcher.group(2) + "\n" + matcher.group(3);
        }
    }



    public String getTitle() {
        return title;
    }

    public String getPlace() {
        return place;
    }

    public String getFormat() {
        return format;
    }

    public String getTestText() {
        return title + "\n\n" + place + "\n" + format;
    }

    public TimeLineModel(String mMessage, String mDate, OrderStatus mStatus) {
        this.mMessage = mMessage;
        this.mDate = mDate;
        this.mStatus = mStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public void semMessage(String message) {
        this.mMessage = message;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public OrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(OrderStatus mStatus) {
        this.mStatus = mStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMessage);
        dest.writeString(this.mDate);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }

    protected TimeLineModel(Parcel in) {
        this.mMessage = in.readString();
        this.mDate = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];
    }

    public static final Creator<TimeLineModel> CREATOR = new Creator<TimeLineModel>() {
        @Override
        public TimeLineModel createFromParcel(Parcel source) {
            return new TimeLineModel(source);
        }

        @Override
        public TimeLineModel[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };
}
