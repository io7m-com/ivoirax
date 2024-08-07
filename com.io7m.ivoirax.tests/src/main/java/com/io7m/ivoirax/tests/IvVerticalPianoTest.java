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
    assertEquals(254.0, view.naturalKeyWidth());
    assertEquals(127.0, view.accidentalKeyWidth());
    assertEquals(24.0, view.naturalKeyHeight());
    assertTrue(view.accidentalKeyHeight() < view.naturalKeyHeight());
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
    final var events =
      Collections.synchronizedList(new ArrayList<IvKeyEventType>());

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
    assertEquals(0, events.size());
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
    final var events =
      Collections.synchronizedList(new ArrayList<IvKeyEventType>());

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
    piano.setOnKeyEventHandler(events::add);
    clickKey(robot, piano, 2);

    checkPressRelease(events, 0, 2);
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
    final var events =
      Collections.synchronizedList(new ArrayList<IvKeyEventType>());

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
    piano.setOnKeyEventHandler(events::add);
    clickKey(robot, piano, 3);

    checkPressRelease(events, 0, 3);
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
    final var events =
      Collections.synchronizedList(new ArrayList<IvKeyEventType>());

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
    piano.setOnKeyEventHandler(events::add);
    clickKeySecondary(robot, piano, 7);
    assertEquals(0, events.size());
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
    final var events =
      Collections.synchronizedList(new ArrayList<IvKeyEventType>());

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
    piano.setOnKeyEventHandler(events::add);
    clickKeySecondary(robot, piano, 8);
    assertEquals(0, events.size());
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
    final var events =
      Collections.synchronizedList(new ArrayList<IvKeyEventType>());

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
    piano.setOnKeyEventHandler(events::add);
    piano.naturalKeyHeightProperty().set(32.0);
    clickKey(robot, piano, 6);
    clickKey(robot, piano, 5);
    piano.naturalKeyHeightProperty().set(64.0);
    clickKey(robot, piano, 6);
    clickKey(robot, piano, 5);

    checkPressRelease(events, 0, 6);
    checkPressRelease(events, 2, 5);
    checkPressRelease(events, 4, 6);
    checkPressRelease(events, 6, 5);
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
    final var events =
      Collections.synchronizedList(new ArrayList<IvKeyEventType>());

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
    piano.setOnKeyEventHandler(events::add);

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

    dumpEvents(events);
    checkPressRelease(events, 0, 1);
    checkPressRelease(events, 2, 2);
    checkPressRelease(events, 4, 3);
  }

  private void dumpEvents(
    final List<IvKeyEventType> events)
  {
    events.forEach(event -> LOG.debug("Event: {}", event));
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
    final var events =
      Collections.synchronizedList(new ArrayList<IvKeyEventType>());

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
    piano.setOnKeyEventHandler(events::add);

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

    checkPressRelease(events, 0, 0);
    checkPressRelease(events, 2, 1);
    checkPressRelease(events, 4, 2);
    checkPressRelease(events, 6, 3);
  }

  private static void checkPressRelease(
    final List<IvKeyEventType> events,
    final int eventIndex,
    final int expected)
  {
    {
      final var e =
        assertInstanceOf(IvKeyPressed.class, events.get(eventIndex));
      assertEquals(expected, e.index());
      assertFalse(e.isSynthesized());
    }

    {
      final var e =
        assertInstanceOf(IvKeyReleased.class, events.get(eventIndex + 1));
      assertEquals(expected, e.index());
      assertFalse(e.isSynthesized());
    }
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
    final var events =
      Collections.synchronizedList(new ArrayList<IvKeyEventType>());

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
    piano.setOnKeyEventHandler(events::add);

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
        assertInstanceOf(IvKeyPressed.class, events.get(0));
      assertEquals(1, e.index());
      assertTrue(e.isSynthesized());
    }

    {
      final var e =
        assertInstanceOf(IvKeyReleased.class, events.get(1));
      assertEquals(1, e.index());
      assertTrue(e.isSynthesized());
    }
  }
}
