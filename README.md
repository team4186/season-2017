# Code for season 2017

## How to compile

If you are compiling using CLI, you *must* set JAVA_HOME variable to your JDK path otherwise a strange error will throw during compilation.

Having it set it's just a matter of:

```
./gradlew build deploy
```

We are using [Open-RIO/GradleRIO](https://github.com/Open-RIO/GradleRIO) for our build system.