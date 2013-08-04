package com.codeminders.ardrone.data.navdata;


public enum CtrlState
{
    DEFAULT, INIT, LANDED, FLYING, HOVERING, TEST, TRANS_TAKEOFF, TRANS_GOTOFIX, TRANS_LANDING;

    public static CtrlState fromInt(int v) throws NavDataFormatException
    {
        switch(v)
        {
        case 0:
            return DEFAULT;
        case 1:
            return INIT;
        case 2:
            return LANDED;
        case 3:
            return FLYING;
        case 4:
            return HOVERING;
        case 5:
            return TEST;
        case 6:
            return TRANS_TAKEOFF;
        case 7:
            return TRANS_GOTOFIX;
        case 8:
            return TRANS_LANDING;
        default:
            throw new NavDataFormatException("Invalid control state " + v);
        }
    }
}