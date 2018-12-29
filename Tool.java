package com.michaelportillo.android.toolcheckout;

import java.util.Date;
import java.util.UUID;

/**
 * Created by USER on 12/4/18.
 */

public class Tool {

    private UUID mId;
    private String mToolName;
    private Date mDate;
    private Date mReturnDate;
    private boolean mReturned;
    private String mContact;

    public String getToolName() {
        return mToolName;
    }

    public void setToolName(String toolName) {
        mToolName = toolName;
    }
    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getReturnDate(){return mReturnDate;}

    public void setReturnDate(Date date){mReturnDate = date;}

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public boolean isReturned() {
        return mReturned;
    }

    public void setReturned(boolean returned) {
        mReturned = returned;
    }

    public void setContact(String contact){ mContact = contact;}

    public String getContact(){return mContact;}

    public String getPhotoFileName(){
        return "IMG_" + getId().toString() + ".jpg";
    }

    public Tool(){this(UUID.randomUUID());}

    /**
     * d.13
     * @param id
     */
    public Tool(UUID id){
        mId = id;
        mDate = new Date();
        mReturnDate = new Date();
    }


}
