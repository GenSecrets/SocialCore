package com.nicholasdoherty.socialcore;

/**
 * Created by john on 1/11/15.
 */
public class Test {
    public static void main(String[] args) {
        int chunkX = 318;
        int chunkZ = 407;
        int regionX = (int)Math.floor(chunkX / 32.0);
        int regionZ = (int)Math.floor(chunkZ / 32.0);
        System.out.println(regionX + "," + regionZ);
    }

}
