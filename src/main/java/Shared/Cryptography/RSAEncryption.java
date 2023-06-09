package Shared.Cryptography;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Base64;

public class RSAEncryption {
    private KeyPairGenerator keyPairGenerator;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RSAEncryption(){
        try {
            this.keyPairGenerator =  KeyPairGenerator.getInstance("RSA");
            SecureRandom secureRandom = new SecureRandom();
            this.keyPairGenerator.initialize(1024, secureRandom);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeyPair pair = keyPairGenerator.generateKeyPair();
        this.publicKey = pair.getPublic();
        this.privateKey = pair.getPrivate();
    }

    public PublicKey getPublicKey(){
        return this.publicKey;
    }

    public String encrypt(String plaintext, PublicKey publicKey){
        String encryption = "";
        try {
            Cipher encryptionCipher = Cipher.getInstance("RSA");
            encryptionCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedMessage = encryptionCipher.doFinal(plaintext.getBytes());
            encryption = Base64.getEncoder().encodeToString(encryptedMessage);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
            e.printStackTrace();
        }
        return encryption;
    }

    public String decrypt(String ciphertext){
        String decryption = "";
        try {
            Cipher decryptionCipher = Cipher.getInstance("RSA");
            decryptionCipher.init(Cipher.DECRYPT_MODE, this.privateKey);
            byte[] decryptedMessage = decryptionCipher.doFinal(Base64.getDecoder().decode(ciphertext.getBytes()));
            decryption = new String(decryptedMessage);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
            e.printStackTrace();
        }
        return decryption;
    }
}
