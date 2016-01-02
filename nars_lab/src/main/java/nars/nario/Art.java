package nars.nario;


import nars.audio.sample.SampleLoader;
import nars.audio.sample.SonarSample;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class Art
{
    public static final int SAMPLE_BREAK_BLOCK = 0;
    public static final int SAMPLE_GET_COIN = 1;
    public static final int SAMPLE_MARIO_JUMP = 2;
    public static final int SAMPLE_MARIO_STOMP = 3;
    public static final int SAMPLE_MARIO_KICK = 4;
    public static final int SAMPLE_MARIO_POWER_UP = 5;
    public static final int SAMPLE_MARIO_POWER_DOWN = 6;
    public static final int SAMPLE_MARIO_DEATH = 7;
    public static final int SAMPLE_ITEM_SPROUT = 8;
    public static final int SAMPLE_CANNON_FIRE = 9;
    public static final int SAMPLE_SHELL_BUMP = 10;
    public static final int SAMPLE_LEVEL_EXIT = 11;
    public static final int SAMPLE_MARIO_1UP = 12;
    public static final int SAMPLE_MARIO_FIREBALL = 13;

    public static Image[][] mario;
    public static Image[][] smallMario;
    public static Image[][] fireMario;
    public static Image[][] enemies;
    public static Image[][] items;
    public static Image[][] level;
    public static Image[][] particles;
    public static Image[][] font;
    public static Image[][] bg;
    public static Image[][] map;
    public static Image[][] endScene;
    public static Image[][] gameOver;
    public static Image logo;
    public static Image titleScreen;

    public static SonarSample[] samples = new SonarSample[100];

    private static final Sequence[] songs = new Sequence[10];
    private static Sequencer sequencer;


     public static SonarSample loadSample(String resourceName)    {
        try
        {
            InputStream is = Art.class.getResourceAsStream("res" + resourceName);
            return SampleLoader.loadSample(is);
        }
        catch (Exception e)
        {
            System.out.println("Failed to load sample " + resourceName + ". Using silent sample");
            e.printStackTrace();
            return null;
            //return silentSample;
        }
    }
    @SuppressWarnings("HardcodedFileSeparator")
    public static void init(GraphicsConfiguration gc)
    {
        try
        {
            mario = cutImage(gc, "/mariosheet.png", 32, 32);
            smallMario = cutImage(gc, "/smallmariosheet.png", 16, 16);
            fireMario = cutImage(gc, "/firemariosheet.png", 32, 32);
            enemies = cutImage(gc, "/enemysheet.png", 16, 32);
            items = cutImage(gc, "/itemsheet.png", 16, 16);
            level = cutImage(gc, "/mapsheet.png", 16, 16);
            map = cutImage(gc, "/worldmap.png", 16, 16);
            particles = cutImage(gc, "/particlesheet.png", 8, 8);
            bg = cutImage(gc, "/bgsheet.png", 32, 32);
            logo = getImage(gc, "/logo.gif");
            titleScreen = getImage(gc, "/title.gif");
            font = cutImage(gc, "/font.gif", 8, 8);
            endScene = cutImage(gc, "/endscene.gif", 96, 96);
            gameOver = cutImage(gc, "/gameovergost.gif", 96, 64);

//            if (sound != null)
//            {
//                samples[SAMPLE_BREAK_BLOCK] = loadSample("/snd/breakblock.wav");
//                samples[SAMPLE_GET_COIN] = loadSample("/snd/coin.wav");
//                samples[SAMPLE_MARIO_JUMP] = loadSample("/snd/jump.wav");
//                samples[SAMPLE_MARIO_STOMP] = loadSample("/snd/stomp.wav");
//                samples[SAMPLE_MARIO_KICK] = loadSample("/snd/kick.wav");
//                samples[SAMPLE_MARIO_POWER_UP] = loadSample("/snd/powerup.wav");
//                samples[SAMPLE_MARIO_POWER_DOWN] = loadSample("/snd/powerdown.wav");
//                samples[SAMPLE_MARIO_DEATH] = loadSample("/snd/death.wav");
//                samples[SAMPLE_ITEM_SPROUT] = loadSample("/snd/sprout.wav");
//                samples[SAMPLE_CANNON_FIRE] = loadSample("/snd/cannon.wav");
//                samples[SAMPLE_SHELL_BUMP] = loadSample("/snd/bump.wav");
//                samples[SAMPLE_LEVEL_EXIT] = loadSample("/snd/exit.wav");
//                samples[SAMPLE_MARIO_1UP] = loadSample("/snd/1-up.wav");
//                samples[SAMPLE_MARIO_FIREBALL] = loadSample("/snd/fireball.wav");
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            songs[0] = MidiSystem.getSequence(new File("nars_lab/src/main/java/nars/nario/res/mus/smb3map1.mid"));
            songs[1] = MidiSystem.getSequence(new File("nars_lab/src/main/java/nars/nario/res/mus/smwovr1.mid"));
            songs[2] = MidiSystem.getSequence(new File("nars_lab/src/main/java/nars/nario/res/mus/smb3undr.mid"));
            songs[3] = MidiSystem.getSequence(new File("nars_lab/src/main/java/nars/nario/res/mus/smwfortress.mid"));
            songs[4] = MidiSystem.getSequence(new File("nars_lab/src/main/java/nars/nario/res/mus/smwtitle.mid"));
        }
        catch (Exception e)
        {
            sequencer = null;
            e.printStackTrace();
        }
    }

    @SuppressWarnings("HardcodedFileSeparator")
    private static Image getImage(GraphicsConfiguration gc, String imageName) throws IOException
    {
        BufferedImage source = ImageIO.read(new File("nars_lab/src/main/java/nars/nario/res/" + imageName));
        Image image = gc.createCompatibleImage(source.getWidth(), source.getHeight(), Transparency.BITMASK);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return image;
    }

    private static Image[][] cutImage(GraphicsConfiguration gc, String imageName, int xSize, int ySize) throws IOException
    {
        Image source = getImage(gc, imageName);
        Image[][] images = new Image[source.getWidth(null) / xSize][source.getHeight(null) / ySize];
        for (int x = 0; x < source.getWidth(null) / xSize; x++)
        {
            for (int y = 0; y < source.getHeight(null) / ySize; y++)
            {
                Image image = gc.createCompatibleImage(xSize, ySize, Transparency.BITMASK);
                Graphics2D g = (Graphics2D) image.getGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(source, -x * xSize, -y * ySize, null);
                g.dispose();
                images[x][y] = image;
            }
        }

        return images;
    }

    public static void startMusic(int song)
    {
        stopMusic();
        if (sequencer != null)
        {
            try
            {
                sequencer.open();
                sequencer.setSequence((Sequence)null);
                sequencer.setSequence(songs[song]);
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                sequencer.start();
            }
            catch (Exception e)
            {
            }
        }
    }

    public static void stopMusic()
    {
        if (sequencer != null)
        {
            try
            {
                sequencer.stop();
                sequencer.close();
            }
            catch (Exception e)
            {
            }
        }
    }
}