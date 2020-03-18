package com.mesh.Database;

public enum BooleanEnum {
    TRUE("True", 1),
    FALSE("False", 0);

    private String stringValue;
    private int intValue;
    private BooleanEnum(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public static boolean getBoolean(int i)
    {
        if (i == 0)
            return false;
        else
            return true;
    }

    public static int getIntValueOfBoolean(boolean b)
    {
        if (b)
            return 1;
        else
            return 0;
    }
}
