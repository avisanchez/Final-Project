package swingui;

import java.awt.Graphics;

public interface Container {
    /**
     * Resizes this container based on a change in size of one of its wrapped
     * components
     * 
     * @param c component which has changed size
     */
    public void updateSize(Component c);

    /**
     * Draws a rectangle with top-left corner at point {@code x, y}. Fits to the
     * largest component width and height which it encloses.
     * 
     * @param g graphics variable
     */
    public void outline(Graphics g);
}