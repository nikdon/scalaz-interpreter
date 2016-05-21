package com.github.nikdon.interpreter.cats

import cats.data.Coproduct
import cats.free.{Free, Inject}
import cats.{Id, ~>}
import com.github.nikdon.interpreter.Fold
import org.scalatest.{FlatSpec, Matchers}

import scala.language.higherKinds


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

  import Implicits._

  def program[F[_]](implicit
                    T1: Inject[EnglishSyntax, F],
                    T2: Inject[JapaneseSyntax, F],
                    T3: Inject[ChineseSyntax, F]): Free[F, Unit] = for {
    _ ← English.Say("The one says: 'Hi!'")
    _ ← Japanese.Say("Another one says: 'こんにちは'!")
    _ ← Chinese.Say("And somebody says: '嗨!'")
  } yield ()

  type App[A] = Coproduct[EnglishSyntax, JapaneseSyntax, A]
  type App1[A] = Coproduct[ChineseSyntax, App, A]
  val prg: Free[App1, Unit] = program[App1]


  "Cats implicit conversions" should "be used in the building of a programs" in {

    val engJpInterpreter: App ~> Id = EnglishInterpreter or JapaneseInterpreter
    val chEngJpInterpreter: App1 ~> Id = ChineseInterpreter or engJpInterpreter

    prg.foldMap(chEngJpInterpreter)
  }

  it should "be used in the building of programs with HList" in {

    import shapeless.HNil

    val interpreter: App1 ~> Id = Fold(
      (ChineseInterpreter: ChineseSyntax ~> Id) ::
        (EnglishInterpreter: EnglishSyntax ~> Id) ::
        (JapaneseInterpreter: JapaneseSyntax ~> Id) :: HNil
    )

    prg.foldMap(interpreter)
  }

  it should "be used in the building of programs with shapeless conversions" in {

    val interpreter: App1 ~> Id = Fold.tupleN((
      ChineseInterpreter: ChineseSyntax ~> Id,
      EnglishInterpreter: EnglishSyntax ~> Id,
      JapaneseInterpreter: JapaneseSyntax ~> Id
    ))

    prg.foldMap(interpreter)
  }
}
