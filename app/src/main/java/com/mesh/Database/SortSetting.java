package com.mesh.Database;

public enum SortSetting {
    Recency,
    Frequency,
    Unknown;

    public static SortSetting getSetting(int i)
    {
        switch(i)
        {
            case 0:
                return Recency;

            case 1:
                return Frequency;

            default:
                return Unknown;

        }
    }

    public static int getSettingID(SortSetting s)
    {
        switch (s)
        {
            case Recency:
                return 0;

            case Frequency:
                return 1;

            default:
                return -1;
        }
    }
}
