package uz.app.Anno;

import uz.app.Anno.Util.AnnoValidationException;

public abstract class BaseEntity {
    public abstract boolean isValid();

    public abstract void validate() throws AnnoValidationException;
}
