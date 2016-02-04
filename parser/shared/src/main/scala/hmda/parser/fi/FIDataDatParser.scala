package hmda.parser.fi

import hmda.model.fi.FIData
import hmda.parser.fi.lar.LarDatParser
import hmda.parser.fi.ts.TsDatParser
import scala.scalajs.js.annotation.JSExport

@JSExport
class FIDataDatParser extends FIDataParser[String] {
  @JSExport
  override def read(input: Iterable[String]): FIData = {
    val tsLine = input.head
    val ts = TsDatParser(tsLine)
    val lars = input.tail.iterator.map(l => LarDatParser(l))
    FIData(ts, lars)
  }
}
