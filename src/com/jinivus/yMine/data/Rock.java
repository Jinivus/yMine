package com.jinivus.yMine.data;

/**
 * Created by Matt on 1/8/2015.
 */
public enum Rock {
    IRON(new int[]{11956,11955,11954,37307,37309,37308},440);
    private final int[] OBJECT_ID;
    private final int ore_ID;

    private Rock( int[] object_id, int ore_ID) {

        this.OBJECT_ID = object_id;
        this.ore_ID = ore_ID;

    }


    public int[] getObject_ID() {
        return OBJECT_ID;
    }
}
