package org.komparator.security;

import java.io.*;
import java.security.*;
import javax.crypto.*;
import java.util.*;



public class CryptoUtil {

    // TODO add security helper methods
	public byte[] asymCipher(byte[] plainBytes, PublicKey publicKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherBytes = cipher.doFinal(plainBytes);
		
		//PARA IMPRIMIR ISTO USAR printBase64Binary EM CASO DE TESTE
		
		return cipherBytes;
	}
	
	public byte[] asymDecipher(byte[] cipherBytes, PrivateKey privateKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey); 
		byte[] plainBytes = cipher.doFinal(cipherBytes); 
		
		//PARA IMPRIMIR ISTO USAR parseBase64Binary EM CASO DE TESTE
		
		return plainBytes;

	}
}
