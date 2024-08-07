/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.ivoirax.core;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Paint;

/**
 * The base type of piano keyboards.
 */

public interface IvPianoType
{
  /**
   * @return The color used for accidental (black) keys when not over or pressed
   */

  default Paint colorKeyAccidental()
  {
    return this.colorKeyAccidentalProperty().get();
  }

  /**
   * @return The color used for accidental (black) keys when not over or pressed
   */

  SimpleObjectProperty<Paint> colorKeyAccidentalProperty();

  /**
   * @return The color used for accidental (black) keys when the cursor is over them
   */

  default Paint colorKeyAccidentalOver()
  {
    return this.colorKeyAccidentalOverProperty().get();
  }

  /**
   * @return The color used for accidental (black) keys when the cursor is over them
   */

  SimpleObjectProperty<Paint> colorKeyAccidentalOverProperty();

  /**
   * @return The color used for accidental (black) keys when the keys are pressed
   */

  default Paint colorKeyAccidentalPressed()
  {
    return this.colorKeyAccidentalPressedProperty().get();
  }

  /**
   * @return The color used for accidental (black) keys when the keys are pressed
   */

  SimpleObjectProperty<Paint> colorKeyAccidentalPressedProperty();

  /**
   * @return The color used for natural (white) keys when not over or pressed
   */

  default Paint colorKeyNatural()
  {
    return this.colorKeyNaturalProperty().get();
  }

  /**
   * @return The color used for natural (white) keys when not over or pressed
   */

  SimpleObjectProperty<Paint> colorKeyNaturalProperty();

  /**
   * @return The color used for natural (white) keys when the cursor is over them
   */

  default Paint colorKeyNaturalOver()
  {
    return this.colorKeyNaturalOverProperty().get();
  }

  /**
   * @return The color used for natural (white) keys when the cursor is over them
   */

  SimpleObjectProperty<Paint> colorKeyNaturalOverProperty();

  /**
   * @return The color used for natural (white) keys when the keys are pressed
   */

  default Paint colorKeyNaturalPressed()
  {
    return this.colorKeyNaturalPressedProperty().get();
  }

  /**
   * @return The color used for natural (white) keys when the keys are pressed
   */

  SimpleObjectProperty<Paint> colorKeyNaturalPressedProperty();

  /**
   * @return The number of keys on the keyboard
   */

  int keyCount();

  /**
   * Set the key event handler.
   *
   * @param handler The handler
   */

  void setOnKeyEventHandler(
    IvKeyEventHandlerType handler);

  /**
   * Perform a synthetic keypress. This is in contrast to keypresses that
   * occurred via the mouse.
   *
   * @param index The key index
   */

  void keyPress(
    int index);

  /**
   * Release a key.
   *
   * @param index The key index
   */

  void keyRelease(
    int index);

  /**
   * @param index The key index
   *
   * @return {@code true} if the given key is currently pressed
   */

  boolean keyIsPressed(
    int index);
}
