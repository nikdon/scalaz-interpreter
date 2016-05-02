package com.github.nikdon.interpreter.scalaz

import com.github.nikdon.interpreter.Interpreters
import shapeless.{HList, HNil, _}

import scala.language.higherKinds
import scalaz.Coproduct
import scalaz.{Inject, Free, \/-, -\/, ~>}


object Implicits {

  type Aux[L <: HList, Out0] = Interpreters[L] { type Out = Out0 }

  implicit def interpreters0[F[_], H[_]]: Aux[(F ~> H) :: HNil, F ~> H] =
    new Interpreters[(F ~> H) :: HNil] {
      type Out = F ~> H
      def apply(in: (F ~> H) :: HNil): F ~> H = in.head
    }

  implicit def interpreters1[F[_], G[_], H[_], T <: HList](implicit ti: Aux[T, G ~> H]): Aux[(F ~> H) :: T, Coproduct[F, G, ?] ~> H] =
    new Interpreters[(F ~> H) :: T] {
      type Out = Coproduct[F, G, ?] ~> H
      def apply(in: (F ~> H) :: T): Coproduct[F, G, ?] ~> H =
        new (Coproduct[F, G, ?] ~> H) {
          def apply[A](fa: Coproduct[F, G, A]): H[A] =
            fa.run.fold(in.head.apply, ti(in.tail).apply)
        }
    }

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
          case -\/(fa) => nt(fa)
          case \/-(ga) => f(ga)
        }
      }
  }

  implicit def ImplicitInjectLift[F[_], G[_], A](fa: F[A])(implicit I: Inject[F, G]): Free[G, A] = Free liftF I.inj(fa)
}
