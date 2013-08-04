
package com.codeminders.ardrone.controllers;

public class ControllerStateChange extends GameControllerState
{
    protected boolean squareChanged;
    protected boolean crossChanged;
    protected boolean circleChanged;
    protected boolean triangleChanged;
    protected boolean L1Changed;
    protected boolean R1Changed;
    protected boolean L2Changed;
    protected boolean R2Changed;
    protected boolean selectChanged;
    protected boolean startChanged;
    protected boolean leftJoystickPressChanged;
    protected boolean rightJoystickPressChanged;
    protected boolean PSChanged;
    protected int     hatSwitchLeftRightChange;
    protected int     hatSwitchUpDownChange;
    protected int     leftJoystickXChange;
    protected int     leftJoystickYChange;
    protected int     rightJoystickXChange;
    protected int     rightJoystickYChange;

    // composite change flags
    private boolean   buttonStateChanged;
    private boolean   hatChanged;
    private boolean   leftJoystickChanged;
    private boolean   rightJoystickChanged;
    private boolean   joysticksChanged;
    private boolean   changed;

    /**
     * 
     * @param o Old state
     * @param n New state
     */
    public ControllerStateChange(GameControllerState o, GameControllerState n)
    {
        super(n);

        if(o==null)
            o=n;
        
        squareChanged = o.square != n.square;
        crossChanged = o.cross != n.cross;
        circleChanged = o.circle != n.circle;
        triangleChanged = o.triangle != n.triangle;
        L1Changed = o.L1 != n.L1;
        R1Changed = o.R1 != n.R1;
        L2Changed = o.L2 != n.L2;
        R2Changed = o.R2 != n.R2;
        selectChanged = o.select != n.select;
        startChanged = o.start != n.start;
        leftJoystickPressChanged = o.leftJoystickPress != n.leftJoystickPress;
        rightJoystickPressChanged = o.rightJoystickPress != n.rightJoystickPress;
        PSChanged = o.PS != n.PS;

        hatSwitchLeftRightChange = n.hatSwitchLeftRight - o.hatSwitchLeftRight;
        hatSwitchUpDownChange = n.hatSwitchUpDown - o.hatSwitchUpDown;

        leftJoystickXChange = n.leftJoystickX - o.leftJoystickX;
        leftJoystickYChange = n.leftJoystickY - o.leftJoystickY;
        rightJoystickXChange = n.rightJoystickX - o.rightJoystickX;
        rightJoystickYChange = n.rightJoystickY - o.rightJoystickY;

        buttonStateChanged = squareChanged || crossChanged || circleChanged || triangleChanged || L1Changed
                || R1Changed || L2Changed || R2Changed || selectChanged || startChanged || leftJoystickPressChanged
                || rightJoystickPressChanged || PSChanged;

        hatChanged = hatSwitchLeftRightChange != 0 || hatSwitchUpDownChange != 0;

        leftJoystickChanged = leftJoystickXChange != 0 || leftJoystickYChange != 0;
        rightJoystickChanged = rightJoystickXChange != 0 || rightJoystickYChange != 0;

        joysticksChanged = leftJoystickChanged || rightJoystickChanged;

        changed = joysticksChanged || hatChanged || buttonStateChanged;

    }

    public int getHatSwitchLeftRightChange()
    {
        return hatSwitchLeftRightChange;
    }

    public int getHatSwitchUpDownChange()
    {
        return hatSwitchUpDownChange;
    }

    public int getLeftJoystickXChange()
    {
        return leftJoystickXChange;
    }

    public int getLeftJoystickYChange()
    {
        return leftJoystickYChange;
    }

    public int getRightJoystickXChange()
    {
        return rightJoystickXChange;
    }

    public int getRightJoystickYChange()
    {
        return rightJoystickYChange;
    }

    public boolean isButtonStateChanged()
    {
        return buttonStateChanged;
    }

    public boolean isChanged()
    {
        return changed;
    }

    public boolean isCircleChanged()
    {
        return circleChanged;
    }

    public boolean isCrossChanged()
    {
        return crossChanged;
    }

    public boolean isHatChanged()
    {
        return hatChanged;
    }

    public boolean isJoysticksChanged()
    {
        return joysticksChanged;
    }

    public boolean isL1Changed()
    {
        return L1Changed;
    }

    public boolean isL2Changed()
    {
        return L2Changed;
    }

    public boolean isLeftJoystickChanged()
    {
        return leftJoystickChanged;
    }

    public boolean isLeftJoystickPressChanged()
    {
        return leftJoystickPressChanged;
    }

    public boolean isPSChanged()
    {
        return PSChanged;
    }

    public boolean isR1Changed()
    {
        return R1Changed;
    }

    public boolean isR2Changed()
    {
        return R2Changed;
    }

    public boolean isRightJoystickChanged()
    {
        return rightJoystickChanged;
    }

    public boolean isRightJoystickPressChanged()
    {
        return rightJoystickPressChanged;
    }

    public boolean isSelectChanged()
    {
        return selectChanged;
    }

    public boolean isSquareChanged()
    {
        return squareChanged;
    }

    public boolean isStartChanged()
    {
        return startChanged;
    }

    public boolean isTriangleChanged()
    {
        return triangleChanged;
    }
}
