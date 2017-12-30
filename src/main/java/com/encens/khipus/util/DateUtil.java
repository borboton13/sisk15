package com.encens.khipus.util;

/**
 * Created by Diego on 07/05/2015.
 */
public class DateUtil {
    public DateUtil() {
    }

    public  String getMes(int month)
    {
        String result = "";
        if(0 == month)
            result = "enero";
        if(1 == month)
            result = "febrero";
        if(2 == month)
            result = "marzo";
        if(3 == month)
            result = "abril";
        if(4 == month)
            result = "mayo";
        if(5 == month)
            result = "junio";
        if(6 == month)
            result = "julio";
        if(7 == month)
            result = "agosto";
        if(8 == month)
            result = "septiembre";
        if(9 == month)
            result = "octubre";
        if(10 == month)
            result = "noviembre";
        if(11 == month)
            result = "diciembre";

        return result;
    }
}
