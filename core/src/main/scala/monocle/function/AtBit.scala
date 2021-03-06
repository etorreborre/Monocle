package monocle.function

import monocle.Lens
import monocle.internal.Bits
import scala.annotation.implicitNotFound

@implicitNotFound("Could not find an instance of AtBit[${S}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait AtBit[S] {

  def atBit(index: Int): Lens[S, Boolean]

}

object AtBit extends AtBitFunctions

trait AtBitFunctions {

  def atBit[S](index: Int)(implicit ev: AtBit[S]): Lens[S, Boolean] = ev.atBit(index)

  def bitsAtBit[S: Bits]: AtBit[S] = new AtBit[S] {
    def atBit(index: Int): Lens[S, Boolean] = {
      val n = normalizeIndex(Bits[S].bitSize, index)
      Lens(Bits[S].testBit(_: S, n))(a => s => Bits[S].updateBit(a)(s, n))
    }
  }

  // map i to a value in base, negative value means that it is indexed from the end
  private def normalizeIndex(base: Int, i: Int): Int = {
    val modulo = i % base
    if (modulo >= 0) modulo else modulo + base
  }

}
