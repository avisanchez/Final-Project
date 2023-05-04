package swingui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.awt.Graphics;

public class VStack implements Component, Container {

    private ArrayList<Component> houseComponents = new ArrayList<>();
    private int x, y, width, height, spacing;

    private Container container;

    public VStack(int x, int y, int spacing, Component... componentList) {

        if (spacing < 0) {
            System.out.println("ERROR: Negative Spacing. Spacing is now set to 0");
            spacing = 0;
        }

        this.x = x;
        this.y = y;

        int localY = y;
        for (Component component : componentList) {

            component.setContainer(this);

            component.setPosition(x, localY);
            localY += component.getHeight() + spacing;
            houseComponents.add(component);

            width = (component.getWidth() > width) ? component.getWidth() : width;
        }
        localY -= spacing;
        height = localY - y;
        this.spacing = spacing;
    }

    @Override
    public void setPosition(int x, int y) {
        int dx = x - this.x;
        int dy = y - this.y;

        for (Component component : houseComponents) {
            component.setPosition(component.getX() + dx, component.getY() + dy);
        }

        this.x = x;
        this.y = y;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
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

    /**
     * Moves a component to the bottom of the vertical stack. Updates
     * {@code width} and {@code height} accordingly.
     * 
     * @param component new component which is added to the stack
     */
    public void add(Component component) {
        int localY = y + height + spacing;

        component.setPosition(x, localY);
        localY += component.getHeight();

        width = (component.getWidth() > width) ? component.getWidth() : width;
        height = localY - y;

        component.setContainer(this);
    }

    @Override
    public void updateSize(Component c) {

        boolean containsComponent = false;
        for (int i = 0; i < houseComponents.size(); i++) {
            Component currentComponent = houseComponents.get(i);

            if (c.equals(currentComponent)) {
                containsComponent = true;
            }

            if (containsComponent) {
                Component previousComponent = houseComponents.get(i - 1);
                int newY = previousComponent.getY() + previousComponent.getHeight() + spacing;
                currentComponent.setPosition(x, newY);

                if (i == houseComponents.size() - 1) {
                    this.height = (newY + currentComponent.getHeight()) - y;
                }
            }

        }

        int newWidth = c.getWidth();
        this.width = newWidth > this.width ? newWidth : this.width;

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