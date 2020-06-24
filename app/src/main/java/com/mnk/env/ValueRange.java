package com.mnk.env;

public class ValueRange {
    float r1,r2;
    ValueRange(float r1, float r2)
    {
        this.r1=r1;
        this.r2=r2;
    }

    public boolean isValidValue(long a) {
        if(r1<=a && a<=r2 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
