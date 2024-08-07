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

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * A horizontal piano.
 */

public final class IvHorizontalPiano
  extends Pane
  implements IvPianoType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(IvHorizontalPiano.class);

  private static final int KEY_COUNT_DEFAULT = (12 * 12) + 1;

  private final DoubleBinding accidentalKeyHeight;
  private final DoubleBinding accidentalKeyWidth;
  private final HashMap<Integer, Key> keysAll;
  private final ObservableMap<Integer, KeyPressed> keysPressed;
  private final ReadOnlyDoubleProperty naturalKeyHeight;
  private final SimpleDoubleProperty naturalKeyWidth;
  private final SimpleObjectProperty<Paint> colorKeyAccidental;
  private final SimpleObjectProperty<Paint> colorKeyAccidentalOver;
  private final SimpleObjectProperty<Paint> colorKeyAccidentalPressed;
  private final SimpleObjectProperty<Paint> colorKeyNatural;
  private final SimpleObjectProperty<Paint> colorKeyNaturalOver;
  private final SimpleObjectProperty<Paint> colorKeyNaturalPressed;
  private final SimpleObjectProperty<Font> keyFont;
  private final int keyCount;
  private final ArrayList<KeyNatural> naturalKeys;
  private final SimpleObjectProperty<Paint> keyTextColor;
  private IvKeyEventHandlerType keyEvent;

  /**
   * A horizontal piano.
   */

  public IvHorizontalPiano()
  {
    this(KEY_COUNT_DEFAULT);
  }

  /**
   * A horizontal piano.
   *
   * @param inKeyCount The number of keyboard keys
   */

  public IvHorizontalPiano(
    final int inKeyCount)
  {
    if (inKeyCount <= 0) {
      throw new IllegalArgumentException("Key count must be positive.");
    }

    this.keyEvent =
      (event) -> {
      };
    this.keyCount =
      inKeyCount;
    this.keysPressed =
      FXCollections.observableHashMap();
    this.keysAll =
      new HashMap<>(this.keyCount);

    this.keyFont =
      new SimpleObjectProperty<>(Font.font("Monospaced", FontWeight.BOLD, 9.0));
    this.keyTextColor =
      new SimpleObjectProperty<>(Color.gray(0.0));

    this.colorKeyNatural =
      new SimpleObjectProperty<>(Color.gray(1.0));
    this.colorKeyNaturalOver =
      new SimpleObjectProperty<>(Color.gray(0.9));
    this.colorKeyNaturalPressed =
      new SimpleObjectProperty<>(Color.gray(0.8));

    this.colorKeyAccidental =
      new SimpleObjectProperty<>(Color.gray(0.1));
    this.colorKeyAccidentalOver =
      new SimpleObjectProperty<>(Color.gray(0.3));
    this.colorKeyAccidentalPressed =
      new SimpleObjectProperty<>(Color.gray(0.5));

    this.naturalKeyWidth =
      new SimpleDoubleProperty(24.0);
    this.accidentalKeyWidth =
      this.naturalKeyWidth.divide(2.0);
    this.naturalKeyHeight =
      this.heightProperty();
    this.accidentalKeyHeight =
      this.naturalKeyHeight.divide(3.0)
        .multiply(2.0);
    this.naturalKeys =
      new ArrayList<>(this.keyCount);

    this.createNaturalKeys();
    this.createAccidentalKeys();
    this.layoutKeys();

    this.naturalKeyWidthProperty()
      .addListener((observable, oldValue, newValue) -> this.layoutKeys());

    this.keysPressed.addListener(
      (MapChangeListener<? super Integer, ? super KeyPressed>)
        this::onKeysPressedChanged
    );
  }

  private static boolean isAccidental(
    final int index)
  {
    return switch (index % 12) {
      case 0 -> false;
      case 1 -> true;
      case 2 -> false;
      case 3 -> true;
      case 4 -> false;
      case 5 -> false;
      case 6 -> true;
      case 7 -> false;
      case 8 -> true;
      case 9 -> false;
      case 10 -> true;
      case 11 -> false;
      default -> false;
    };
  }

  private void createAccidentalKeys()
  {
    for (final var naturalKey : this.naturalKeys) {
      final var sharpIndex = naturalKey.index() + 1;
      if (isAccidental(sharpIndex)) {
        final var accidentalKey = new KeyAccidental(sharpIndex);
        accidentalKey.setStroke(Color.BLACK);
        accidentalKey.setStrokeWidth(1.0);
        accidentalKey.setFill(this.colorKeyAccidental.get());

        accidentalKey.setOnMouseEntered(
          event -> accidentalKey.setFill(this.colorKeyAccidentalOver.get()));
        accidentalKey.setOnMouseExited(
          event -> accidentalKey.setFill(this.colorKeyAccidental.get()));
        accidentalKey.setOnMousePressed(
          event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
              this.keyPressedSet(accidentalKey, false);
            }
          });
        accidentalKey.setOnMouseReleased(event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            this.keysPressed.remove(accidentalKey.index());
            accidentalKey.keyDraggedOverLast().ifPresent(this::keyPressedUnset);
          }
        });
        accidentalKey.setOnMouseDragged(event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            this.onKeyDragged(event, accidentalKey);
          }
        });

        accidentalKey.widthProperty()
          .bind(this.accidentalKeyWidth);
        accidentalKey.heightProperty()
          .bind(this.accidentalKeyHeight);
        accidentalKey.layoutXProperty()
          .bind(
            naturalKey.layoutXProperty()
              .add(naturalKey.widthProperty())
              .subtract(accidentalKey.widthProperty().divide(2.0))
          );

        this.getChildren().add(accidentalKey);
        naturalKey.setSharp(accidentalKey);
        this.keysAll.put(accidentalKey.index(), accidentalKey);
      }
    }
  }

  private void createNaturalKeys()
  {
    for (var index = 0; index < this.keyCount; ++index) {
      if (!isAccidental(index)) {
        final var naturalKey = new KeyNatural(index);
        naturalKey.setStroke(Color.BLACK);
        naturalKey.setStrokeWidth(1.0);
        naturalKey.setFill(this.colorKeyNatural.get());

        naturalKey.setOnMouseEntered(
          event -> naturalKey.setFill(this.colorKeyNaturalOver.get()));
        naturalKey.setOnMouseExited(
          event -> naturalKey.setFill(this.colorKeyNatural.get()));
        naturalKey.setOnMousePressed(
          event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
              this.keyPressedSet(naturalKey, false);
            }
          });
        naturalKey.setOnMouseReleased(event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            this.keyPressedUnset(naturalKey);
            naturalKey.keyDraggedOverLast().ifPresent(this::keyPressedUnset);
          }
        });
        naturalKey.setOnMouseDragged(event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            this.onKeyDragged(event, naturalKey);
          }
        });

        naturalKey.heightProperty()
          .bind(this.naturalKeyHeight);
        naturalKey.widthProperty()
          .bind(this.naturalKeyWidth);

        this.naturalKeys.add(naturalKey);
        this.getChildren().add(naturalKey);
        this.keysAll.put(naturalKey.index(), naturalKey);

        if (index % 12 == 0) {
          final var label = new Label();
          label.setText("C%d".formatted(index / 12));
          label.setAlignment(Pos.BOTTOM_CENTER);
          label.fontProperty().bind(this.keyFont);
          label.textFillProperty().bind(this.keyTextColor);

          naturalKey.heightProperty()
            .addListener((observable, oldValue, newValue) -> {
              final var h = newValue.doubleValue();
              label.setPrefHeight(h);
              label.setMinHeight(h);
              label.setMaxHeight(h);
            });
          naturalKey.widthProperty()
            .addListener((observable, oldValue, newValue) -> {
              final var w = newValue.doubleValue();
              label.setPrefWidth(w);
              label.setMinWidth(w);
              label.setMaxWidth(w);
            });

          label.setFocusTraversable(false);
          label.setMouseTransparent(true);
          label.layoutXProperty().bind(naturalKey.layoutXProperty());
          this.getChildren().add(label);
        }
      }
    }
  }

  private void layoutKeys()
  {
    var naturalKeyX = 0.0;
    for (final var naturalKey : this.naturalKeys) {
      naturalKey.setLayoutX(naturalKeyX);
      naturalKeyX += this.naturalKeyWidth.get();
    }
  }

  @Override
  public SimpleObjectProperty<Paint> colorKeyAccidentalProperty()
  {
    return this.colorKeyAccidental;
  }

  @Override
  public SimpleObjectProperty<Paint> colorKeyAccidentalOverProperty()
  {
    return this.colorKeyAccidentalOver;
  }

  @Override
  public SimpleObjectProperty<Paint> colorKeyAccidentalPressedProperty()
  {
    return this.colorKeyAccidentalPressed;
  }

  @Override
  public SimpleObjectProperty<Paint> colorKeyNaturalProperty()
  {
    return this.colorKeyNatural;
  }

  @Override
  public SimpleObjectProperty<Paint> colorKeyNaturalOverProperty()
  {
    return this.colorKeyNaturalOver;
  }

  @Override
  public SimpleObjectProperty<Paint> colorKeyNaturalPressedProperty()
  {
    return this.colorKeyNaturalPressed;
  }

  /**
   * @return The current height of accidental keys
   */

  public double accidentalKeyHeight()
  {
    return this.accidentalKeyHeightProperty().get();
  }

  /**
   * @return The height of accidental keys
   */

  public DoubleExpression accidentalKeyHeightProperty()
  {
    return this.accidentalKeyHeight;
  }

  /**
   * @return The current width of accidental keys
   */

  public double accidentalKeyWidth()
  {
    return this.accidentalKeyWidthProperty().get();
  }

  /**
   * @return The width of accidental keys
   */

  public DoubleExpression accidentalKeyWidthProperty()
  {
    return this.accidentalKeyWidth;
  }

  @Override
  public int keyCount()
  {
    return this.keyCount;
  }

  /**
   * @return The current height of natural keys
   */

  public double naturalKeyHeight()
  {
    return this.naturalKeyHeightProperty().get();
  }

  /**
   * @return The height of natural keys
   */

  public ReadOnlyDoubleProperty naturalKeyHeightProperty()
  {
    return this.naturalKeyHeight;
  }

  /**
   * @return The current width of natural keys
   */

  public double naturalKeyWidth()
  {
    return this.naturalKeyWidthProperty().get();
  }

  /**
   * @return The width of natural keys
   */

  public SimpleDoubleProperty naturalKeyWidthProperty()
  {
    return this.naturalKeyWidth;
  }

  @Override
  public void setOnKeyEventHandler(
    final IvKeyEventHandlerType handler)
  {
    Objects.requireNonNull(handler, "handler");

    this.keyEvent = (event -> {
      try {
        handler.onKeyEvent(event);
      } catch (final Throwable e) {
        LOG.debug("Ignored exception in event handler: ", e);
      }
    });
  }

  @Override
  public void keyPress(
    final int index)
  {
    if (index >= 0 && index < this.keyCount) {
      this.keyPressedSet(this.keysAll.get(index), true);
    }
  }

  @Override
  public void keyRelease(
    final int index)
  {
    if (index >= 0 && index < this.keyCount) {
      this.keyPressedUnset(this.keysAll.get(index));
    }
  }

  @Override
  public boolean keyIsPressed(
    final int index)
  {
    if (index >= 0 && index < this.keyCount) {
      return this.keysPressed.get(index) != null;
    }
    return false;
  }

  private void onKeyDragged(
    final MouseEvent event,
    final Key keySource)
  {
    final var picked =
      event.getPickResult();
    final var pickedNode =
      picked.getIntersectedNode();

    /*
     * When clicking and dragging a key, the original key will receive
     * drag events that contain a "pick result" that can be used to identify
     * what key the mouse cursor is over now, mid-drag.
     *
     * As the cursor is dragged over the keys, we need to artificially
     * press and release those keys as the cursor passes over them. We avoid
     * pressing and releasing the same key in the same drag event by only
     * releasing a key if it isn't the one that's currently picked.
     */

    if (pickedNode instanceof final Key keyPicked) {
      final var keyDraggedLastOpt =
        keySource.keyDraggedOverLast();

      if (keyDraggedLastOpt.isPresent()) {
        final var keyDraggedLast = keyDraggedLastOpt.get();
        if (keyDraggedLast != keyPicked) {
          this.keyPressedUnset(keyDraggedLast);
        }
      }

      keySource.setKeyDraggedOverLast(keyPicked);
      this.keyPressedSet(keyPicked, false);
    }
  }

  private void keyPressedUnset(
    final Key key)
  {
    this.keysPressed.remove(key.index());
  }

  private void keyPressedSet(
    final Key key,
    final boolean isSynthesized)
  {
    this.keysPressed.put(
      key.index(),
      new KeyPressed(key, isSynthesized)
    );
  }

  private void onKeysPressedChanged(
    final MapChangeListener.Change<? extends Integer, ? extends KeyPressed> change)
  {
    if (change.wasAdded()) {
      final var keyPressed =
        change.getValueAdded();
      final var addedKey =
        keyPressed.key();

      LOG.trace("KeyPressed: {}", addedKey);
      this.keyEvent.onKeyEvent(
        new IvKeyPressed(addedKey.index(), keyPressed.isSynthesized())
      );

      switch (addedKey) {
        case final KeyAccidental k -> {
          k.setFill(this.colorKeyAccidentalPressed.get());
        }
        case final KeyNatural k -> {
          k.setFill(this.colorKeyNaturalPressed.get());
        }
      }
    }

    if (change.wasRemoved()) {
      final var keyPressed =
        change.getValueRemoved();
      final var removedKey =
        keyPressed.key();

      LOG.trace("KeyReleased: {}", removedKey);
      this.keyEvent.onKeyEvent(
        new IvKeyReleased(removedKey.index(), keyPressed.isSynthesized())
      );

      switch (removedKey) {
        case final KeyAccidental k -> {
          k.setFill(this.colorKeyAccidental.get());
        }
        case final KeyNatural k -> {
          k.setFill(this.colorKeyNatural.get());
        }
      }
    }
  }

  /**
   * Find the X position of the given key.
   *
   * @param index The key index
   *
   * @return The X position
   */

  public double xPositionOf(
    final int index)
  {
    if (index >= 0 && index < this.keyCount) {
      return this.keysAll.get(index).getLayoutX();
    }
    return 0.0;
  }

  /**
   * Find the X position of the center of the given key.
   *
   * @param index The key index
   *
   * @return The X position
   */

  public double xPositionCenterOf(
    final int index)
  {
    return this.xPositionOf(index) + (this.naturalKeyWidth() / 2.0);
  }

  private record KeyPressed(
    Key key,
    boolean isSynthesized)
  {

  }

  private static sealed abstract class Key
    extends Rectangle
  {
    private final int index;
    private Key keyDraggedOverLast;

    Key(
      final int inIndex)
    {
      this.index = inIndex;
    }

    public final int index()
    {
      return this.index;
    }

    public Optional<Key> keyDraggedOverLast()
    {
      return Optional.ofNullable(this.keyDraggedOverLast);
    }

    public void setKeyDraggedOverLast(
      final Key key)
    {
      this.keyDraggedOverLast = key;
    }
  }

  private static final class KeyAccidental
    extends Key
  {
    KeyAccidental(
      final int index)
    {
      super(index);
    }

    @Override
    public String toString()
    {
      return "[KeyAccidental %d]".formatted(this.index());
    }
  }

  private static final class KeyNatural
    extends Key
  {
    private KeyAccidental sharp;

    KeyNatural(
      final int index)
    {
      super(index);
    }

    public void setSharp(
      final KeyAccidental newSharp)
    {
      this.sharp = Objects.requireNonNull(newSharp, "sharp");
    }

    @Override
    public String toString()
    {
      return "[KeyNatural %d]".formatted(this.index());
    }
  }
}
