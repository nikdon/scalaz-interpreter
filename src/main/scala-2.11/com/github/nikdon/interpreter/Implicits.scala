package com.github.nikdon.interpreter

import scala.language.higherKinds
import scalaz._


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
          case -\/(fa) => nt(fa)
          case \/-(ga) => f(ga)
        }
      }
  }

  implicit def ImplicitInjectLift[F[_], G[_], A](fa: F[A])(implicit I: Inject[F, G]): Free[G, A] = Free liftF I.inj(fa)
}
