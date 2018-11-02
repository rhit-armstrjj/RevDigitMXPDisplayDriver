package net.bak3dnet.robotics.displays.modules;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import edu.wpi.first.wpilibj.RobotController;

import net.bak3dnet.robotics.displays.RevDigitDisplay;

/**
 * A module dedicated to showing the current battery percentage of the battery.
 * 
 * @author Jake Armstrong
 * @version 0.1.0
 * @since 0.1.0
 */
public class BatteryPercentModule implements DisplayModuleBase {

    private static final NumberFormat subHundo;
    private static final NumberFormat supHundo;

    private double[] averageArray = new double[5];

    private int check;
    private long totalLoops;

    private double emptyVoltage;

    static {

        subHundo = new DecimalFormat("#.##");
        supHundo = new DecimalFormat("#.#");

    }

    /**
     * Constructs the BatteryPercentModule with the percentage at which the battery is at 0%.
     * 
     * @param minVoltage The voltage for when the battery is at 0%
     */
    public BatteryPercentModule(double minVoltage) {

        emptyVoltage = minVoltage;
        Arrays.fill(averageArray,0D);

    }

    @Override
    public void task(RevDigitDisplay display, double deltaTime) {

        String formattedString = getFormattedPercentage();

        if(totalLoops%1000==0){
            display.setText(formattedString);
        }
        check++;
        totalLoops++;

    }

    /**
     * Gets the percentage of the battery.
     * 
     * @return The current percentage of the battery.
     */
    private double getPercentage() {
        
        if(check > 4) {

            check = 0;

        }

        averageArray[check] = (RobotController.getBatteryVoltage()-emptyVoltage)*100D;

        double out = 0;

        for(int i = 0; i < 5; i++) {

            out += averageArray[i];

        }

        return out/5;

    }

    private String getFormattedPercentage() {

        double percentage = getPercentage();
        String formattedDecimal;

        if(percentage > 100) {

            formattedDecimal = supHundo.format(percentage);

        } else {

            formattedDecimal = subHundo.format(percentage);

        }

        if(percentage < 10&&percentage > 0) {

            formattedDecimal = "0" + formattedDecimal;

        } else if(percentage < 0 && percentage > -10) {

            formattedDecimal = "-0" + formattedDecimal;

        } else if(percentage < -10) {

            formattedDecimal = "-" + formattedDecimal;

        }

        return formattedDecimal;

    }

    public String toString() {

        return "Battery Percentage";

    }

    @Override
    public void close() {

    }

}