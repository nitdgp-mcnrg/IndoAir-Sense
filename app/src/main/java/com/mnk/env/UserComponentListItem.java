package com.mnk.env;

public class UserComponentListItem {
    private int imageViewID;
    private String heading;

    public UserComponentListItem(int imageViewID, String heading) {
        this.imageViewID = imageViewID;
        this.heading = heading;
    }

    public int getImageView() {
        return imageViewID;
    }

    public String getHeading() {
        return heading;
    }
}
