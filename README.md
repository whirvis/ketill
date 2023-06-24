<p align="center">
  <a href="#void"><img src="https://user-images.githubusercontent.com/5157755/153051741-39f3846f-f7cc-4be4-ad0e-09fbf6fb8488.png" alt="ASCII art displaying KETILL"></a>
</p>

<p align="center">
  <a href="CODE_OF_CONDUCT.md"><img src="https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg" alt="Contributor Covenant 2.1"></a>
  <a href="LICENSE"><img src="https://img.shields.io/github/license/whirvis/ketill" alt="MIT License"></a>
  <a href="https://www.oracle.com/java/technologies/java8.html"><img src="https://img.shields.io/badge/version-8-orange?style=flat&logo=java" alt="Java 8"></a>
  <a href="#void"><img src="https://img.shields.io/github/repo-size/whirvis/ketill" alt=""></a>
  <a href="https://www.codacy.com/gh/Whirvis/ketill/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=whirvis/ketill&amp;utm_campaign=Badge_Grade"><img src="https://app.codacy.com/project/badge/Grade/2be5a01acd504e9d8b5067ccfe4c79c4" alt="Code Quality"></a>
</p>

#

### 🔖 Summary

Ketill is an API for dynamic device I/O. Derived from code for the [Ardenus Engine](https://github.com/whirvis/ardenus-engine), its original goal was to facilitate [game development](https://youtu.be/zCmMuOXr-Nk).
However, it has since evolved to support a wide range of applications. Ketill's primary objective is to provide a framework for defining I/O devices (e.g., keyboards, mice, gamepads, etc.) while keeping their implementation details separate.

### 📓 History

Development of Ketill began in the fall of 2021. While revising the Ardenus Engine, I decided to create a new input system from scratch, as the original was clunky.
At the time, Ketill was simply the engine's `input` package. As the package grew in size, I realized it could be its own library. This would allow seamless integration with other projects in the future.

After extensive refactoring, the `input` package was moved to a separate repository and reborn as a [Maven](https://maven.apache.org/) project, similar to its ancestor.
I also decided to split the library into modules to improve modularity and maintainability. Although Maven supports modules, they are sadly a nuisance to work with.
However, while tinkering with [FabricMC](https://fabricmc.net/) one night, I was introduced to [Gradle](https://gradle.org/). I was delighted by its succinct build scripts and module system.
As such, the decision was made to use it for future projects.

# 👾 Why use Ketill?

By design, the definition of an `IoDevice` is separate from the code which implements it. The implementation of a device is provided by an `IoAdapter`.
Keeping the definition and implementation separate provides the following benefits:

   1. The code for defining a device is usually small.
   2. Definitions rarely require logic, just a statement of features.
   3. Definitions requires little or no unit testing when properly written.
   4. One device can be implemented with different adapters.
   5. Cross-platform support (e.g., using `GlfwXboxAdapter` on MacOS and `XInputXboxAdapter` on Windows).

Ketill also offers a litany of built-in components. Their purpose is to reduce the time required to integerate device I/O.
They also serve as examples for those who wish to create their own.

The following is a list of modules included with Ketill:

| Module    | Description                                                        |
|-----------|--------------------------------------------------------------------|
| `api`     | The base API for Ketill.                                           |
| `device`  | Provided definitions (`Keyboard`, `Mouse`, `XboxController`, etc.) |
| `adapter` | Provided implementations using GLFW, X-input, etc.                 |

# 🛠️ Building

**If you prefer not to build Ketill yourself:** pre-built JARs are available on [Jenkins](https://jenkins.ketill.io).<br>
Note that Jenkins may not always be available. In which case, you will need to build Ketill yourself.

Building Ketill is simple, and can be done by following the instructions below:

   1. Install [Git](https://git-scm.com/) if you have not done so already.
   2. Install the JDK. Builds of the OpenJDK can be found [here](https://adoptium.net/).
   3. Open a terminal of your choice and run the following:

```bash
git clone https://github.com/whirvis/ketill.git
cd ketill
chmod +x ./gradlew # unix only
./gradlew build

# install to local Maven repository if desired
./gradlew publishToMavenLocal
```

# 🖥️ Technologies

| Category          | Technologies                                                                                                                                                                                                                               |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Development       | [Eclipse Adoptium](https://adoptium.net/), [IntelliJ IDEA](https://www.jetbrains.com/idea/), and [Java 8](https://www.oracle.com/java/technologies/java8.html)                                                                             |
| Build System      | [Gradle](https://gradle.org/) and [Jenkins](https://www.jenkins.io/)                                                                                                                                                                       |
| Unit Testing      | [Equals Verifier](https://jqno.nl/equalsverifier/), [JetBrains Annotations](https://github.com/JetBrains/java-annotations), [JUnit 5](https://junit.org/junit5/), and [Mockito](https://site.mockito.org/)                                 |
| Quality Assurance | [Codacy](https://www.codacy.com/) and [Sonatype Lift](https://lift.sonatype.com/)                                                                                                                                                          |
| Adapters          | [GLFW 3](https://www.glfw.org/) (via [LWJGL 3](https://www.lwjgl.org/)), [JXInput](https://github.com/StrikerX3/JXInput), [Usb4Java](http://usb4java.org/quickstart/javax-usb.html), and [Hid4Java](https://github.com/gary-rowe/hid4java) |
| Libraries         | [GSON](https://github.com/google/gson) and [JOML](https://joml-ci.github.io/JOML/)                                                                                                                                                         |

# 💎 Special Thanks

  - My friend [Surhou](https://t.co/gt2mqvXKaA), for creating the project logo (which is still in progress, the one above is a placeholder).
  - My Uncle, for introducing me to [Mockito](https://site.mockito.org/) when I was seeking advice for writing unit tests.
  - My colleagues at university, for cheering me on the entire time I wrote this.