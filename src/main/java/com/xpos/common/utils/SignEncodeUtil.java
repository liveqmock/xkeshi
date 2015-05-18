package com.xpos.common.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class SignEncodeUtil {
    private static final String TAG = "code";

    private static final String ALGORITHM = "RSA";
    /**
     * desc:对数据进行加签
     * <p>创建人：聂旭阳 , 2013-9-30 下午5:29:23</p>
     * @param voucher
     * @return
     */
    public static String umpaySignData(String voucher, String keyPath) {
        try {
            File file = new File(keyPath); // keyfile
                                                                  // key文件的地址
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] tmpbuf = new byte[1024];
            int count = 0;
            while ((count = in.read(tmpbuf)) != -1) {
                bout.write(tmpbuf, 0, count);
                tmpbuf = new byte[1024];
            }
            in.close();
            // 把读的输入流转变成自己想要的privatekey
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bout.toByteArray());
            RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(privateKeySpec);
//            System.out.println("密钥" + bytesToHexStr(privateKey.getEncoded()));
//            System.out.println("密钥" + privateKey.toString());

            // 这样就可以使用privatekey对自己的文件进行加密了
            // 进行加密
            Signature dsa = Signature.getInstance("SHA1withRSA"); // 采用SHA1withRSA加密
            dsa.initSign(privateKey);
            dsa.update(voucher.getBytes()); // voucher需要加密的String必须变成byte类型的
            byte[] sig = dsa.sign();
            String byRSA = bytesToHexStr(sig); // 加密后的byte类型变成十六进制的String
            return byRSA;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return voucher;
    }

    public static String shengPaySignData(String voucher, String keyPath) {
        try {
            //keyfile key文件的地址
            File file = new File(keyPath); // keyfile
                                                                  // key文件的地址
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] tmpbuf = new byte[1024];
            int count = 0;
            while ((count = in.read(tmpbuf)) != -1) {
                bout.write(tmpbuf, 0, count);
                tmpbuf = new byte[1024];
            }
            in.close();
            // 把读的输入流转变成自己想要的privatekey
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bout.toByteArray());
            RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(privateKeySpec);
