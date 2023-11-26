package ru.koshkin.enums;

import java.awt.*;

public enum LIGHTS {
    RED,YELLOW,GREEN;

    public static Color resolveToColor(LIGHTS l){
        switch (l) {
            case RED:return Color.RED;
            case YELLOW:return Color.YELLOW;
            case GREEN:return Color.GREEN;
        }
        return Color.RED;
    }
}
