package com.example.ozercanh.screenrecordqa.Model;

/**
 * Created by ozercanh on 26/08/2015.
 */
public enum Place {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

    public static int toInt(Place place){
        switch(place){
            case TOP_LEFT:
                return 1;
            case TOP_RIGHT:
                return 2;
            case BOTTOM_LEFT:
                return 3;
            case BOTTOM_RIGHT:
                return 4;
        }
        return 1;
    }
}
