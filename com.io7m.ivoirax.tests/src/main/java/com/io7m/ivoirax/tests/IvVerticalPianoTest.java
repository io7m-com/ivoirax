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


package com.io7m.ivoirax.tests;

import com.io7m.ivoirax.core.IvKeyEnter;
import com.io7m.ivoirax.core.IvKeyEventType;
import com.io7m.ivoirax.core.IvKeyPressed;
import com.io7m.ivoirax.core.IvKeyReleased;
import com.io7m.ivoirax.core.IvVerticalPiano;
import com.io7m.xoanon.commander.api.XCCommanderType;
import com.io7m.xoanon.commander.api.XCRobotType;
import com.io7m.xoanon.extension.XoExtension;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(XoExtension.class)
public final class IvVerticalPianoTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(IvVerticalPianoTest.class);

  private List<IvKeyEventType> events;

  private void eventContains(
    final IvKeyEventType e)
  {
    assertTrue(
      this.events.contains(e),
      "Events must contain %s".formatted(e)
    );
  }

  private void eventIsBefore(
    final IvKeyEventType e0,
    final IvKeyEventType e1)
  {
    assertTrue(
      this.events.indexOf(e0) < this.events.indexOf(e1),
      "Event %s must be before event %s".formatted(e0, e1)
    );
  }

  private void dumpEvents()
  {
    this.events.forEach(event -> LOG.debug("Event: {}", event));
  }

  @BeforeEach
  public void setup()
  {
    this.events = Collections.synchronizedList(new ArrayList<>());
  }

  /**
   * The piano must have at least one key.
   *
   * @param commander The commander
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyRequired(
    final XCCommanderType commander)
    throws Exception
  {
    final var exRef =
      new AtomicReference<IllegalArgumentException>();

    commander.stageNewAndWait(newStage -> {
      final var ex =
        assertThrows(IllegalArgumentException.class, () -> {
          new IvVerticalPiano(0);
        });
      exRef.set(ex);
    });

    assertEquals("Key count must be positive.", exRef.get().getMessage());
  }

  /**
   * The piano has sane defaults.
   *
   * @param commander The commander
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoPropertyDefaults(
    final XCCommanderType commander)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano(100);
      view.setMaxWidth(32.0);
      view.setMinWidth(32.0);
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(800.0);
      newStage.setWidth(256.0);
    });

    final var view = pianoView.get();
    assertTrue(
      view.naturalKeyWidth() >= 240.0,
      "Natural key width %s must be >= 240.0 "
        .formatted(view.naturalKeyWidth())
    );
    assertTrue(
      view.accidentalKeyWidth() >= 120.0,
      "Accidental key width %s must be >= 120.0"
        .formatted(view.accidentalKeyWidth())
    );

    assertEquals(24.0, view.naturalKeyHeight());

    assertTrue(
      view.accidentalKeyWidth() < view.naturalKeyWidth(),
      "Accidental key width %s must be < natural key width %s"
        .formatted(
          view.accidentalKeyWidth(),
          view.naturalKeyWidth())
    );
    assertTrue(
      view.accidentalKeyHeight() < view.naturalKeyHeight(),
      "Accidental key height %s must be < natural key height %s"
        .formatted(
          view.accidentalKeyHeight(),
          view.naturalKeyHeight())
    );

    assertEquals(100, view.keyCount());
    assertEquals(Color.gray(0.1), view.colorKeyAccidental());
    assertEquals(Color.gray(0.3), view.colorKeyAccidentalOver());
    assertEquals(Color.gray(0.5), view.colorKeyAccidentalPressed());
    assertEquals(Color.gray(1.0), view.colorKeyNatural());
    assertEquals(Color.gray(0.8), view.colorKeyNaturalPressed());
    assertEquals(Color.gray(0.9), view.colorKeyNaturalOver());
  }

  /**
   * The piano has the expected number of keys.
   *
   * @param commander The commander
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyCount(
    final XCCommanderType commander)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano();
      pianoView.set(view);
      newStage.setScene(new Scene(view));
    });

    final var view = pianoView.get();
    assertEquals((12 * 12) + 1, view.keyCount());
  }

  /**
   * Pressing a piano key works.
   *
   * @param commander The commander
   * @param robot     The robot
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyPressCrashyHandler(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano();
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(800.0);
      newStage.setWidth(256.0);
    });

    final var piano = pianoView.get();
    piano.setOnKeyEventHandler(event -> {
      throw new IllegalArgumentException();
    });
    clickKey(robot, piano, 7);
    assertEquals(0, this.events.size());
  }

  /**
   * Pressing a piano key works.
   *
   * @param commander The commander
   * @param robot     The robot
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyPress(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano(12);
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(400.0);
      newStage.setWidth(256.0);
      newStage.setX(128.0);
      newStage.setY(128.0);
    });

    final var piano = pianoView.get();
    piano.setOnKeyEventHandler(this.events::add);
    clickKey(robot, piano, 7);

    final var kp0 = new IvKeyPressed(7, false);
    final var kr0 = new IvKeyReleased(7, false);
    final var ken0 = new IvKeyEnter(7);

    this.dumpEvents();
    this.eventContains(kp0);
    this.eventContains(kr0);
    this.eventContains(ken0);
    this.eventIsBefore(ken0, kp0);
    this.eventIsBefore(kp0, kr0);
  }

  /**
   * Pressing a piano key works.
   *
   * @param commander The commander
   * @param robot     The robot
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyPressAccidental(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano(12);
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(400.0);
      newStage.setWidth(256.0);
      newStage.setX(128.0);
      newStage.setY(128.0);
    });

    final var piano = pianoView.get();
    piano.setOnKeyEventHandler(this.events::add);
    clickKey(robot, piano, 8);

    final var kp0 = new IvKeyPressed(8, false);
    final var kr0 = new IvKeyReleased(8, false);
    final var ken0 = new IvKeyEnter(8);

    this.dumpEvents();
    this.eventContains(kp0);
    this.eventContains(kr0);
    this.eventContains(ken0);
    this.eventIsBefore(ken0, kp0);
    this.eventIsBefore(kp0, kr0);
  }

  /**
   * Pressing a piano key works.
   *
   * @param commander The commander
   * @param robot     The robot
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyPressSecondary(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano(12);
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(400.0);
      newStage.setWidth(256.0);
      newStage.setX(128.0);
      newStage.setY(128.0);
    });

    final var piano = pianoView.get();
    piano.setOnKeyEventHandler(this.events::add);
    clickKeySecondary(robot, piano, 7);

    final var ken0 = new IvKeyEnter(7);
    this.dumpEvents();
    this.eventContains(ken0);
  }

  /**
   * Pressing a piano key works.
   *
   * @param commander The commander
   * @param robot     The robot
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyPressAccidentalSecondary(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano(12);
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(400.0);
      newStage.setWidth(256.0);
      newStage.setX(128.0);
      newStage.setY(128.0);
    });

    final var piano = pianoView.get();
    piano.setOnKeyEventHandler(this.events::add);
    clickKeySecondary(robot, piano, 8);

    final var ken0 = new IvKeyEnter(8);
    this.dumpEvents();
    this.eventContains(ken0);
  }

  /**
   * Adjusting the key width works.
   *
   * @param commander The commander
   * @param robot     The robot
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoHeightAdjust(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano(12);
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(400.0);
      newStage.setWidth(256.0);
      newStage.setX(128.0);
      newStage.setY(128.0);
    });

    final var piano = pianoView.get();
    piano.setOnKeyEventHandler(this.events::add);
    piano.naturalKeyHeightProperty().set(32.0);
    clickKey(robot, piano, 2);
    clickKey(robot, piano, 1);
    piano.naturalKeyHeightProperty().set(64.0);
    clickKey(robot, piano, 2);
    clickKey(robot, piano, 1);

    final var ken1 = new IvKeyEnter(1);
    final var ken2 = new IvKeyEnter(2);
    final var kp1 = new IvKeyPressed(1, false);
    final var kp2 = new IvKeyPressed(2, false);
    final var kr1 = new IvKeyReleased(1, false);
    final var kr2 = new IvKeyReleased(2, false);

    this.dumpEvents();
    this.eventContains(ken1);
    this.eventContains(ken2);
    this.eventContains(kp1);
    this.eventContains(kp2);
    this.eventContains(kr1);
    this.eventContains(kr2);
    this.eventIsBefore(ken2, kp2);
    this.eventIsBefore(kp2, kr2);
    this.eventIsBefore(ken1, kp1);
    this.eventIsBefore(kp1, kr1);
  }

  /**
   * Dragging the mouse over the keyboard works.
   *
   * @param commander The commander
   * @param robot     The robot
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyDrag(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano(12);
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(400.0);
      newStage.setWidth(256.0);
      newStage.setX(128.0);
      newStage.setY(128.0);
    });

    final var piano = pianoView.get();
    piano.setOnKeyEventHandler(this.events::add);

    robot.evaluate(() -> {
      final var y =
        piano.yPositionCenterOf(1);
      final var position =
        piano.localToScreen(16.0, y);

      robot.robot().mouseMove(position);
      return null;
    });

    robot.evaluate(() -> {
      robot.robot().mousePress(MouseButton.PRIMARY);
      return null;
    });

    for (int index = 1; index < 4; ++index) {
      final int finalIndex = index;
      robot.evaluate(() -> {
        final var y =
          piano.yPositionCenterOf(finalIndex);
        final var position =
          piano.localToScreen(16.0, y);

        robot.robot().mouseMove(position);
        return null;
      });
      robot.waitForFrames(100);
    }

    robot.evaluate(() -> {
      robot.robot().mouseRelease(MouseButton.PRIMARY);
      return null;
    });
    robot.waitForFrames(100);

    this.dumpEvents();
    final var ken1 = new IvKeyEnter(1);
    final var ken2 = new IvKeyEnter(2);
    final var ken3 = new IvKeyEnter(3);
    final var kp1 = new IvKeyPressed(1, false);
    final var kp2 = new IvKeyPressed(2, false);
    final var kp3 = new IvKeyPressed(3, false);
    final var kr1 = new IvKeyReleased(1, false);
    final var kr2 = new IvKeyReleased(2, false);
    final var kr3 = new IvKeyReleased(3, false);

    this.dumpEvents();
    this.eventContains(ken1);
    this.eventContains(ken2);
    this.eventContains(ken3);
    this.eventContains(kp1);
    this.eventContains(kp2);
    this.eventContains(kp3);
    this.eventContains(kr1);
    this.eventContains(kr2);
    this.eventContains(kr3);
    this.eventIsBefore(ken3, kp3);
    this.eventIsBefore(kp3, kr3);
    this.eventIsBefore(ken2, kp2);
    this.eventIsBefore(kp2, kr2);
    this.eventIsBefore(ken1, kp1);
    this.eventIsBefore(kp1, kr1);
  }

  /**
   * Dragging the mouse over the keyboard works.
   *
   * @param commander The commander
   * @param robot     The robot
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyDragAccidental(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano(12);
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(400.0);
      newStage.setWidth(256.0);
      newStage.setX(128.0);
      newStage.setY(128.0);
    });

    final var piano = pianoView.get();
    piano.setOnKeyEventHandler(this.events::add);

    robot.evaluate(() -> {
      final var y =
        piano.yPositionCenterOf(0);
      final var position =
        piano.localToScreen(16.0, y);

      robot.robot().mouseMove(position);
      return null;
    });

    robot.evaluate(() -> {
      robot.robot().mousePress(MouseButton.PRIMARY);
      return null;
    });

    for (int index = 0; index < 4; ++index) {
      final int finalIndex = index;
      robot.evaluate(() -> {
        final var y =
          piano.yPositionCenterOf(finalIndex);
        final var position =
          piano.localToScreen(16.0, y);

        robot.robot().mouseMove(position);
        return null;
      });
      robot.waitForFrames(100);
    }

    robot.evaluate(() -> {
      robot.robot().mouseRelease(MouseButton.PRIMARY);
      return null;
    });
    robot.waitForFrames(100);

    this.dumpEvents();
    final var ken1 = new IvKeyEnter(1);
    final var ken2 = new IvKeyEnter(2);
    final var ken3 = new IvKeyEnter(3);
    final var kp1 = new IvKeyPressed(1, false);
    final var kp2 = new IvKeyPressed(2, false);
    final var kp3 = new IvKeyPressed(3, false);
    final var kr1 = new IvKeyReleased(1, false);
    final var kr2 = new IvKeyReleased(2, false);
    final var kr3 = new IvKeyReleased(3, false);

    this.dumpEvents();
    this.eventContains(ken1);
    this.eventContains(ken2);
    this.eventContains(ken3);
    this.eventContains(kp1);
    this.eventContains(kp2);
    this.eventContains(kp3);
    this.eventContains(kr1);
    this.eventContains(kr2);
    this.eventContains(kr3);
    this.eventIsBefore(ken3, kp3);
    this.eventIsBefore(kp3, kr3);
    this.eventIsBefore(ken2, kp2);
    this.eventIsBefore(kp2, kr2);
    this.eventIsBefore(ken1, kp1);
    this.eventIsBefore(kp1, kr1);
  }

  private static void clickKey(
    final XCRobotType robot,
    final IvVerticalPiano piano,
    final int index)
    throws Exception
  {
    robot.evaluate(() -> {
      final var y =
        piano.yPositionCenterOf(index);
      final var position =
        piano.localToScreen(16.0, y);

      robot.robot().mouseMove(position);
      return null;
    });

    robot.execute(() -> {
      robot.robot().mousePress(MouseButton.PRIMARY);
    });
    robot.waitForFrames(60);

    /*
     * Releasing the second button does nothing.
     */

    robot.execute(() -> {
      robot.robot().mouseRelease(MouseButton.SECONDARY);
    });
    robot.waitForFrames(60);

    robot.execute(() -> {
      robot.robot().mouseRelease(MouseButton.PRIMARY);
    });
    robot.waitForFrames(60);
  }

  private static void clickKeySecondary(
    final XCRobotType robot,
    final IvVerticalPiano piano,
    final int index)
    throws Exception
  {
    robot.evaluate(() -> {
      final var y =
        piano.yPositionCenterOf(index);
      final var position =
        piano.localToScreen(16.0, y);

      robot.robot().mouseMove(position);
      return null;
    });

    robot.execute(() -> {
      robot.robot().mousePress(MouseButton.SECONDARY);
    });
    robot.waitForFrames(60);

    robot.execute(() -> {
      robot.robot().mouseRelease(MouseButton.SECONDARY);
    });
    robot.waitForFrames(60);
  }

  /**
   * Pressing a piano key works (synthetically).
   *
   * @param commander The commander
   * @param robot     The robot
   *
   * @throws Exception On errors
   */

  @Test
  public void testPianoKeyPressSynthetic(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    final var pianoView = new AtomicReference<IvVerticalPiano>();
    commander.stageNewAndWait(newStage -> {
      final var view = new IvVerticalPiano(12);
      pianoView.set(view);
      newStage.setScene(new Scene(view));
      newStage.setHeight(400.0);
      newStage.setWidth(256.0);
      newStage.setX(128.0);
      newStage.setY(128.0);
    });

    final var piano = pianoView.get();
    piano.setOnKeyEventHandler(this.events::add);

    robot.execute(() -> {
      piano.keyPress(1);
    });
    assertTrue(piano.keyIsPressed(1));
    robot.execute(() -> {
      piano.keyRelease(1);
    });
    assertFalse(piano.keyIsPressed(1));

    robot.execute(() -> {
      piano.keyPress(-1);
    });
    assertFalse(piano.keyIsPressed(-1));
    robot.execute(() -> {
      piano.keyRelease(-1);
    });
    assertFalse(piano.keyIsPressed(-1));

    robot.execute(() -> {
      piano.keyPress(1024);
    });
    assertFalse(piano.keyIsPressed(1024));
    robot.execute(() -> {
      piano.keyRelease(1024);
    });
    assertFalse(piano.keyIsPressed(1024));

    {
      final var e =
        assertInstanceOf(IvKeyPressed.class, this.events.get(0));
      assertEquals(1, e.index());
      assertTrue(e.isSynthesized());
    }

    {
      final var e =
        assertInstanceOf(IvKeyReleased.class, this.events.get(1));
      assertEquals(1, e.index());
      assertTrue(e.isSynthesized());
    }
  }
}
