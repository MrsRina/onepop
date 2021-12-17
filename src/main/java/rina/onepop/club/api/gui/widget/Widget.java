package rina.onepop.club.api.gui.widget;

import rina.onepop.club.api.gui.IScreenBasic;
import me.rina.turok.util.TurokRect;

/**
 * @author SrRina
 * @since 17/11/20 at 11:12am
 */
public class Widget implements IScreenBasic {
  public TurokRect rect;

  public Widget(String tag) {
    this.rect = new TurokRect(tag, 0, 0);
  }

  @Override
  public boolean isEnabled() {
    return false;
  }

  @Override
  public TurokRect getRect() {
    return this.rect;
  }

  @Override
  public boolean verifyFocus(int mx, int my) {
    return false;
  }

  @Override
  public void onScreenOpened() {

  }

  @Override
  public void onCustomScreenOpened() {

  }

  @Override
  public void onScreenClosed() {

  }

  @Override
  public void onCustomScreenClosed() {

  }

  @Override
  public void onKeyboardPressed(char charCode, int keyCode) {

  }

  @Override
  public void onCustomKeyboardPressed(char charCode, int keyCode) {

  }

  @Override
  public void onMouseClicked(int button) {

  }

  @Override
  public void onCustomMouseClicked(int button) {

  }

  @Override
  public void onMouseReleased(int button) {

  }

  @Override
  public void onCustomMouseReleased(int button) {

  }

  @Override
  public void onRender() {

  }

  @Override
  public void onCustomRender() {

  }

  @Override
  public void onSave() {

  }

  @Override
  public void onLoad() {

  }
}