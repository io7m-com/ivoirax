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


package com.io7m.ivoirax.demo;

import com.io7m.ivoirax.core.IvHorizontalPiano;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * A piano demo.
 */

public final class IvHorizontalPianoMain
{
  private static final Logger LOG =
    LoggerFactory.getLogger(IvHorizontalPianoMain.class);

  private static final DoubleSpinnerValueFactory KEY_WIDTH_FACTORY =
    new DoubleSpinnerValueFactory(
    2.0,
    128.0,
    20.0,
    1.0
    );

  private final Stage stage;
  private VBox root;
  private Scene scene;
  private IvHorizontalPiano piano;
  private AnchorPane pianoContainer;
  private Spinner<Double> widthSpinner;
  private ScrollPane pianoScroll;
  private TextField events;
  private HBox controlsH;
  private CheckBox colorCheck;

  private IvHorizontalPianoMain(
    final Stage inStage)
  {
    this.stage = Objects.requireNonNull(inStage, "stage");
  }

  /**
   * A piano demo.
   *
   * @param args The command-line arguments
   */

  public static void main(
    final String[] args)
  {
    Platform.startup(() -> {
      try {
        final var stage = new Stage();
        stage.setTitle("Piano");
        stage.setMinWidth(640);
        stage.setMinHeight(160);
        stage.setHeight(240.0);
        stage.setWidth(800.0);

        final var demo = new IvHorizontalPianoMain(stage);
        demo.start();
      } catch (final Throwable e) {
        LOG.error("Error: ", e);
        Platform.exit();
      }
    });
  }

  private void start()
  {
    this.controlsH =
      new HBox();
    this.widthSpinner =
      new Spinner<>(KEY_WIDTH_FACTORY);
    this.colorCheck =
      new CheckBox("Colors");

    this.controlsH.getChildren().add(this.widthSpinner);
    this.controlsH.getChildren().add(this.colorCheck);
    HBox.setMargin(this.widthSpinner, new Insets(8.0));
    HBox.setMargin(this.colorCheck, new Insets(8.0));

    this.piano =
      new IvHorizontalPiano();

    this.colorCheck.selectedProperty()
      .addListener((observable, oldValue, newValue) -> {
        if (newValue.booleanValue()) {
          this.piano.colorKeyTextProperty()
            .set(Color.gray(1.0));

          this.piano.colorStrokeKeyAccidentalProperty()
            .set(Color.color(0.7, 0.9, 1.0));
          this.piano.colorStrokeKeyNaturalProperty()
            .set(Color.color(0.7, 0.9, 1.0));

          this.piano.colorKeyNaturalProperty()
            .set(Color.color(0.0, 0.1, 0.6));
          this.piano.colorKeyNaturalOverProperty()
            .set(Color.color(0.0, 0.2, 0.7));
          this.piano.colorKeyNaturalPressedProperty()
            .set(Color.color(0.0, 0.3, 0.8));

          this.piano.colorKeyAccidentalProperty()
            .set(Color.color(0.2, 0.5, 1.0));
          this.piano.colorKeyAccidentalOverProperty()
            .set(Color.color(0.3, 0.6, 1.0));
          this.piano.colorKeyAccidentalPressedProperty()
            .set(Color.color(0.4, 0.7, 1.0));
        } else {
          this.piano.colorKeyTextProperty()
            .set(Color.gray(0.0));

          this.piano.colorStrokeKeyAccidentalProperty()
            .set(Color.gray(0.0));
          this.piano.colorStrokeKeyNaturalProperty()
            .set(Color.gray(0.0));

          this.piano.colorKeyNaturalProperty()
            .set(Color.gray(1.0));
          this.piano.colorKeyNaturalOverProperty()
            .set(Color.gray(0.9));
          this.piano.colorKeyNaturalPressedProperty()
            .set(Color.gray(0.8));

          this.piano.colorKeyAccidentalProperty()
            .set(Color.gray(0.1));
          this.piano.colorKeyAccidentalOverProperty()
            .set(Color.gray(0.3));
          this.piano.colorKeyAccidentalPressedProperty()
            .set(Color.gray(0.5));
        }
      });

    this.root = new VBox();
    this.pianoContainer = new AnchorPane();
    this.pianoScroll = new ScrollPane(this.pianoContainer);
    VBox.setMargin(this.pianoScroll, new Insets(8.0));

    this.events = new TextField();
    VBox.setMargin(this.events, new Insets(8.0));
    this.piano.setOnKeyEventHandler(event -> {
      LOG.debug("Event: {}", event);
      this.events.setText(event.toString());
    });

    this.root.getChildren().add(this.controlsH);
    this.root.getChildren().add(this.pianoScroll);
    this.root.getChildren().add(this.events);

    this.piano.naturalKeyWidthProperty()
      .bind(this.widthSpinner.valueProperty());

    this.pianoScroll.fitToHeightProperty().set(true);

    VBox.setVgrow(this.pianoScroll, Priority.ALWAYS);

    this.pianoContainer.getChildren().add(this.piano);
    AnchorPane.setTopAnchor(this.piano, 8.0);
    AnchorPane.setBottomAnchor(this.piano, 8.0);
    AnchorPane.setLeftAnchor(this.piano, 8.0);
    AnchorPane.setRightAnchor(this.piano, 8.0);

    this.scene = new Scene(this.root);
    this.stage.setScene(this.scene);
    this.stage.show();
  }
}
