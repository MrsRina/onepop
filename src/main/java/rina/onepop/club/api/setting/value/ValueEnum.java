package rina.onepop.club.api.setting.value;

import rina.onepop.club.api.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author SrRina
 * @since 20/01/2021 at 09:55
 **/
public class ValueEnum extends Setting {
    private final List<Enum<?>> valueList;
    private Enum<?> value;

    private int index;

    public ValueEnum(String name, String tag, String description, Enum<?> value) {
        super(name, tag, description);

        this.value = value;

        this.valueList = new ArrayList<>();
        this.valueList.addAll(Arrays.asList(value.getDeclaringClass().getEnumConstants()));

        int id = this.valueList.indexOf(this.value);

        this.index = id != -1 ? id : 0;
    }

    public void setValue(Enum<?> p_Enum) {
        int id = this.valueList.indexOf(p_Enum);

        this.index = id != -1 ? id : 0;
        this.value = this.valueList.get(this.index);
    }

    public Enum<?> getValue() {
        return value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public List<Enum<?>> getValueList() {
        return valueList;
    }

    /**
     * Update the place index!
     */
    public void updateIndex() {
        if (this.index >= this.valueList.size() - 1) {
            this.index = 0;
        } else {
            this.index++;
        }
    }
}