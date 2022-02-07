<p align="center">
  <a href="#"><img src="https://user-images.githubusercontent.com/5157755/152665520-7b8b9077-c37b-46d7-9aef-a32cfeb8457b.png"></a>
</p>

[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE_OF_CONDUCT.md)
[![License](https://img.shields.io/github/license/whirvex/stdio-commons)](https://choosealicense.com/licenses/mit/)
[![Java version](https://img.shields.io/badge/version-8-orange?style=flat&logo=java)](#)
[![Repo size](https://img.shields.io/github/repo-size/whirvis/ketill)](#)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/2be5a01acd504e9d8b5067ccfe4c79c4)](https://www.codacy.com/gh/Whirvis/ketill/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=whirvis/ketill&amp;utm_campaign=Badge_Grade)
[![Build Status](http://jenkins.ketill.io:8080/buildStatus/icon?job=ketill)](http://jenkins.ketill.io:8080/job/ketill/)
[![Unit Tests](https://img.shields.io/jenkins/tests?compact_message&jobUrl=http%3A%2F%2Fjenkins.kettle.io%2Fjob%2Fketill)](#)

#

### Summary

Ketill provides dynamic device I/O for Java. Created from code originally written for the [Ardenus Engine](https://github.com/whirvis/ardenus-engine),
it is intended for use in [game development](https://youtu.be/zCmMuOXr-Nk). However, other use cases are supported. Ketill provides an avenue to
provide universal definitions for I/O devices (such as Keyboards, Mice, XBOX controllers, etc.) while keeping implementation details separate.

### History

Ketill began development in the Fall Semester of 2021 at University. While working on a revision of the Ardenus Engine, I decided that I wanted to
create a new input system from the ground up (as the original was clunky.) At the time, Ketill was simply the `input` package of the engine. Later,
as the package grew in size, I realized it could work as its own library. This would allow me to use it in other projects seamlessly.

After some painful refactoring, the `input` package was moved to another repository and began life as a [Maven](https://maven.apache.org/) project
(like its ancestor.) After some more weeks of coding, I decided that the now monolothic library should be split into modules. While Maven supports
modules, I found them to be a nuisance. For the time though, I tolerated them as I had with [STD I/O Commons](https://github.com/whirvis/stdio-commons).
However, after tinkering with [PaperMC](https://papermc.io/) for the first time, I was properly introduced to [Gradle](https://gradle.org/). I was
so delighted by how clean the build scripts were that I decided to use it for my future projects. Thus, the switch from Maven to Gradle was made.

# 🎮 Why use Ketill?
By design, the definition of an `IoDevice` is kept separate from the code which implements it. The implementation of a device is provided by an
`IoDeviceAdapter`. Keeping the definition and implementation separate provides the following primary benefits:

1. The code for defining a device is (usually) small.
2. Said definitions rarely require logic, just a statement of features.
3. Device definitions requires little or no unit testing when properly written.
4. The same device can be implemented with different adapters, enabling features as desired.
5. Cross platform support (e.g., using `GlfwXboxAdapter` on MacOS and `XboxAdapter` on Windows.)

Ketill also provides a litany of built-in definitions and implementations. The goal of these built-in devices and adapters is to reduce the time
required to get device I/O up and running. They also serve as examples for those who wish to create their own devices and/or adapters.

The following is a list of modules included with Ketill:

| Module    | Description                                                        |
| --------- | ------------------------------------------------------------------ |
| `api`     | The base API for Ketill.                                           |
| `devices` | Provided definitions (`Keyboard`, `Mouse`, `XboxController`, etc.) |
| `glfw`    | Keyboard, mouse, and joystick support via GLFW.                    |
| `xinput`  | XBOX controller support via X-input.                               |

**Note:** Modules present in the repository but not listed above are not ready for use.

# 🔗 Dependencies

This section is a work in progress.

# 🛠️ Building

This section is a work in progress.

# ⚗️ Examples

This section is a work in progress.

# 🖥️ Technologies

| Category     | Technologies                                                                                                                                                                                                                               |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Development  | [Eclipse Adoptium](https://adoptium.net/), [IntelliJ IDEA](https://www.jetbrains.com/idea/), and [Java 8](https://www.oracle.com/java/technologies/java8.html)                                                                             |
| Build System | [Gradle](https://gradle.org/) and [Jenkins](https://www.jenkins.io/)                                                                                                                                                                       |
| Unit Testing | [JetBrains Annotations](https://github.com/JetBrains/java-annotations), [JUnit 5](https://junit.org/junit5/), and [Mockito](https://site.mockito.org/)                                                                                     |
| Adapters     | [GLFW 3](https://www.glfw.org/) (via [LWJGL 3](https://www.lwjgl.org/)), [JXInput](https://github.com/StrikerX3/JXInput), [Usb4Java](http://usb4java.org/quickstart/javax-usb.html), and [Hid4Java](https://github.com/gary-rowe/hid4java) |
| Libraries    | [GSON](https://github.com/google/gson) and [JOML](https://joml-ci.github.io/JOML/)                                                                                                                                                         |

# 💎 Special Thanks

- My friend [Surhou](https://t.co/gt2mqvXKaA), for creating the project logo. (It's not done yet!)
- My Uncle, for introducing me to [Mockito](https://site.mockito.org/) when I was seeking advice for writing unit tests.
- My colleagues at University, for cheering me on through the two semesters that I wrote this.
