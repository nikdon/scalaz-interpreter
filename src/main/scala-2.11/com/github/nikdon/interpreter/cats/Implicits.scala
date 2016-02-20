package com.github.nikdon.interpreter.cats

import cats.free.{Free, Inject}

import scala.language.higherKinds


object Implicits {

  implicit def ImplicitInjectLift[F[_], G[_], A](fa: F[A])(implicit I: Inject[F, G]): Free[G, A] = Free liftF I.inj(fa)

}
