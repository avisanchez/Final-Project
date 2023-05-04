package swingui;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

@SuppressWarnings("rawtypes")

public interface Component extends ActionListener, ComponentListener {

    /*
     * --------------------------------
     * Getter Methods
     * --------------------------------
     */

    /**
     * @return the x-coordinate of this component. This represents the left side of
     *         the component.
     */
    public int getX();

    /**
     * @return the y-coordinate of this component. This represents the top side of
     *         the component.
     */

    public int getY();

    /**
     * @return the width of this component
     */

    public int getWidth();

    /**
     * @return the height of this component
     */

    public int getHeight();

    /*
     * --------------------------------
     * Setter Methods
     * --------------------------------
     */

    /**
     * Moves this component. The new location of the top-left corner is specified by
     * {@code x} and {@code y}
     * 
     * @param x the new x-coordinate of this component
     * @param y the new y-coordinate of this component
     */

    public void setPosition(int x, int y);

    /**
     * Resizes this component
     * 
     * @param width  the new {@code width} of this component
     * @param height the new {@code height} of this component
     */

    public void setSize(int width, int height);

    /**
     * Sets the {@code container} variable of this component
     * 
     * @param c
     */
    public void setContainer(Container c);

    default public void onChange(Binding var, Action actionToPerform) {
        var.linkTo(this, actionToPerform);
    }

    /**
     * Defines an {@code action} to be executed when this component is clicked
     * 
     * @param actionToPerform a lambda function to be executed in
     *                        {@code Component.actionPerformed()}
     */

    public void onClick(Action actionToPerform);

    /**
     * Defines an action to be executed when a binding variable changes its value
     * 
     * @param var             binding variable which calls {@code actionToPerform}
     * @param actionToPerform a lambda function to be executed when
     *                        {@code var.set()} is
     *                        called
     */

    /*
     * --------------------------------
     * Update Methods
     * --------------------------------
     */

    public void updateContainerSize();

    /*
     * --------------------------------
     * Component Listener
     * --------------------------------
     */

    default public void componentResized(ComponentEvent e) {
        updateContainerSize();
    }

    default public void componentMoved(ComponentEvent e) {
        // do not allow
    }

    default public void componentShown(ComponentEvent e) {
        // TODO
    }

    default public void componentHidden(ComponentEvent e) {
        // TODO
    }

}
