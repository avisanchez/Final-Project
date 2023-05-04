package swingui;

import java.util.HashMap;

public class Binding<E> {

    HashMap<Component, Action> linkedComponents = new HashMap<>();

    E wrappedVar;

    public Binding(E wrappedVar) {
        this.wrappedVar = wrappedVar;
    }

    public E get() {
        return wrappedVar;
    }

    public void set(E updatedVar) {
        wrappedVar = updatedVar;

        for (Component each : linkedComponents.keySet()) {
            linkedComponents.get(each).execute();
        }
    }

    public void linkTo(Component c, Action a) {
        linkedComponents.put(c, a);
    }

}
