package swingui;

import javax.swing.JTextField;

import java.awt.event.ActionEvent;

public class TextField extends JTextField implements Component {

    private static int DEFAULT_WIDTH = 100;
    private static int DEFAULT_HEIGHT = 30;

    private Action actionToPerform = () -> System.out.println("TODO: Implement actionToPerform");
    private String defaultText = "";

    private Container container;

    public TextField(String defaultText) {
        super(defaultText);
        super.setBounds(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        super.addActionListener(this);
        this.defaultText = defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public void setPosition(int x, int y) {
        this.setBounds(x, y, getWidth(), getHeight());
    }

    public void setSize(int width, int height) {
        this.setBounds(getX(), getY(), width, height);
    }

    public void onClick(Action actionToPerform) {
        this.actionToPerform = actionToPerform;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (actionToPerform == null) {
            return;
        }
        actionToPerform.execute();
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