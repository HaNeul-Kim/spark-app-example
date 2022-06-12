package com.tistory.hskimsky

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

/**
 * hskimsky <=> AuuoMxYR2+zplOO++aY7ww==
 *
 * @author Haneul, Kim
 * @version 3.0.0
 * @since 2022-06-08
 */
class AES256 {
  val alg: String = "AES/CBC/PKCS5Padding"
  val key: String = "01234567890123456789012345678901"
  val iv: String = key.substring(0, 16)

  val cipher: Cipher = Cipher.getInstance(alg)
  val keySpec: SecretKeySpec = new SecretKeySpec(key.getBytes(), "AES")
  val ivParamSpec: IvParameterSpec = new IvParameterSpec(iv.getBytes())

  def encrypt(text: String): String = {
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec)
    Base64.getEncoder.encodeToString(cipher.doFinal(text.getBytes("UTF-8")))
  }

  def decrypt(cipherText: String): String = {
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec)
    new String(cipher.doFinal(Base64.getDecoder.decode(cipherText)), "UTF-8")
  }
}
