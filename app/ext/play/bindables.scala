package ext.play

import play.api.mvc.QueryStringBindable.Parsing

/**
 * Created with IntelliJ IDEA.
 * User: hussachai
 * Date: 3/1/14
 */
package bindables {

  object bindableBigInt extends Parsing[BigInt](
    BigInt(_), _.toString, (key: String, e: Exception) => "Cannot parse parameter %s as BigInt: %s".format(key, e.getMessage)
  )

}
