package com.github.nikdon.interpreter

import shapeless.{::, DepFn1, Generic, HList, HNil}
import shapeless.ops.hlist.Tupler


trait Interpreters[L <: HList] extends DepFn1[L]


object Fold {
  def apply[L <: HList](l: L)(implicit is: Interpreters[L]): Interpreters[L]#Out = is(l)

  def tupleN[P <: Product, In <: HList, Out <: HList](p: P)
                                                     (implicit
                                                      gen: Generic.Aux[P, In],
                                                      ev: IsHlistOfH[In, Out],
                                                      is: Interpreters[Out],
                                                      tupler: Tupler[Out]): Interpreters[Out]#Out =
    apply(ev.hsequence(gen.to(p)))
}


trait IsHlistOfH[In <: HList, Out <: HList] {
  def hsequence(l: In): Out
}


object IsHlistOfH {
  implicit object HNilListOfH extends IsHlistOfH[HNil, HNil] {
    override def hsequence(l: HNil): HNil = l
  }

  implicit def hconsIsListOfH[H, In <: HList, Out <: HList]
    (implicit ev: IsHlistOfH[In, Out]): IsHlistOfH[H :: In, H :: Out] =
      new IsHlistOfH[H :: In, H :: Out] {
        override def hsequence(l: H :: In): H :: Out = l.head :: ev.hsequence(l.tail)
      }
}
