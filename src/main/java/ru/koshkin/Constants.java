package ru.koshkin;

public abstract class Constants {
    public static int Pmin = 5;
    public static float Tmin = 10;
    public static float simSpeed = 1;

    public static float Trepeat = 10;
    public static float Tgreen = 10;

    public static float TsimMessageSend = 0.01f;
    public static int TsimElementArrivalMin = 2;
    public static int TsimElementArrivalMax = 5;
    public static float TsimElemArrivalIntervalMin = 2;
    public static float TsimElemArrivalIntervalMax = 5;

    public static void initialize(int Pmin, float Tmin, float Trepeat, float simSpeed) {
        Constants.Pmin = Pmin;
        Constants.Tmin = Tmin;
        Constants.Trepeat = Trepeat;
        update(simSpeed);
    }

    public static void update(float simSpeed) {
        Constants.Tmin = Tmin / simSpeed;
        Constants.Trepeat = Trepeat / simSpeed;
        Constants.simSpeed = simSpeed;
        Constants.Trepeat /= simSpeed;
        Constants.Tgreen /= simSpeed;
        Constants.TsimMessageSend /= simSpeed;
        Constants.TsimElemArrivalIntervalMin /= simSpeed;
        Constants.TsimElemArrivalIntervalMax /= simSpeed;
    }
}
