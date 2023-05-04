package swingui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.awt.Graphics;

public class HStack implements Component, Container {

    private ArrayList<Component> housedComponents = new ArrayList<>();
    private int x, y, width, height, spacing;

    private Container container;

    public HStack(int x, int y, int spacing, Component... componentList) {

        if (spacing < 0) {
            System.out.println("ERROR: Negative Spacing. Spacing is now set to 0");
            spacing = 0;
        }

        this.x = x;
        this.y = y;

        int localX = x;
        for (Component component : componentList) {

            component.setContainer(this);

            component.setPosition(localX, y);
            localX += component.getWidth() + spacing;
            housedComponents.add(component);

            height = (component.getHeight() > height) ? component.getHeight() : height;
        }
        localX -= spacing;
        width = localX - x;
        this.spacing = spacing;
    }

    /**
     * Moves a component to the bottom of the vertical stack. Updates
     * {@code width} and {@code height} accordingly.
     * 
     * @param component new component which is added to the stack
     */
    public void add(Component component) {
        int localX = x + width + spacing;

        component.setPosition(localX, y);
        localX += component.getWidth();

        width = localX - x;
        height = (component.getHeight() > height) ? component.getHeight() : height;

        component.setContainer(this);
    }

    @Override
    public void setPosition(int x, int y) {
        int dx = x - this.x;
        int dy = y - this.y;

        for (Component component : housedComponents) {
            component.setPosition(component.getX() + dx, component.getY() + dy);
        }

        this.x = x;
        this.y = y;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;

        updateContainerSize();
    }

    @Override
    public void onClick(Action actionToPerform) {
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void outline(Graphics g) {
        g.drawRect(x, y, width, height);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void updateSize(Component c) {

        System.out.println("supposed to upsate size");

        boolean containsComponent = false;
        for (int i = 0; i < housedComponents.size(); i++) {
            Component currentComponent = housedComponents.get(i);

            if (c.equals(currentComponent)) {
                containsComponent = true;
            }

            if (containsComponent) {
                System.out.println("contains");
                Component previousComponent = housedComponents.get(i - 1);
                int newX = previousComponent.getX() + previousComponent.getWidth() + spacing;
                currentComponent.setPosition(newX, y);

                if (i == housedComponents.size() - 1) {
                    this.width = (newX + currentComponent.getWidth()) - x;
                    System.out.println("Updating witdth");
                }
            }

        }

        int newHeight = c.getHeight();
        this.height = newHeight > this.height ? newHeight : this.height;

        updateContainerSize();
    }

    public void updateContainerSize() {
        if (container == null) {
            return;
        }

        container.updateSize(this);
    }

    @Override
    public void setContainer(Container c) {
        container = c;
    }
}