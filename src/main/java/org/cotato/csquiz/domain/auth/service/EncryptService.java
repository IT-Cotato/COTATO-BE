package org.cotato.csquiz.domain.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EncryptService {

	private final AesBytesEncryptor aesBytesEncryptor;

	public String encryptPhoneNumber(final String rawPhoneNumber) {
		byte[] encrypt = aesBytesEncryptor.encrypt(rawPhoneNumber.getBytes(StandardCharsets.UTF_8));
		return byteArrayToString(encrypt);
	}

	public String decryptPhoneNumber(final String encryptedPhoneNumber) {
		byte[] bytes = stringToByteArray(encryptedPhoneNumber);
		byte[] decrypt = aesBytesEncryptor.decrypt(bytes);
		return new String(decrypt, StandardCharsets.UTF_8);
	}

	public String byteArrayToString(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public byte[] stringToByteArray(String byteString) {
		return Base64.getDecoder().decode(byteString);
	}
}
