# scalaz-interperter

[![Build Status](https://travis-ci.org/nikdon/scalaz-interpreter.svg?branch=master)](https://travis-ci.org/nikdon/scalaz-interpreter)

Interpreter implementation based on **scalaz** inspired by Rúnar Bjarnason [Compositional Application Architecture With Reasonably Priced Monads](https://www.parleys.com/play/53a7d2c3e4b0543940d9e538/).

Refer that current version of **scalaz** 7.2.0 doesn't prevent stackoverflow. See [PR](https://github.com/scalaz/scalaz/pull/1079)

See tests for usage example:

```scala
package com.github.nikdon.interpreter

import org.scalatest.prop.{Checkers, PropertyChecks}
import org.scalatest.{FlatSpec, Matchers}

import scala.language.higherKinds
import scalaz.Scalaz._
import scalaz._


sealed trait EnglishSyntax[A]
object English {
  case class Say(str: String) extends EnglishSyntax[Unit]
}

object EnglishInterpreter extends (EnglishSyntax ~> Id) {
  import English._
  def apply[A](fa: EnglishSyntax[A]): Id[A] = fa match {
    case Say(str) ⇒ println(str)
  }
}


sealed trait JapaneseSyntax[A]
object Japanese {
  case class Say(str: String) extends JapaneseSyntax[Unit]
}

object JapaneseInterpreter extends (JapaneseSyntax ~> Id) {
  import Japanese._
  def apply[A](fa: JapaneseSyntax[A]): Id[A] = fa match {
    case Say(str) ⇒ println(str)
  }
}


sealed trait ChineseSyntax[A]
object Chinese {
  case class Say(str: String) extends ChineseSyntax[Unit]
}

object ChineseInterpreter extends (ChineseSyntax ~> Id) {
  import Chinese._
  def apply[A](fa: ChineseSyntax[A]): Id[A] = fa match {
    case Say(str) ⇒ println(str)
  }
}


class ImplicitsTest extends FlatSpec with Matchers {

  "Implicit conversions" should "be used in the building of a programs" in {

    import com.github.nikdon.interpreter.Implicits._

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