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

## Demo

A [demo application](com.io7m.ivoirax.demo) is included.

![Ivoirax](src/site/resources/ivoirax2.png)

