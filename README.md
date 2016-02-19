# scalaz-interperter

[![Build Status](https://travis-ci.org/nikdon/scalaz-interpreter.svg?branch=master)](https://travis-ci.org/nikdon/scalaz-interpreter)
[![](https://jitpack.io/v/nikdon/scalaz-interpreter.svg)](https://jitpack.io/#nikdon/scalaz-interpreter)
[![codecov.io](https://codecov.io/github/nikdon/scalaz-interpreter/coverage.svg?branch=master)](https://codecov.io/github/nikdon/scalaz-interpreter?branch=master)

Interpreter implementation based on [**scalaz**](https://github.com/scalaz/scalaz) and [**cats**](https://github.com/typelevel/cats) inspired by Rúnar Bjarnason [Compositional Application Architecture With Reasonably Priced Monads](https://www.parleys.com/play/53a7d2c3e4b0543940d9e538/).

Note that current stable version of **scalaz** 7.2.0 doesn't prevent stack overflow. For more information on this look at the [PR](https://github.com/scalaz/scalaz/pull/1079).

Code based on **scalaz** and **cats** is almost the same except dependencies. Full examples are in tests.

Programs for interpretation in general look like this one:

```scala
class ImplicitsTest extends FlatSpec with Matchers {

  "Implicit conversions" should "be used in the building of a programs" in {

    import com.github.nikdon.interpreter.scalaz.Implicits._
    // import com.github.nikdon.interpreter.cats.Implicits._

    def program[F[_]](implicit
                      T1: Inject[EnglishSyntax, F],
                      T2: Inject[JapaneseSyntax, F],
                      T3: Inject[ChineseSyntax, F]) = for {
      _ ← English.Say("The one says: 'Hi!'")
      _ ← Japanese.Say("Another one says: 'こんにちは'!")
      _ ← Chinese.Say("And somebody says: '嗨!'")
    } yield ()

    type App[A] = Coproduct[EnglishSyntax, JapaneseSyntax, A]
    type App1[A] = Coproduct[ChineseSyntax, App, A]
    val prg: Free[App1, Unit] = program[App1]

    val engJpInterpreter: App ~> Id = EnglishInterpreter or JapaneseInterpreter
    val chEngJpInterpreter: App1 ~> Id = ChineseInterpreter or engJpInterpreter

    prg.foldMap(chEngJpInterpreter)
  }
}
```

## Getting Started

```scala
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.nikdon" % "scalaz-interpreter" % "2e3e3cb6c9"
```