//            System.out.println("密钥" + bytesToHexStr(privateKey.getEncoded()));
//            System.out.println("密钥" + privateKey.toString());

            // 这样就可以使用privatekey对自己的文件进行加密了
            // 进行加密
            Signature dsa = Signature.getInstance("SHA1withRSA"); // 采用SHA1withRSA加密
            dsa.initSign(privateKey);
            dsa.update(voucher.getBytes()); // voucher需要加密的String必须变成byte类型的
            byte[] sig = dsa.sign();
            String byRSA = Base64.encodeBase64String(sig);
            return byRSA;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return voucher;
    }
    
    // 将 BASE64 编码的字符串 s 进行解码
    public static byte[] getFromBASE64(byte []s) {
        if (s == null)
            return null;
        Base64 decoder = new Base64();
        try {
            byte[] b = decoder.decode(s);
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    // 11E4679B4423892EF6D7226A6B5C21D2809A9909144621E22F54E52113FFA76F5D708D85D07A87ED78C8762907076249ABAA27D16A94D5954585EA62F32ACCC4CBDCDC7F7CDD6F691D251A2DE621EE86AF20123478CF2BB475488906B0CEB02F82077B6786B3F3A4E9023B3383141735BBCDF9B6A779C0F59B75DD945D043BDC
    // 11E4679B4423892EF6D7226A6B5C21D2809A9909144621E22F54E52113FFA76F5D708D85D07A87ED78C8762907076249ABAA27D16A94D5954585EA62F32ACCC4CBDCDC7F7CDD6F691D251A2DE621EE86AF20123478CF2BB475488906B0CEB02F82077B6786B3F3A4E9023B3383141735BBCDF9B6A779C0F59B75DD945D043BDC
    // 11E4679B4423892EF6D7226A6B5C21D2809A9909144621E22F54E52113FFA76F5D708D85D07A87ED78C8762907076249ABAA27D16A94D5954585EA62F32ACCC4CBDCDC7F7CDD6F691D251A2DE621EE86AF20123478CF2BB475488906B0CEB02F82077B6786B3F3A4E9023B3383141735BBCDF9B6A779C0F59B75DD945D043BDC
    public static boolean shengPayVerify(String info, String data, String file) {

        try {
            CertificateFactory certificatefactory=CertificateFactory.getInstance("X.509");
            FileInputStream bais = new FileInputStream(file);
            X509Certificate Cert = (X509Certificate)certificatefactory.generateCertificate(bais);
            PublicKey publicKey = Cert.getPublicKey();
            Base64 decoder = new Base64();
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(info.getBytes());
           
            // 验证签名是否正常
            boolean verify = signature.verify(decoder.decode(data));
            System.out.println("验签结果"+verify);
            return verify;
        } catch (InvalidKeyException | NoSuchAlgorithmException | IOException | SignatureException | CertificateException e) {
            e.printStackTrace();
        }
        return false;
    }

   
    private static final String dsf = "MIICXQIBAAKBgQCtvItiXsqWnJjnGqhkRD23tOrA9YaC3ynG/xPPv5EsNeHhHr4zAGfaZyFzzBc+XT24WsqUzipMBk6BGxnRXtUi9GEoe6PraQciEFXAEMa/CCRB0r7rdFT/u64uforeT1FCSA0lhypau4rxNcdwPEJqN5J+MykTjKTEPcvAsTWsFwIDAQABAoGAHScXKHGJgw5R5e2mNfTxekMEZU6NvKYfx4GD3Idjn8yG05SqC7rUsmQ9y8WCXPeeZLHvblrN5CXmxGk8wtIr50PMEY+4CZ33Nl4/PdFmOM083KM6sGMWO28kTX4KMyqmAnuAnzCRezlHGWqH5Xn60gbQHRZuKIEBiYYfBP+evkkCQQDcarTNG1PMXphzPUKvLMcrQyvBs3R/KVLQcPu4ny29kHjjZ2ckAW7/iE6ztnFZ6Jy6ox0r6Geqt6mrKTvn5crFAkEAycinUdh6a381nK+L0pjeki88qUJRsW/20j5TAbVf9RqkEJuIBrnuWOdb3VwSC7X24wKz9YIcRSQBvfHN8Af5KwJBAMo+xuMkXgG6Epw668McjSvvGGlFpnE/k5Na+D3xIOE9fQ77xDHPdu/VPJG9p8hdneHK5Wtydhy5JV++GA+yVBkCQQCx42MelGnYOt1YtKnfj0UoOtyPmxfKBZri3m7vIqblvgbFXVgeFew6FDy4eWKvUEvG9asQ1RN3ILcobPPQmDbhAkAFNR+H+HALnJ8n5SZ2jKH3ImAh0QzY4w90Jtl1DO8dy/O85n/R9A9EG4Ktn1p3kfRMTNI1C7yQs6E7zwZki";


    public static String enCoder(String info, String pubkey) {
        String signature = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(hexStrToBytes(pubkey));
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] cipherText = cipher.doFinal(info.getBytes("GBK"));
            signature = bytesToHexStr(cipherText);
        } catch (java.lang.Exception e) {
        }
        return signature;
    }

    /**
     * md5加密文件
     * 
     * @param fileName
     * @return
     */
    public static String md5sum(String fileName) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            fis = new FileInputStream(fileName);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            return bytesToHexStr(md5.digest());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Transform the specified byte into a Hex String form.
     */
    public static final String bytesToHexStr(byte[] bcd) {
        StringBuffer s = new StringBuffer(bcd.length * 2);
        for (int i = 0; i < bcd.length; i++) {
            s.append(bcdLookup[(bcd[i] >>> 4) & 0x0f]);
            s.append(bcdLookup[bcd[i] & 0x0f]);
        }
        return s.toString();
    }

    /**
     * MD5加密字符串。32位
     * 
     * @param inStr
     * @return
     */
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");

            char[] charArray = str.toCharArray();
            byte[] byteArray = new byte[charArray.length];
            for (int i = 0; i < charArray.length; i++)
                byteArray[i] = (byte)charArray[i];

            byte[] md5Bytes = md5.digest(byteArray);
            return bytesToHexStr(md5Bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Transform the specified Hex String into a byte array.
     */
    public static final byte[] hexStrToBytes(String s) {
        byte[] bytes;
        bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private static final char[] bcdLookup = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    public static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;
    }

}
