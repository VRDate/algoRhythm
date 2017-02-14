package regan_danny.javasynth;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 *
 * @see http://stackoverflow.com/a/12297384/714968
 */
public class CustomSliderUI extends BasicSliderUI {
	private BufferedImage img;
	
    public CustomSliderUI(JSlider b) {
        super(b);
        try {
        	img = ImageIO.read(CustomSliderUI.class.getResource("/regan_danny/javasynth/images/slider.png"));
        } 
        catch (IOException e) {
        	System.out.println(e);
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_QUALITY);
        super.paint(g, c);
    }
    
    @Override
    protected Dimension getThumbSize() {
    	try{
    		return new Dimension(img.getWidth(),img.getHeight());
    	}
    	catch(NullPointerException npe){
    		return new Dimension();
    	}
    }
    
    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.DARK_GRAY);
        g2d.fillRoundRect(trackRect.x + 11, trackRect.y, trackRect.width-22, trackRect.height,5,5);
    }
    
    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
        		 RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.drawImage(img,thumbRect.x,thumbRect.y,null);
    }
}
