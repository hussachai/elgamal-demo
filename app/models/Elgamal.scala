package models

import java.security.SecureRandom

/**
 * Basic ElGamal encryption
 * @author Hussachai Puripunpinyo
 * Date: 2/27/14
 */
object Elgamal {

  private val sc = new SecureRandom()

  case class PublicKey(prime: BigInt, alpha: BigInt, beta: BigInt)
  case class Cipher(r: BigInt, t: BigInt)
  case class Ciphers(arr: Array[Cipher]){
    override def toString() = {
      val str = new StringBuilder
      for(c <- arr)  str ++= c.r.toString +=' ' ++= c.t.toString += '\n'
      str toString
    }
    def decryptAsString(publicKey: PublicKey, privateKey: BigInt) = {
      val str = new StringBuilder
      for(c <- arr) str += Elgamal.decrypt(c, publicKey, privateKey).toChar
      str toString
    }
  }

  def createPublicKey(privateKey: BigInt, alpha: BigInt = 2) = {
    val prime = BigInt.probablePrime(64, sc)
    val beta = alpha modPow(privateKey, prime)
    PublicKey(prime, alpha, beta)
  }

  def encrypt(m: BigInt, publicKey: PublicKey, k: BigInt) =
    Cipher(publicKey.alpha modPow(k, publicKey.prime) , publicKey.beta.modPow(k, publicKey.prime) * (m mod publicKey.prime))

  def decrypt(c: Cipher, publicKey: PublicKey, privateKey: BigInt) =
    (c.t * (c.r modPow(-privateKey, publicKey.prime))) mod publicKey.prime


  implicit def wrapperForString(str: String) = new StringExt(str)

  class StringExt(str: String){
    def encrypt(publicKey: PublicKey, k: Option[BigInt] = None) = {
      Ciphers(for{s <- str getBytes} yield Elgamal.encrypt(s.toInt, publicKey, k getOrElse(BigInt(64, sc))))
    }
  }

}

