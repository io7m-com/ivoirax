ivoirax
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.ivoirax/com.io7m.ivoirax.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.ivoirax%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.ivoirax/com.io7m.ivoirax?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/ivoirax/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/ivoirax.svg?style=flat-square)](https://codecov.io/gh/io7m-com/ivoirax)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=e6c35c)

![com.io7m.ivoirax](./src/site/resources/ivoirax.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/ivoirax/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/ivoirax/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/ivoirax/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/ivoirax/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/ivoirax/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/ivoirax/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/ivoirax/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/ivoirax/actions?query=workflow%3Amain.windows.temurin.lts)|

## ivoirax

A JavaFX piano component.

## Features

* Simple piano display component.
* Display a configurable number of octaves.
* Capture key press/release events.
* Available in horizontal and vertical forms.
* Written in pure Java 21.
* [OSGi-ready](https://www.osgi.org/).
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System).
* ISC license.

## Usage

Add a `IvHorizontalPiano` or `IvVerticalPiano` to your JavaFX layouts. Set a
`IvKeyEventHandlerType` function to receive key events.

### Key Terminology

Keys are divided into _naturals_ (the white keys) and _accidentals_
(the black keys), mirroring the standard names from music theory.

The reason for avoiding white/black terminology is that the piano controls
allow for complete control over key colors, and so keys may not actually be
"black" or "white":

![colors](src/site/resources/colors.png)

### Events

The piano controls publish events in response to user input. The following
events are published:

  * `IvKeyEnter`; the mouse cursor has moved over a specific key.
  * `IvKeyExit`; the mouse cursor is no longer over the specific key that it was previously over.
  * `IvKeyPressed`; the user has pressed the primary mouse button whilst over a key.
  * `IvKeyReleased`; the user has released the primary mouse button whilst over a key that was previously pressed.

The implementation attempts to provide sensible semantics with regard to
event delivery for individual keys. For example, for a given key `k`, the
implementation won't publish a `IvKeyReleased` event for `k` _before_ it
publishes a `IvKeyPressed` event for `k`. Due to the somewhat non-deterministic
nature of input event handling between the underlying platforms, the
implementation can't make many guarantees on the ordering between events
for _different_ keys. For a given key `k`, the implementation can largely
be trusted to publish events in the following orders:

```
IvKeyEnter k ⇒ IvKeyPressed k ⇒ IvKeyReleased k ⇒ IvKeyExit k

IvKeyEnter k ⇒ IvKeyExit k
```

## Demo

A [demo application](com.io7m.ivoirax.demo) is included.

![Ivoirax](src/site/resources/ivoirax2.png)

