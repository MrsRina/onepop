package rina.onepop.club.api.gui;

import me.rina.turok.util.TurokRect;

/**
 * @author SrRina
 * @since 17/11/20 at 11:12am
 */
public interface IScreenBasic {
  boolean isEnabled();

  /*
   * Get rect instance.
   */
  TurokRect getRect();

  /*
   * Verify if booleans focus;
   */
  boolean verifyFocus(int mx, int my);

  /*
   * Event opened on screen.
   */
  void onScreenOpened();
  void onCustomScreenOpened();

  /*
   * Event closed on screen.
   */
  void onScreenClosed();
  void onCustomScreenClosed();

  /*
   * Keyboard input.
   */
  void onKeyboardPressed(char c, int keyCode);
  void onCustomKeyboardPressed(char c, int keyCode);

  /*
   * Mouse clicks down.
   */
  void onMouseClicked(int button);
  void onCustomMouseClicked(int button);

  /*
   * Mouse clicks up.
   */
  void onMouseReleased(int button);
  void onCustomMouseReleased(int button);

  /*
   * Render ticks.
   */
  void onRender();
  void onCustomRender();

  /*
   * Saving!!
   */
  void onSave();
  void onLoad();
}