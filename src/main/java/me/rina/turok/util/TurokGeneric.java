package me.rina.turok.util;

/**
 * @author SrRina
 * @since 26/11/20 at 7:19pm
 */
public class TurokGeneric <S> {
    private S value;

    public TurokGeneric(final S value) {
        this.value = value;
    }

    public void setValue(S value) {
        this.value = value;
    }

    public S getValue() {
        return value;
    }
}
