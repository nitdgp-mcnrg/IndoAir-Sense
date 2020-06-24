package com.mnk.env;

public class AQI{
    String[] state={"","SATISFACTORY,","MODERATERY POLLUTED,","POOR,","VERY POOR,","SEVERE,"};
    String aqiTest(float a, float r1, float r2, float r3, float r4, float r5, float r6, float r7, float r8, float r9, float r10, String name)
    {
        String gas="PM2.5 is ";
        gas=name+" is ";
        if(new ValueRange(r1,r2).isValidValue((long) a))
        {
            return state[0];
        }
        else if(new ValueRange(r3,r4).isValidValue((long) a))
        {
            return gas+state[1];
        }
        else if(new ValueRange(r5,r6).isValidValue((long) a))
        {
            return gas+state[2];
        }
        else if(new ValueRange(r7,r8).isValidValue((long) a))
        {
            return gas+state[3];
        }
        else if(new ValueRange(r9,r10).isValidValue((long) a))
        {
            return gas+state[4];
        }
        else
        {
            return gas+state[5];
        }
    }

}

