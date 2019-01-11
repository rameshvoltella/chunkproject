package com.ramzi.chunkproject;
import java.io.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.*;
/**
 * Created by oliveboard on 11/1/19.
 *
 * @auther Ramesh M Nair
 */
public class Encryption {
    public static void encryptFile(String fileName, String keyName)
    {
        String original = fileName ;
        String encrypted = "encrypted_" + fileName ;
        Cipher encrypt;
        byte[] initialization_vector = { 22, 33, 11, 44, 55, 99, 66, 77 };
        try
        {
            SecretKey secret_key = readKey(keyName, "DES");
            AlgorithmParameterSpec alogrithm_specs = new IvParameterSpec(initialization_vector);
            encrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
            encrypt.init(Cipher.ENCRYPT_MODE, secret_key, alogrithm_specs);
            encrypt(new FileInputStream(original), new FileOutputStream(encrypted),encrypt);
            System.out.println("End of Encryption procedure!");
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }
    private static void decryptFile(String fileName, String keyName)
    {
        String original = "decrypted_" + fileName;
        String encrypted = fileName ;
        Cipher decrypt;
        byte[] initialization_vector = { 22, 33, 11, 44, 55, 99, 66, 77 };
        try
        {
            SecretKey secret_key = readKey(keyName, "DES");
            AlgorithmParameterSpec alogrithm_specs = new IvParameterSpec(initialization_vector);

            decrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
            decrypt.init(Cipher.DECRYPT_MODE, secret_key, alogrithm_specs);
            decrypt(new FileInputStream(encrypted), new FileOutputStream(original),decrypt);
            System.out.println("End of Decryption procedure!");
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }

    private static void encrypt(InputStream input, OutputStream output,Cipher encrypt) throws IOException {
        output = new CipherOutputStream(output, encrypt);
        writeBytes(input, output);
    }

    private static void decrypt(InputStream input, OutputStream output,Cipher decrypt) throws IOException {

        input = new CipherInputStream(input, decrypt);
        writeBytes(input, output);
    }
    private static void writeBytes(InputStream input, OutputStream output) throws IOException {
        byte[] writeBuffer = new byte[1024];
        int readBytes = 0;
        while ((readBytes = input.read(writeBuffer)) >= 0) {
            output.write(writeBuffer, 0, readBytes);
        }
        output.close();
        input.close();
    }
    private static void writeKey(int keySize, String output,String algorithm) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        kg.init(keySize);
        System.out.println();
        System.out.println("KeyGenerator Object Info: ");
        System.out.println("Algorithm = " + kg.getAlgorithm());
        System.out.println("Provider = " + kg.getProvider());
        System.out.println("Key Size = " + keySize);
        System.out.println("toString = " + kg.toString());

        SecretKey ky = kg.generateKey();
        byte[] kb;
        try (FileOutputStream fos = new FileOutputStream(output)) {
            kb = ky.getEncoded();
            fos.write(kb);
        }
        System.out.println();
        System.out.println("SecretKey Object Info: ");
        System.out.println("Algorithm = " + ky.getAlgorithm());
        System.out.println("Saved File = " + output);
        System.out.println("Size = " + kb.length);
        System.out.println("Format = " + ky.getFormat());
        System.out.println("toString = " + ky.toString());
    }
    private static SecretKey readKey(String input, String algorithm)throws Exception {
        FileInputStream fis = new FileInputStream(input);
        int kl = fis.available();
        byte[] kb = new byte[kl];
        fis.read(kb);
        fis.close();
        KeySpec ks = null;
        SecretKey ky = null;
        SecretKeyFactory kf = null;
        if (algorithm.equalsIgnoreCase("DES")) {
            ks = new DESKeySpec(kb);
            kf = SecretKeyFactory.getInstance("DES");
            ky = kf.generateSecret(ks);
        } else if (algorithm.equalsIgnoreCase("DESede")) {
            ks = new DESedeKeySpec(kb);
            kf = SecretKeyFactory.getInstance("DESede");
            ky = kf.generateSecret(ks);
        } else {
            ks = new SecretKeySpec(kb, algorithm);
            ky = new SecretKeySpec(kb, algorithm);
        }
        /*
        System.out.println();
        System.out.println("KeySpec Object Info: ");
        System.out.println("Saved File = " + fl);
        System.out.println("Length = " + kb.length);
        System.out.println("toString = " + ks.toString());
        System.out.println();
        System.out.println("SecretKey Object Info: ");
        System.out.println("Algorithm = " + ky.getAlgorithm());
        System.out.println("toString = " + ky.toString());
        */
        return ky;
    }
}
