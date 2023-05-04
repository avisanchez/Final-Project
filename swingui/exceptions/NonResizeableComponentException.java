package swingui.exceptions;

public class NonResizeableComponentException extends RuntimeException {
    public NonResizeableComponentException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}