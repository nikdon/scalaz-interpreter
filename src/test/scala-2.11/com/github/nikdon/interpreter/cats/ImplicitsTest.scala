package com.github.nikdon.interpreter.cats

import cats.data.Coproduct
import cats.free.{Free, Inject}
import cats.{Id, ~>}
import org.scalatest.{FlatSpec, Matchers}

import scala.language.higherKinds


sealed trait EnglishSyntax[A]
object CatsEnglish {
  case class Say(str: String) extends EnglishSyntax[Unit]
}

object EnglishInterpreter extends (EnglishSyntax ~> Id) {
  import CatsEnglish._
  def apply[A](fa: EnglishSyntax[A]): Id[A] = fa match {
    case Say(str) ⇒ println(str)
  }
}


sealed trait JapaneseSyntax[A]
object CatsJapanese {
  case class Say(str: String) extends JapaneseSyntax[Unit]
}

object JapaneseInterpreter extends (JapaneseSyntax ~> Id) {
  import CatsJapanese._
  def apply[A](fa: JapaneseSyntax[A]): Id[A] = fa match {
    case Say(str) ⇒ println(str)
  }
}


sealed trait CatsChineseSyntax[A]
object CatsChinese {
  case class Say(str: String) extends CatsChineseSyntax[Unit]
}

object ChineseInterpreter extends (CatsChineseSyntax ~> Id) {
  import CatsChinese._
  def apply[A](fa: CatsChineseSyntax[A]): Id[A] = fa match {
    case Say(str) ⇒ println(str)
  }
}


class ImplicitsTest extends FlatSpec with Matchers {

  "Implicit conversions" should "be used in the building of a programs" in {

    import Implicits._

    def program[F[_]](implicit
                      T1: Inject[EnglishSyntax, F],
                      T2: Inject[JapaneseSyntax, F],
                      T3: Inject[CatsChineseSyntax, F]) = for {
      _ ← CatsEnglish.Say("The one says: 'Hi!'")
      _ ← CatsJapanese.Say("Another one says: 'こんにちは'!")
      _ ← CatsChinese.Say("And somebody says: '嗨!'")
    } yield ()

    type App[A] = Coproduct[EnglishSyntax, JapaneseSyntax, A]
    type App1[A] = Coproduct[CatsChineseSyntax, App, A]
    val prg: Free[App1, Unit] = program[App1]

    val engJpInterpreter: App ~> Id = EnglishInterpreter or JapaneseInterpreter
    val chEngJpInterpreter: App1 ~> Id = ChineseInterpreter or engJpInterpreter

    prg.foldMap(chEngJpInterpreter)
  }
}
