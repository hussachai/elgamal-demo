package controllers

import play.api._
import play.api.mvc._
import views.html
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import ext.play.Formats._
import algorithms.ElGamal
import algorithms.ElGamal._

object Application extends Controller {

  case class SetupKey(privateKey: BigInt, alpha: BigInt)

  case class KeyPair(publicKey: PublicKey, privateKey: BigInt)

  case class Message(text: String, key: Option[BigInt])

  val setupKeyForm: Form[SetupKey] = Form(
    mapping(
      "privateKey" -> of[BigInt],
      "alpha" -> of[BigInt]
    )(SetupKey.apply)(SetupKey.unapply)
  )

  val messageForm: Form[Message] = Form(
    mapping(
      "text" -> text,
      "key" -> optional(of[BigInt])
    )(Message.apply)(Message.unapply)
  )

  def index = Action {
    Ok(html.index())
  }

  def source = Action {
    Ok(html.source())
  }

  def setupKey = Action {
    Ok(html.setupKey(setupKeyForm));
  }

  def createPublicKey = Action { implicit request =>
    setupKeyForm.bindFromRequest.fold(
      errors => BadRequest(html.setupKey(errors)),
      setupKey => {
        val publicKey = ElGamal.createPublicKey(setupKey.privateKey, setupKey.alpha)
        Ok(html.publicKey(KeyPair(publicKey, setupKey.privateKey))).withSession(session
          + ("privateKey" -> setupKey.privateKey.toString)
          + ("publicKey.prime" -> publicKey.prime.toString)
          + ("publicKey.alpha" -> publicKey.alpha.toString)
          + ("publicKey.beta" -> publicKey.beta.toString)
        )
      }
    )
  }

  def prepareEncryption = Action { implicit request =>
     Ok(html.encryption(messageForm))
  }

  def encrypt = Action { implicit request =>
    messageForm.bindFromRequest.fold(
      errors => Ok("Error: message is required"),
      message => {
        request.session.get("privateKey").map { privateKey =>
          val prime = BigInt(request.session.get("publicKey.prime").get)
          val alpha = BigInt(request.session.get("publicKey.alpha").get)
          val beta = BigInt(request.session.get("publicKey.beta").get)
          val cipherText = message.text.encrypt(PublicKey(prime, alpha, beta), message.key)
          Ok(cipherText.toString).withSession(session + ("cipherText" -> cipherText.toString))
        }.getOrElse {
          Ok("Error: KeyPair not found")
        }
      }
    )
  }

  def prepareDecryption = Action { implicit request =>

      request.session.get("cipherText").map { cipherText =>
          val privateKey = request.session.get("privateKey").map{ BigInt(_) }
          Ok(html.decryption(messageForm.fill(Message(cipherText, privateKey)))
        )
      }.getOrElse{
        Ok(html.decryption(messageForm))
      }
  }

  def decrypt = Action { implicit request =>
    messageForm.bindFromRequest.fold(
      errors => Ok("Error: cipher is required"),
      message => {
        request.session.get("privateKey").map { pk =>
          val privateKey = message.key.getOrElse(BigInt(pk))
          val prime = BigInt(request.session.get("publicKey.prime").get)
          Ok(Ciphers(for{m <- message.text.split("\\r?\\n")} yield{
            val n = m.split(" ")
            if(n.size==2) Cipher(BigInt(n(0)),BigInt(n(1)))
            else Cipher(0, 0)
          }) decryptAsString(prime, privateKey))
        }.getOrElse {
          Ok("Error: KeyPair not found")
        }
      }
    )
  }

}