package views.helper

import views.html.helper.{FieldElements, FieldConstructor}
import views.html.helper.twitterBootstrap3.twitterBootstrapFieldConstructor

/**
 * Created by hussachai on 3/9/14.
 */
package object twitterBootstrap3 {
   /**
   * Twitter bootstrap input structure.
   *
   * {{{
   * <dl>
   *   <dt><label for="username"></dt>
   *   <dd><input type="text" name="username" id="username"></dd>
   *   <dd class="error">This field is required!</dd>
   *   <dd class="info">Required field.</dd>
   * </dl>
   * }}}
   */
  implicit val twitterBootstrapField = new FieldConstructor {
    def apply(elts: FieldElements) = twitterBootstrapFieldConstructor(elts)
  }
}
