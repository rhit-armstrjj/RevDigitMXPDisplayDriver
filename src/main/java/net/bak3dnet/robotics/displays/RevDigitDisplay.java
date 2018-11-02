package net.bak3dnet.robotics.displays;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import net.bak3dnet.robotics.displays.modules.DisplayModuleBase;
import net.bak3dnet.robotics.displays.modules.TickerTapeModule;

/**
 * 
 * @author Jake Armstrong
 * @version 0.0.1
 * @since 0.1.0
 * 
 */
public class RevDigitDisplay {

    I2C i2c;

    private static RevDigitDisplay singleton;

    /**
     * Processor Chip Commands:
     * Source: https://cdn-shop.adafruit.com/datasheets/ht16K33v110.pdf
     * 
     * Constants!
     * 
     */

    //private static final Logger logger = LogManager.getLogger(RevDigitDisplay.class);

     /**
      * Sets the display to active.
      */
    public static final byte[] OSCILATOR_ON = {(byte) 0x21}; 
    /**
     *  Sets the display to inactive.
     */
    public static final byte[] OSCILATOR_OFF = {(byte) 0x20};

    /**
     * Sets the blinking to off.
     */
    private final byte[] DEFAULT_BLINKING = {(byte) 0x81};

    /**
     * Sets the brightness to bright.
     */
    private final byte[] DEFAULT_BRIGHTNESS = {(byte) 0xEF};

    public DigitalInput buttonA;
    public DigitalInput buttonB;

    public AnalogPotentiometer potentiometer;

    private DisplayModuleBase activeModule;
    private DisplayTaskManager taskManager;

    private Thread taskCoordinator;


    private static class DisplayTaskManager implements Runnable{

        private RevDigitDisplay display;

        public DisplayTaskManager(RevDigitDisplay display) {

            this.display = display;

        }

        public void run() {
            long previousTime = System.currentTimeMillis();
            while(true){

                long currentTime = System.currentTimeMillis();

                short deltaTime = (short)(currentTime - previousTime);

                display.getActiveModule().task(this.display,deltaTime);

                previousTime = currentTime;

                try {
                    Thread.sleep(1);
                    //logger.debug("Loop Completed");
				} catch (InterruptedException e) {
                    break;
                }

            }

        }

    }

    /**
     * Checks to see that a singleton has been made.
     */
    private static void singletonCheck() {

        //logger.debug("Checking for Singleton");
        if(singleton == null) {
            singleton = new RevDigitDisplay();
            //logger.debug("Singleton Created");
        }

    }
    /**
     * 
     * @return Returns the singleton class.
     */
    public static RevDigitDisplay getInstance() {

        //logger.debug("Getting an instance");
        singletonCheck();
        return singleton;

    }
    
    /**
     * @param setToString The string to pre initialize with.
     * 
     * @return Returns the singleton class, pre-initialized to ticker whatever string you have set.
     * 
     */
    public static RevDigitDisplay getInstance(String setToString) {

        //logger.debug("Getting Instance");
        singletonCheck();

        TickerTapeModule module = new TickerTapeModule();
        module.setDisplayText(setToString);

        singleton.setActiveModule(module);

        return singleton;

    }

    /**
     * @param dChars A set of dChars to pre-initialize with.
     * 
     *@return Returns the singleton class, pre-initialized to ticker whatever string you have set.
     *  
     */
    public static RevDigitDisplay getInstance(DChar[] dChars) {

        singletonCheck();

        TickerTapeModule tickerTapeModule = new TickerTapeModule();
        
        tickerTapeModule.setDisplayText(dChars);

        singleton.setActiveModule(tickerTapeModule);

        return singleton;

    }

    /**
     * @param number A double to be pre-initialized with.
     *@return Returns the singleton class, pre-initialized to ticker whatever string you have set.
     *  
     */
    public static RevDigitDisplay getInstance(double number) {

        singletonCheck();

        TickerTapeModule tickerTapeModule = new TickerTapeModule();
        tickerTapeModule.setDisplayText(number);

        singleton.setActiveModule(tickerTapeModule);

        return singleton;
    }

    private RevDigitDisplay() {

        buttonA = new DigitalInput(19);
        buttonB = new DigitalInput(20);

        potentiometer = new AnalogPotentiometer(4);

        i2c = new I2C(Port.kMXP,0x70);
        
        i2c.writeBulk(OSCILATOR_ON);
        
        Timer.delay(0.01);

        i2c.writeBulk(DEFAULT_BRIGHTNESS);

        Timer.delay(0.01);

        i2c.writeBulk(DEFAULT_BLINKING);
        
        Timer.delay(0.01);

        taskManager = new DisplayTaskManager(this);

    }

    /**
     * 
     * @param module Whichever module you want to have set to display.
     * 
     */
    public void setActiveModule(DisplayModuleBase module) {

        //logger.debug("Setting active module to {}", module.getClass());

        if(this.taskCoordinator != null) {
            
            //logger.debug("Interrupting taskCoordinator");
            this.taskCoordinator.interrupt();
        
        }
        
        //logger.debug("Setting active module");
        this.activeModule = module;
        //logger.debug("Creating new thread");
        this.taskCoordinator = new Thread(taskManager);
        //logger.info("Starting new thread");
        this.taskCoordinator.start();

    }

    /**
     * 
     * @return Returns the current module that is being displayed right now.
     * 
     */
    public DisplayModuleBase getActiveModule() {

        //logger.debug("Returning active module");
        return this.activeModule;

    }

    /**
     * Sets the string that is displayed on the display
     * 
     * @param text The array of DChars that will scroll on the displays
     * Warning the DChar Array will be truncated to four characters.
     * 
     */
    public void setText(DChar[] text) {
        //logger.debug("Setting text");

        DChar[] truncated = new DChar[4];
        Arrays.fill(truncated,DCharFactory.getDChar(' '));

        for(int i =0; i <4;i++) {

            //logger.debug("Setting index {} to {}", Integer.toString(i),Character.toString(text[i].getEncapsulatedChar()));
            try {
                truncated[i] = text[i];
            } catch (ArrayIndexOutOfBoundsException e) {}

        }


        //byte[] preSend =  {(byte)0b00001111,(byte)0b00001111}; 

        List<Byte> sendData = new ArrayList<Byte>();

        sendData.add(new Byte((byte)0b00001111));
        sendData.add(new Byte((byte)0b00001111));

        for(int i = 0; i < truncated.length; i ++) {

            sendData.add(new Byte(truncated[3-i].getBinary()[0]));
            sendData.add(new Byte(truncated[3-i].getBinary()[1]));

        }

        byte[] finalData = new byte[sendData.size()];
        
        for(int i = 0; i < sendData.size(); i++) {
            
            finalData[i] = sendData.get(i);

        }

        i2c.writeBulk(finalData);
        Timer.delay(0.01);

    }

    /**
     * Sets the string that is displayed on the display;
     * 
     * @param text The string that will be displayed on the display.
     * Warning! The string will be truncated to four characters.
     */
    public void setText(String text) {

        DChar[] unTruncated = DCharFactory.getDChars(text);
        DChar[] truncated = new DChar[4];

        if(text.length() > 4) {

            for(int i = 0; i<4; i++) {

                truncated[i] = unTruncated[i];
    
            }

        } else {

            truncated = unTruncated;

        }

        setText(truncated);

    }

}