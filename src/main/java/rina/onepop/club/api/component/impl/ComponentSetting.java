package rina.onepop.club.api.component.impl;

/**
 * @author SrRina
 * @since 30/11/20 at 8:32pm
 *
 * @param <T> The value. :o
 **/
public class ComponentSetting<T> {
  private String name;
  private String tag;
  private String description;

  private T value;
  private T minimum;
  private T maximum;

  public ComponentSetting(String name, String tag, String description, T value) {
    this.name = name;
    this.tag = tag;
    this.description = description;

    this.value = value;
  }

  public ComponentSetting(String name, String tag, String description, T value, T minimum, T maximum) {
    this.name = name;
    this.tag = tag;
    this.description = description;

    this.value = value;

    this.minimum = minimum;
    this.maximum = maximum;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getTag() {
    return tag;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  public T getMinimum() {
    return minimum;
  }

  public T getMaximum() {
    return maximum;
  }
}