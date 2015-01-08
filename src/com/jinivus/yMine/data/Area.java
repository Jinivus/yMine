package com.jinivus.yMine.data;

/**
 * Created by Matt on 1/8/2015.
 */
import org.powerbot.script.Tile;

public enum Area {

    YANILLE(new org.powerbot.script.Area(
            new Tile(2625, 3138, 0),
            new Tile(2629, 3143, 0)

    ), new org.powerbot.script.Area(
            new Tile(2608, 3087, 0),
            new Tile(2614, 3098, 0)
    ));

    private final org.powerbot.script.Area rockArea;
    private final org.powerbot.script.Area bankArea;

    private Area(org.powerbot.script.Area rockArea, org.powerbot.script.Area bankArea){
        this.rockArea = rockArea;
        this.bankArea = bankArea;
    }

    public org.powerbot.script.Area getRockArea() {
        return rockArea;
    }

    public org.powerbot.script.Area getBankArea() {
        return  bankArea;
    }

}
