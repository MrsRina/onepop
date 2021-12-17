package rina.onepop.club.api.tool;

import java.util.HashMap;

/**
 * @author SrRina
 * @since 17/04/2021 at 16:26
 **/
public class CounterTool<T> {
    private final HashMap<T, Integer> node = new HashMap<>();

    public CounterTool() {
    }

    public void dispatch(T key) {
        this.node.put(key, getCount(key) == null ? 1 : getCount(key) + 1);
    }

    public void remove(T key) {
        this.node.remove(key);
    }

    public Integer getCount(T key) {
        return this.node.get(key);
    }

    public HashMap<T, Integer> getNode() {
        return node;
    }

    public void clear() {
        this.node.clear();
    }
}
