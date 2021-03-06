package br.com.nexfe.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtilCrypt {

	public static String criptografia(String original) {
		String senha = null;
		MessageDigest algoritmo;
		byte messageDigest[];
		StringBuilder hexString;
		try {
			// algoritmo =MessageDigest.getInstance("SHA-256");// 64 letras
			algoritmo = MessageDigest.getInstance("MD5"); // 32 letras
			messageDigest = algoritmo.digest(original.getBytes("UTF-8"));
			hexString = new StringBuilder();
			for (byte b : messageDigest) {
				hexString.append(String.format("%02X", 0xFF & b));
			}
			senha = hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//System.out.println("Senha normal: " + original + " - Senha criptografada: " + senha);
		return senha;
	}

}
