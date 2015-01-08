package com.jinivus.yMine.tools;

import com.jinivus.yMine.data.Area;
import com.jinivus.yMine.data.Rock;


public class Data {


    private final Rock rock;
    private final Area area;

    public Data()
    {
        this.rock = Rock.IRON;
        this.area = Area.YANILLE;
    }

    public int[] getRock() {
        return rock.getObject_ID();
    }

    public org.powerbot.script.Area getRockArea() {
        return  area.getRockArea();
    }
    public org.powerbot.script.Area getBankArea() {
        return area.getBankArea();
    }

}
