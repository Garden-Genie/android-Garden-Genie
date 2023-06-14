package com.example.gardengenie_android;

public class PlantName {
    private static String plantName;

    public static void setPlantName(String name) {
        plantName = name;
    }

    public static String getPlantName() {
        return plantName;
    }
}