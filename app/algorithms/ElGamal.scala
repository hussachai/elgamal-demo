package algorithms

import java.security.SecureRandom

/**
 * Simple ElGamal encryption
 * @author Hussachai Puripunpinyo
 * Date: 2/27/14
 */
object ElGamal {

  private val sc = new SecureRandom()

  case class PublicKey(prime: BigInt, alpha: BigInt, beta: BigInt)
  case class Cipher(r: BigInt, t: BigInt){
    override def toString() = {
      "("+ r.toString + "," + t.toString +")"
    }
  }
  case class Ciphers(seq: Seq[Cipher]){
    override def toString() = {
      val str = new StringBuilder
      for(c <- seq)  str ++= c.r.toString +=' ' ++= c.t.toString += '\n'
      str toString
    }
    def decryptAsString(prime: BigInt, privateKey: BigInt): String = {
      val str = new StringBuilder
      for(c <- seq) str += ElGamal.decrypt(c, prime, privateKey).toChar
      str toString
    }
  }

  def createPublicKey(privateKey: BigInt, alpha: BigInt = 2):PublicKey = {
    val prime = BigInt.probablePrime(64, sc)
    val beta = alpha modPow(privateKey, prime)
    PublicKey(prime, alpha, beta)
  }

  def encrypt(m: BigInt, publicKey: PublicKey, k: BigInt): Cipher =
    Cipher(publicKey.alpha modPow(k, publicKey.prime) ,
      publicKey.beta.modPow(k, publicKey.prime) * (m mod publicKey.prime))

  def decrypt(c: Cipher, prime: BigInt, privateKey: BigInt): BigInt =
    (c.t * (c.r modPow(-privateKey, prime))) mod prime


  implicit def wrapperForString(str: String) = new StringExt(str)

  class StringExt(str: String){
    def encrypt(publicKey: PublicKey, k: Option[BigInt] = None): Ciphers = {
      Ciphers(for{s <- str getBytes} yield
        ElGamal.encrypt(s.toInt, publicKey, k getOrElse(BigInt(64, sc))))
    }
  }

}

