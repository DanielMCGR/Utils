import com.mollin.yapi.*;
import com.mollin.yapi.enumeration.YeelightEffect;
import com.mollin.yapi.enumeration.YeelightFlowAction;
import com.mollin.yapi.enumeration.YeelightProperty;
import com.mollin.yapi.flow.YeelightFlow;
import java.util.Scanner;

/**
 * This class controls Xiaomi light bulbs (via Yeelight).
 * It is based on Florian Mollin's code, found on GitHub.
 *
 * @author Daniel Rocha
 * @version 1.0
 * @see <a href="//https://github.com/florian-mollin/yapi">Florian Mollin's Github</a>
 */

public class YeeLight {

    static String ip = "192.168.1.1";
    static int port = 12345;
    static YeelightEffect effectType = YeelightEffect.SUDDEN;
    static YeelightFlowAction FlowAc = YeelightFlowAction.TURN_OFF;
    static YeelightFlow FlowTes = new YeelightFlow(1, FlowAc);
    static int effectDuration = 0;
    static boolean Stop;

    /**
     * Gets the current rgb values of the light bulb
     *
     * @param device the Yeelight device
     * @return the rgb values (R,G,B)
     */
    public static String getRGB(YeelightDevice device) {
        try {
            int RGB = Integer.parseInt(device.getProperties().get(YeelightProperty.RGB));
            String binary = Integer.toBinaryString(RGB);
            binary = String.format("%24s", binary).replaceAll(" ", "0");
            int R = Integer.parseInt(binary.substring(0,8),2);
            int G = Integer.parseInt(binary.substring(8,16),2);
            int B = Integer.parseInt(binary.substring(16,24),2);
            return ("R: " + R + ", G: " + G + ", B: " + B);
        } catch(Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Connects to a device and sets a variable for use in the rest of the code
     *
     * @return the Yeelight device
     */
    public static YeelightDevice create() {
        try {
            YeelightDevice device = new YeelightDevice(ip, port, effectType, effectDuration);
            return device;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Turns on the light on the device
     *
     * @throws Exception
     */
    public static void turnOn() throws Exception {
        try { create().setPower(true);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    /**
     * Turns off the light on the device
     *
     * @throws Exception
     */
    public static void turnOff() throws Exception {
        try { create().setPower(false);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    /**
     * Gets the properties of the device
     *
     * @throws Exception
     */
    public static String getProp() throws Exception {
        try { return create().getProperties().toString();
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    /**
     * Gets the base properties of a Yeelight device
     *
     * @param device the device to get properties from
     * @return the properties in a full sentence (for use in the bot)
     * @throws Exception
     */
    public static String getBaseProp(YeelightDevice device) throws Exception {
        try {
            String mes = "Use the reactions bellow to control the lights!  (STATE:  "+
                    "Power=" + device.getProperties().get(YeelightProperty.POWER)+
                    ", Color=(" + YeeLight.getRGB(device)+
                    ", Brightness=" + device.getProperties().get(YeelightProperty.BRIGHTNESS) + ")";
            return mes;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
