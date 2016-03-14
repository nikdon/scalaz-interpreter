package com.github.nikdon.interpreter.cats

import cats.data.Coproduct
import cats.free.{Free, Inject}
import cats.~>
import com.github.nikdon.interpreter.Interpreters
import shapeless.{::, HList, HNil}

import scala.language.higherKinds


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

  implicit def ImplicitInjectLift[F[_], G[_], A](fa: F[A])(implicit I: Inject[F, G]): Free[G, A] = Free liftF I.inj(fa)

}
