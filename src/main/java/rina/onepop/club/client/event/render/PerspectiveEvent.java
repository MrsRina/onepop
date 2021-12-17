package rina.onepop.club.client.event.render;

import rina.onepop.club.api.event.Event;

public class PerspectiveEvent extends Event {
    private float aspect;

    public PerspectiveEvent(float aspect) {
        this.aspect = aspect;
    }

    public float getAspect() {
        return aspect;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }
}
