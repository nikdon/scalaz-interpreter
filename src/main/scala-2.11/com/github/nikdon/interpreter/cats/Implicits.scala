package com.github.nikdon.interpreter.cats

import cats.data.Coproduct
import cats.data.Xor.{Left, Right}
import cats.free.{Free, Inject}
import cats.~>

import scala.language.higherKinds


object Implicits {

  /**
    * Introduces the [[or]] function for interpreter composing
    *
    * @param nt The natural transformation (interpreter itself)
    * @tparam F The codomain of the natural transformation
    * @tparam H The domain of the natural transformation
    */
  implicit class NaturalTransformationOrOps[F[_], H[_]](private val nt: F ~> H) extends AnyVal {
    def or[G[_]](f: G ~> H): Coproduct[F, G, ?] ~> H =
      new (Coproduct[F, G, ?] ~> H) {
        def apply[A](c: Coproduct[F, G, A]): H[A] = c.run match {
          case Left(fa) => nt(fa)
          case Right(ga) => f(ga)
        }
      }
  }

  implicit def ImplicitInjectLift[F[_], G[_], A](fa: F[A])(implicit I: Inject[F, G]): Free[G, A] = Free liftF I.inj(fa)
}
