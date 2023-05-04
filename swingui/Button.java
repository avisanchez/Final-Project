package swingui;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;

public class Button extends JButton implements Component {

    private static int DEFAULT_WIDTH = 100;
    private static int DEFAULT_HEIGHT = 30;

    private Action actionToPerform = () -> System.out.println("TODO: Implement actionToPerform");
    private JPanel screenReference;

    private Container container;

    private void init(String title, int x, int y, int width, int height, JPanel screenReference) {
        super.setText(title);
        super.setBounds(x, y, width, height);
        super.addActionListener(this);
        screenReference.add(this);
        this.screenReference = screenReference;
        this.addComponentListener(this);
    }

    public Button(String title, JPanel screenReference) {
        super();
        this.init(title, 0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, screenReference);
    }

    public Button(String title, int x, int y, int width, int height, JPanel screenReference) {
        super();
        this.init(title, x, y, width, height, screenReference);
    }

    public Button(String title, int x, int y, JPanel screenReference) {
        super();
        this.init(title, x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, screenReference);

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
