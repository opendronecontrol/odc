package com.codeminders.ardrone.data.navdata;

public enum FlyingState
{
    FLYING, TAKING_OFF, LANDING, LANDED;

    public static FlyingState fromControlState(CtrlState state)
    {
        switch(state)
        {
        case FLYING:
        case HOVERING:
        case TRANS_GOTOFIX:
            return FlyingState.FLYING;

        case TRANS_TAKEOFF:
            return FlyingState.TAKING_OFF;

        case TRANS_LANDING:
            return FlyingState.LANDING;

        default:
            return FlyingState.LANDED;
        }
    }
}