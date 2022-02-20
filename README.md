<p align="center">
  <a href="#void"><img src="https://user-images.githubusercontent.com/5157755/153051741-39f3846f-f7cc-4be4-ad0e-09fbf6fb8488.png" alt="ASCII art displaying KETILL"></a>
</p>

<p align="center">
  <a href="CODE_OF_CONDUCT.md"><img src="https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg" alt="Contributor Covenant 2.1"></a>
  <a href="LICENSE"><img src="https://img.shields.io/github/license/whirvis/ketill" alt="MIT License"></a>
  <a href="https://www.oracle.com/java/technologies/java8.html"><img src="https://img.shields.io/badge/version-8-orange?style=flat&logo=java" alt="Java 8"></a>
  <a href="#void"><img src="https://img.shields.io/github/repo-size/whirvis/ketill" alt=""></a>
  <a href="https://www.codacy.com/gh/Whirvis/ketill/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=whirvis/ketill&amp;utm_campaign=Badge_Grade"><img src="https://app.codacy.com/project/badge/Grade/2be5a01acd504e9d8b5067ccfe4c79c4" alt="Code Quality"></a>
  <a href="http://jenkins.ketill.io/job/ketill/"><img src="http://jenkins.ketill.io/buildStatus/icon?job=ketill" alt="Jenkins"></a>
</p>

#

### 🔖 Summary

Ketill provides dynamic device I/O for Java. Created from code originally written for the [Ardenus Engine](https://github.com/whirvis/ardenus-engine),
it is intended for use in [game development](https://youtu.be/zCmMuOXr-Nk). However, other use cases are supported. Ketill paves an avenue to
supply universal definitions for I/O devices (such as Keyboards, Mice, XBOX controllers, etc.) while keeping implementation details separate.

### 📓 History

Ketill began development in the Fall Semester of 2021 at University. While working on a revision of the Ardenus Engine, I decided that I wanted to
create a new input system from the ground up (as the original was clunky.) At the time, Ketill was simply the `input` package of the engine. Later,
as the package grew in size, I realized it could work as its own library. This would allow me to use it in other projects seamlessly.

After some painful refactoring, the `input` package was moved to another repository and began life as a [Maven](https://maven.apache.org/) project
(like its ancestor.) After some more weeks of coding, I decided that the now monolothic library should be split into modules. While Maven supports
modules, I found them to be a nuisance. For the time though, I tolerated them as I had with [STD I/O Commons](https://github.com/whirvis/stdio-commons).
However, after tinkering with [PaperMC](https://papermc.io/) for the first time, I was properly introduced to [Gradle](https://gradle.org/). I was
so delighted by how clean the build scripts were that I decided to use it for my future projects. Thus, the switch from Maven to Gradle was made.

# 👾 Why use Ketill?
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

| Module     | Description                                                        |
|------------|--------------------------------------------------------------------|
| `api`      | The base API for Ketill.                                           |
| `devices`  | Provided definitions (`Keyboard`, `Mouse`, `XboxController`, etc.) |
| `adapters` | Provided implementations using GLFW, X-input, etc.                 |

# 🛠️ Building

If you don't want to build Ketill yourself, pre-built JARs can be found on [Jenkins](https://jenkins.ketill.io:8080). However, in certain
situations, building the project yourself may be necessary (e.g., if Jenkins is down.) Building Ketill is simple, and can be done following
the instructions below:

  1. Install [Git](https://git-scm.com/) if not done so already.
  2. Install the JDK. Builds of the OpenJDK can be found [here](https://adoptium.net/).
  4. Open a terminal of your choice and run the following commands:

```bash
git clone https://github.com/whirvis/ketill.git
cd ketill
chmod +x ./gradlew # unix only
./gradlew build

# install to local Maven repository if desired
./gradlew publishToMavenLocal
```

# ⚗️ Examples

This section is a work in progress.

# 🖥️ Technologies

| Category          | Technologies                                                                                                                                                                                                                               |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Development       | [Eclipse Adoptium](https://adoptium.net/), [IntelliJ IDEA](https://www.jetbrains.com/idea/), and [Java 8](https://www.oracle.com/java/technologies/java8.html)                                                                             |
| Build System      | [Gradle](https://gradle.org/) and [Jenkins](https://www.jenkins.io/)                                                                                                                                                                       |
| Unit Testing      | [JetBrains Annotations](https://github.com/JetBrains/java-annotations), [JUnit 5](https://junit.org/junit5/), and [Mockito](https://site.mockito.org/)                                                                                     |
| Quality Assurance | [Codacy](https://www.codacy.com/)                                                                                                                                                                                                          |
| Adapters          | [GLFW 3](https://www.glfw.org/) (via [LWJGL 3](https://www.lwjgl.org/)), [JXInput](https://github.com/StrikerX3/JXInput), [Usb4Java](http://usb4java.org/quickstart/javax-usb.html), and [Hid4Java](https://github.com/gary-rowe/hid4java) |
| Libraries         | [GSON](https://github.com/google/gson) and [JOML](https://joml-ci.github.io/JOML/)                                                                                                                                                         |

# 💎 Special Thanks

- My friend [Surhou](https://t.co/gt2mqvXKaA), for creating the project logo (which is still in progress, the one above is a placeholder.)
- My Uncle, for introducing me to [Mockito](https://site.mockito.org/) when I was seeking advice for writing unit tests.
- My colleagues at University, for cheering me on through the two semesters that I wrote this.

# 

<p align="center">
  <a href="https://twitter.com/whirvis/"><img src="https://img.shields.io/twitter/follow/whirvis?style=flat&logo=twitter&color=%2300acee&label=%40whirvis" alt="Twitter"></a>
  <a href="https://reddit.com/u/whirvis/"><img src="https://img.shields.io/reddit/user-karma/combined/whirvis?style=flat&logo=reddit&color=%23FF5700&label=u%2Fwhirvis" alt="Reddit"></a>
  <a href="https://youtube.com/c/whirvis/"><img src="https://img.shields.io/youtube/channel/subscribers/UC9wxFSON2eQRSxE2OUznP8w?style=flat&logo=youtube&logoColor=red&label=Whirvis" alt="YouTube"></a>
  <a href="https://www.twitch.tv/whirvis/"><img src="https://img.shields.io/twitch/status/whirvis?style=flat&logo=twitch&color=%23815fc0&label=Whirvis" alt="Twitch"></a>
  <a href="https://discord.gg/ShVPZBY6kY"><img src="https://img.shields.io/discord/681551864902320156?logo=Discord&color=%235865F2&label=Whirvex Software" alt="Discord"></a>
</p>
