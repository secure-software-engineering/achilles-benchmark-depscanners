package de.upb.achilles.generator.controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/** @author Andreas Dann created on 06.01.19 */
public final class SessionActions {
  private final EventHandler<KeyEvent> keyPressHandler;

  private final Runnable openSearchAction;

  public SessionActions(
      final EventHandler<KeyEvent> keyPressHandler, final Runnable openSearchAction) {
    this.keyPressHandler = keyPressHandler;
    this.openSearchAction = openSearchAction;
  }

  public EventHandler<KeyEvent> getKeyPressHandler() {
    return keyPressHandler;
  }

  public Runnable getOpenSearchAction() {
    return openSearchAction;
  }
}
