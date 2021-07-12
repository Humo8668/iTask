package uz.app.Anno.Exceptions;

public class AnnoValidationException extends Throwable {
    public String errorCode;

    public AnnoValidationException(String errorCode, String message)
    {
        super(message);
        this.errorCode = errorCode;
    }
}
