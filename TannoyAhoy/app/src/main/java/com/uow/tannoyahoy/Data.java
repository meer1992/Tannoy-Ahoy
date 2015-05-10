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
    public Data(String date, String message)
    {
        DateFormat df1 = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        try{
            dateSent_ = df1.parse(date);
            message_ = message;

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
}
