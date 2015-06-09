package com.uow.tannoyahoy;

import java.sql.Time;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 * Created by Isa on 9/05/2015.
 */
public class Data {
    private Date dateSent_ = new Date();
    private String message_ = new String();
    private int theID;
    public Data(String date, String message, int ID)
    {
        DateFormat df1 = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        try{
            dateSent_ = df1.parse(date);
            message_ = message;
            theID = ID;

        }
        catch(Exception e)
        {
            //insert error code here
        }

    }
    public Date DateSent()
    {

        return dateSent_;
    }
    public String Message()
    {

        return message_;
    }
    public int getTheID() {
        return theID;
    }

    @Override
    public String toString()
    {
        return(DateFormat.getTimeInstance(DateFormat.SHORT).format(dateSent_)+",\n"+message_);
    }
}
