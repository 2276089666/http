package encryption;

/**
 * @Author ws
 * @Date 2021/10/6 14:07
 */
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

public class DH {
    private static String src="security with DH";
    public static void main(String[] args){
        jdkDH();
    }

    public static void jdkDH(){

        Base64 base64 = new Base64();
        try {
            //初始化发送方密钥
            KeyPairGenerator senderKeyPairGenerator=KeyPairGenerator.getInstance("DH");
            senderKeyPairGenerator.initialize(512);
            KeyPair senderKeyPair=senderKeyPairGenerator.generateKeyPair();
            byte[] senderPublicKeyEnc=senderKeyPair.getPublic().getEncoded();

            //初始化接收方密钥
            KeyFactory receiverKeyFactory= KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(senderPublicKeyEnc);
            PublicKey receiverPublicKey=receiverKeyFactory.generatePublic(x509EncodedKeySpec);
            DHParameterSpec dhParameterSpec=((DHPublicKey)receiverPublicKey).getParams();
            KeyPairGenerator receiverKeyPairGenerator=KeyPairGenerator.getInstance("DH");
            receiverKeyPairGenerator.initialize(dhParameterSpec);
            KeyPair receiverKeyPair=receiverKeyPairGenerator.generateKeyPair();
            PrivateKey receiverPrivateKey=receiverKeyPair.getPrivate();
            byte[] receiverPublicKeyEnc =receiverKeyPair.getPublic().getEncoded();

            //密钥构建
            KeyAgreement receiverKeyAgreement = KeyAgreement.getInstance("DH");
            receiverKeyAgreement.init(receiverPrivateKey);
            receiverKeyAgreement.doPhase(receiverPublicKey, true);
            SecretKey receiverDesKey=receiverKeyAgreement.generateSecret("DES");

            KeyFactory senderKeyFactory=KeyFactory.getInstance("DH");
            x509EncodedKeySpec=new X509EncodedKeySpec(receiverPublicKeyEnc);
            PublicKey senderPublicKey=senderKeyFactory.generatePublic(x509EncodedKeySpec);
            KeyAgreement senderKeyAgreement=KeyAgreement.getInstance("DH");
            senderKeyAgreement.init(senderKeyPair.getPrivate());
            senderKeyAgreement.doPhase(senderPublicKey, true);
            SecretKey senderDesKey=senderKeyAgreement.generateSecret("DES");
            if(Objects.equals(receiverDesKey,senderDesKey)){
                System.out.println("双方密钥相同");
            }

            //加密
            Cipher cipher= Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE,senderDesKey);
            byte[] result=cipher.doFinal(src.getBytes());
            System.out.println("jdk dh encrypt:"+ base64.encodeToString(result));

            //解密
            cipher=Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE,receiverDesKey);
            result=cipher.doFinal(result);
            System.out.println("jdk dh encrypt:"+new String(result));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}