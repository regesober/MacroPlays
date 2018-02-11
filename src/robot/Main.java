/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author regesober
 */
public class Main {

    public static final int F3 = KeyEvent.VK_F3;
    public static final int F6 = KeyEvent.VK_F6;
    public static final int F8 = KeyEvent.VK_F8;
    public static final int ALT = KeyEvent.VK_ALT;
    public static final int R = KeyEvent.VK_R;
    public static final int KEY1 = KeyEvent.VK_1;
    public static final int KEY2 = KeyEvent.VK_2;
    public static final int ENTER = KeyEvent.VK_ENTER;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            //BufferedImage screenLine = getScreenLine(383, 392, 40);
            int feedCounter = 1;
            boolean feed;
            boolean resetPos;
            while (true) {
                long time = System.currentTimeMillis();
                Date date = new Date();
                if (feedCounter % 40 == 0) {
                    System.out.println("[" + date.toString() + "] Comida");
                    feed = true;
                } else {
                    System.out.println("[" + date.toString() + "] TP");
                    feed = false;
                }
                click(0, 0, 100);
                int xAcc = 195;
                int yAcc = 750;
                for (int a = 0; a < 20; a++) {
                    click(xAcc, yAcc, 50);
                    combineKeys(ALT, KEY2, 50);
                    delay(50);
                    if (isMapWrong(a)) {
                        System.out.println("[" + date.toString() + "] RPos Acc " + (a + 1));
                        resetPos = true;
                    } else {
                        resetPos = false;
                    }
                    if (feedCounter % 240 == 0 && (a == 1 || a == 6 || a == 7)) {
                        System.out.println("[" + date.toString() + "] RPos Acc " + (a + 1));
                        resetPos = true;
                    }
                    boolean dead = isDead();
                    if (dead) {
                        System.out.println("[" + date.toString() + "] Revive Acc " + (a + 1));
                        resetPos = true;
                    }
                    if (feed == true) {
                        delay(75);
                        if (a != 0) {
                            combineKeys(ALT, R, 50);
                        }
                        feedHom();
                    }
                    if (resetPos == true) {
                        resetPos(dead);
                    } else {
                        delay(75);
                        if (a != 7 && a != 1) {
                            useKey(F3, 50);
                        } else {
                            if (feedCounter % 3 == 0 && a == 7) {
                                useKey(F3, 50);
                            }
                            if (feedCounter % 2 == 0 && a == 1) {
                                useKey(F3, 50);
                            }
                        }
                    }
                    delay(75);
                    xAcc += 45;
                }
                time = System.currentTimeMillis() - time;
                System.out.println("Time taken: " + time / 1000.0 + " s");
                feedCounter += waitTime(time);
                System.out.println("Counter: " + feedCounter);
            }
        } catch (AWTException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void click(int x, int y, int delay) throws AWTException {
        Robot bot = new Robot();
        bot.mouseMove(x, y);
        bot.mousePress(InputEvent.BUTTON1_MASK);
        bot.delay(delay);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
        bot.delay(delay);
    }

    public static void clickNPC(int x, int y, int delay) throws AWTException {
        Robot bot = new Robot();
        bot.mouseMove(x, y);
        bot.delay((int) (1.5 * delay));
        bot.mousePress(InputEvent.BUTTON1_MASK);
        bot.delay(delay);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
        bot.delay(delay);
    }

    public static void useKey(int key, int delay) throws AWTException {
        Robot bot = new Robot();
        bot.keyPress(key);
        bot.delay(delay);
        bot.keyRelease(key);
        bot.delay(delay);
    }

    public static void combineKeys(int key1, int key2, int delay) throws AWTException {
        Robot bot = new Robot();
        bot.keyPress(key1);
        bot.delay(delay);
        bot.keyPress(key2);
        bot.delay(delay);
        bot.keyRelease(key2);
        bot.delay(delay);
        bot.keyRelease(key1);
        bot.delay(delay);
    }

    public static void delay(int delay) throws AWTException {
        Robot bot = new Robot();
        bot.delay(delay);
    }

    public static int waitTime(long time) throws InterruptedException {
        int ticks = (int) ((time / 15000) + 1);
        Thread.sleep(15000 - time % 15000);
        return ticks;
    }

    public static void resetPos(boolean dead) throws AWTException {
        combineKeys(ALT, KEY1, 50);
        delay(5000);
        if (dead) {
            clickNPC(465, 250, 600);
        }
        clickNPC(418, 250, 600);
        useKey(ENTER, 400);
        useKey(ENTER, 400);
        useKey(ENTER, 400);
        if (dead) {
            delay(2500);
            useKey(F6, 400);
            delay(400);
            useKey(F8, 400);
        }
    }

    public static boolean isDead() throws AWTException {
        return isWhite(getScreenLine(583, 392, 40));
    }

    public static boolean isMapWrong(int acc) throws AWTException, IOException {
        int x = 320;
        int y = 506;
        int width = 93;
        int height = 16;
        Robot robot = new Robot();
        BufferedImage img = robot.createScreenCapture(new Rectangle(x, y, width, height));
        int pixelCounter = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int key = img.getRGB(i, j);
                if (key == -157) {
                    pixelCounter++;
                }
            }
        }
        if (pixelCounter == 0) {
            return false;
        }
        if (acc == 0 || acc == 1 || acc == 6 || acc == 7) {
            return false;
        }
        return pixelCounter != 220;
    }

    public static BufferedImage getScreenLine(int x, int y, int length) throws AWTException {
        Robot robot = new Robot();
        Rectangle rect = new Rectangle(x, y, length, 1);
        BufferedImage img = robot.createScreenCapture(rect);
        return img;
    }

    public static boolean isWhite(BufferedImage line) {
        for (int i = 0; i < line.getWidth(); i++) {
            int rgb = line.getRGB(i, 0);
            for (int j = 1; j < 4; j++) {
                if (getByteValue(rgb, j) != 255) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int getByteValue(int rgb, int pos) {
        int mask = 0xFF << (8 * (3 - pos));
        int result = (rgb & mask) >> (8 * (3 - pos));
        return result;
    }

    public static void feedHom() throws AWTException {
        BufferedImage screenLine = getScreenLine(520, 320, 10);
        while (isWhite(screenLine)) {
            delay(200);
            clickNPC(590, 340, 200);
            delay(100);
            clickNPC(590, 360, 200);
            screenLine = getScreenLine(520, 320, 10);
            delay(200);
        }
    }

}
