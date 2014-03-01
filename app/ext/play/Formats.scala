package ext.play

import play.api.data.format.Formatter
import play.api.data.format.Formats._
import play.api.data.FormError

/**
 * Created with IntelliJ IDEA.
 * User: hussachai
 * Date: 2/28/14
 */
object Formats {

  /**
   * Helper for formatters binders
   * @param parse Function parsing a String value into a T value, throwing an exception in case of failure
   * @param error Error to set in case of parsing failure
   * @param key Key name of the field to parse
   * @param data Field data
   */
  private def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
    stringFormat.bind(key, data).right.flatMap { s =>
      scala.util.control.Exception.allCatch[T]
        .either(parse(s))
        .left.map(e => Seq(FormError(key, errMsg, errArgs)))
    }
  }

  implicit def bigIntFormat: Formatter[BigInt] = new Formatter[BigInt] {

    override val format = Some(("format.numeric", Nil))

    def bind(key: String, data: Map[String, String]) =
      parsing(BigInt(_), "error.number", Nil)(key, data)

    def unbind(key: String, value: BigInt) = Map(key -> value.toString)
  }


}
