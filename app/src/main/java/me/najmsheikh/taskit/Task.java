package me.najmsheikh.taskit;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {

    private String mName;
    private boolean mDone;
    private Date DueDate;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isDone() {
        return mDone;
    }

    public void setDone(boolean done) {
        mDone = done;
    }

    public Date getDueDate() {
        return DueDate;
    }

    public void setDueDate(Date dueDate) {
        DueDate = dueDate;
    }

    public String toString() {
        return mName;
    }
}
