package uz.app.Anno.Util;

public class AnnoValidationException extends Throwable {
    public String errorCode;

    public AnnoValidationException(String errorCode, String message)
    {
        super(message);
        this.errorCode = errorCode;
    }
}
