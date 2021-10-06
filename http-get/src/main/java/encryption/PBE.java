package encryption;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @Author ws
 * @Date 2021/10/6 14:00
 */


public class PBE {
    private static String src = "security with PBE";

    public static void main(String[] args) {
        jdkPBE();
    }

    public static void jdkPBE() {

        Base64 base64 = new Base64();
        try {
            //初始化盐（扰码）
            SecureRandom random = new SecureRandom();
            byte[] salt = random.generateSeed(8);

            //口令与密钥
            String password = "CSDN";
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHMD5andEDS");
            Key key = factory.generateSecret(pbeKeySpec);

            //加密
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);//参数1.盐，参数2.迭代次数
            Cipher cipher = Cipher.getInstance("PBEWITHMD5andEDS");
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
            byte[] result = cipher.doFinal(src.getBytes());
            System.out.println("jdk pbe encrypt:" + base64.encodeToString(result));

            //解密
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpec);
            result = cipher.doFinal(result);
            System.out.println("jdk pbe decrypt:" + new String(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}