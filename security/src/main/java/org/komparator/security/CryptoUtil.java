package org.komparator.security;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.crypto.*;
import java.util.*;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;



public class CryptoUtil {

    // TODO add security helper methods
	public String asymCipher(byte[] plainBytes, PublicKey publicKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
		
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherBytes = cipher.doFinal(plainBytes);
		
		String resultado = printBase64Binary(cipherBytes);
		
		return resultado;
	}
	
	public byte[] asymDecipher(String cipherText, PrivateKey privateKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{
		
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey); 
		
		byte[] plainBytes = parseBase64Binary(cipherText);
		
		byte[] resultado = cipher.doFinal(plainBytes); 
		
		return resultado;
	}
	
	public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, PublicKey publicKey)
			throws Exception {

		// verify the signature with the public key
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initVerify(publicKey);
		sig.update(bytes);
		try {
			return sig.verify(cipherDigest);
		} catch (SignatureException se) {
			System.err.println("Caught exception while verifying signature " + se);
			return false;
		}
	}
	
	public static byte[] makeDigitalSignature(byte[] bytes, PrivateKey privateKey) throws Exception {

		// get a signature object using the SHA-1 and RSA combo
		// and sign the plain-text with the private key
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initSign(privateKey);
		sig.update(bytes);
		byte[] signature = sig.sign();

		return signature;
	}
	
	public static KeyStore readKeystoreFile(String keyStoreFilePath, char[] keyStorePassword) throws Exception {
		FileInputStream fis;
		try {
			fis = new FileInputStream(keyStoreFilePath);
		} catch (FileNotFoundException e) {
			System.err.println("Keystore file <" + keyStoreFilePath + "> not found.");
			return null;
		}
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(fis, keyStorePassword);
		return keystore;
	}
	
	public static PrivateKey getPrivateKeyFromKeystore(String keyStoreFilePath, char[] keyStorePassword,
			String keyAlias, char[] keyPassword) throws Exception {

		KeyStore keystore = readKeystoreFile(keyStoreFilePath, keyStorePassword);
		PrivateKey key = (PrivateKey) keystore.getKey(keyAlias, keyPassword);

		return key;
	}
	
	public PublicKey getPublicKeyFromCertificate(Certificate certificate) {
		return certificate.getPublicKey();
	}
	
	public static Certificate readCertificateFile(String certificateFilePath) throws Exception {
		FileInputStream fis;

		try {
			fis = new FileInputStream(certificateFilePath);
		} catch (FileNotFoundException e) {
			System.err.println("Certificate file <" + certificateFilePath + "> not fount.");
			return null;
		}
		BufferedInputStream bis = new BufferedInputStream(fis);

		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		if (bis.available() > 0) {
			Certificate cert = cf.generateCertificate(bis);
			return cert;
			// It is possible to print the content of the certificate file:
			// System.out.println(cert.toString());
		}
		bis.close();
		fis.close();
		return null;
	}
	
	
}
