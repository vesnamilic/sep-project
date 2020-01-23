package sep.project.crypto;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

	//Koristimo AES Counter Mode
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; //mora PKCS5 jer inace Cannot find any provider supporting AES/CBC/PKCS7Padding
	private static final byte[] KEY = "?D(G+KbPeShVkYp3s6v9y$B&E)H@McQf".getBytes(); //koristimo kljuc od 256 bitova
	private static final byte[] IvParameterVector = ")H+MbQeThWmZq4t7".getBytes(); //mora biti byte[16] duzina

	@Override
	public String convertToDatabaseColumn(String something) {
		if (something == null) {
			return "";
		}
		// do some encryption
		Key key = new SecretKeySpec(KEY, "AES");
		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IvParameterVector));
			return Base64.getEncoder().encodeToString(c.doFinal(something.getBytes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		// do some decryption
		if (dbData.equals("")) {
			return "";
		}
		Key key = new SecretKeySpec(KEY, "AES");
		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IvParameterVector));
			return new String(c.doFinal(Base64.getDecoder().decode(dbData)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}