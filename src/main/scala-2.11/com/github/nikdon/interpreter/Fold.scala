package com.github.nikdon.interpreter

import shapeless.{DepFn1, HList}


trait Interpreters[L <: HList] extends DepFn1[L]


object Fold {
  def apply[L <: HList](l: L)(implicit is: Interpreters[L]): is.Out = is(l)
}
