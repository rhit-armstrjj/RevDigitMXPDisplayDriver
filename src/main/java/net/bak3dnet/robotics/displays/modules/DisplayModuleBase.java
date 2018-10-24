package net.bak3dnet.robotics.displays.modules;

import net.bak3dnet.robotics.displays.RevDigitDisplay;

public interface DisplayModuleBase {

    public void task(RevDigitDisplay display, double deltaTime);

    public void close();

    @Override
    public String toString();

}
