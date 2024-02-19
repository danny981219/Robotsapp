package bg.tu.common;

public class Constants {

    public static final float temperatureMin[][] =
            { {-20, -20, -20, -20, -20, -20, -20, -18, -15, -13, -10, -10, -10, -10, -13, -15, -18, -20, -20, -20, -20, -20, -20, -20}, // january
              {-20, -20, -20, -20, -20, -20, -20, -18, -15, -13, -10, -10, -10, -10, -13, -15, -18, -20, -20, -20, -20, -20, -20, -20}, // februari
              {-5, -5, -5, -5, -8, -10, -10, -8, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, -5}, // march
              {0, 0, 0, 0, 0, 0, 0, 0, 3, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3, 0, 0}, // april
              {5, 5, 5, 5, 5, 5, 5, 5, 5, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 8, 5, 5, 5, 5}, // may
              {10, 10, 10, 10, 10, 10, 10, 13, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 13, 10, 10, 10, 10}, // june
              {15, 15, 15, 15, 15, 15, 15, 18, 21, 22, 23, 24, 25, 25, 25, 25, 25, 24, 22, 20, 18, 17, 16, 16}, // july
              {15, 15, 15, 15, 15, 15, 15, 18, 21, 22, 23, 24, 25, 25, 25, 25, 25, 24, 22, 20, 18, 17, 16, 16}, // august
              {10, 10, 10, 10, 10, 10, 10, 10, 13, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 13, 10, 10, 10, 10}, // september
              {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // october
              {-10, -10, -10, -10, -10, -10, -10, -10, -10, -8, -5, -5, -5, -5, -5, -5, -5, -5, -5, -5, -8, -10, -10, -10}, // november
              {-15, -15, -15, -15, -15, -15, -15, -13, -10, -10, -10, -10, -10, -10, -10, -13, -13, -15, -15, -15, -15, -15, -15, -15}, // december
            };

    public static final float temperatureMax[][] =
            { {0, 0, 0, 0, 0, 0, 0, 3, 5, 8, 10, 10, 10, 10, 10, 10, 8, 5, 3, 0, 0, 0, 0, 0},
              {0, 0, 0, 0, 0, 0, 0, 3, 5, 8, 10, 10, 10, 10, 10, 10, 8, 5, 3, 0, 0, 0, 0, 0},
              {10, 10, 10, 10, 10, 10, 10, 10, 13, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 13, 10, 10, 10, 10},
              {15, 15, 15, 15, 15, 15, 15, 18, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 18, 15, 15, 15, 15, 15},
              {15, 15, 15, 15, 15, 15, 18, 20, 23, 25, 25, 25, 25, 25, 25, 25, 25, 23, 20, 20, 18, 15, 15, 15},
              {20, 20, 20, 20, 20, 20, 20, 13, 25, 18, 30, 30, 30, 30, 30, 28, 25, 25, 25, 13, 20, 20, 20, 20},
              {20, 20, 20, 20, 20, 20, 23, 25, 28, 33, 38, 40, 40, 40, 40, 38, 35, 33, 30, 28, 25, 23, 20, 20},
              {20, 20, 20, 20, 20, 20, 23, 25, 28, 33, 38, 40, 40, 40, 40, 38, 35, 33, 30, 28, 25, 23, 20, 20},
              {15, 15, 15, 15, 15, 15, 18, 20, 25, 28, 30, 30, 30, 30, 30, 28, 25, 23, 20, 18, 15, 15, 15, 15},
              {10, 10, 10, 10, 10, 10, 10, 13, 15, 20, 20, 20, 20, 20, 20, 20, 18, 15, 15, 13, 10, 10, 10, 10},
              {5, 5, 5, 5, 5, 5, 5, 8, 10, 13, 15, 15, 15, 15, 15, 13, 10, 10, 10, 8, 5, 5, 5, 5},
              {0, 0, 0, 0, 0, 0, 5, 8, 10, 10, 10, 10, 10, 10, 10, 10, 8, 5, 0, 0, 0, 0, 0, 0},
            };

    public static final float humidityMin[][] =
            { {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                    30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
                    {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                            30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
                    {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                            30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
                    {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                            30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
                    {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                            30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
                    {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
                            20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20},
                    {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
                            10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                    {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
                            10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                    {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
                            20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20},
                    {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
                            20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20},
                    {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                            30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
                    {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                            30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
            };

    public static final float humidityMax[][] =
            { {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
                    100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                    {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
                            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                    {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
                            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                    {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
                            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
                    {80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80,
                            80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80},
                    {60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60,
                            60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60},
                    {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
                            20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20},
                    {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
                            20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20},
                    {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                            30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
                    {50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50,
                            50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50},
                    {60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60,
                            60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60},
                    {80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80,
                            80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80},
            };

    public static final int pumpMaxTimeAuto[] = { 0, 0, 0, 20, 30, 40, 60, 60, 30, 20, 0, 0 };

    public static final int pumpMaxTimeManual[] = { 0, 0, 20, 40, 40, 40, 40, 40, 40, 20, 10, 0};

}