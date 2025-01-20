package com.example;

import java.util.Base64;

public class FeistelCipher {

    private static final int NUM_ROUNDS = 16;
    private static final int BLOCK_SIZE = 8; // Block size in bytes

    public static String encrypt(String plaintext, String key) {
        byte[] data = pad(plaintext.getBytes());
        byte[] encryptedData = new byte[data.length];

        for (int i = 0; i < data.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(data, i, block, 0, BLOCK_SIZE);
            byte[] encryptedBlock = processBlock(block, key, true);
            System.arraycopy(encryptedBlock, 0, encryptedData, i, BLOCK_SIZE);
        }

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String ciphertext, String key) {
        byte[] data = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedData = new byte[data.length];

        for (int i = 0; i < data.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(data, i, block, 0, BLOCK_SIZE);
            byte[] decryptedBlock = processBlock(block, key, false);
            System.arraycopy(decryptedBlock, 0, decryptedData, i, BLOCK_SIZE);
        }

        return new String(unpad(decryptedData)).trim();
    }

    private static byte[] processBlock(byte[] block, String key, boolean encrypt) {
        int[] keys = generateRoundKeys(key);
        int[] data = new int[block.length];
        for (int i = 0; i < block.length; i++) {
            data[i] = block[i] & 0xFF;
        }

        for (int i = 0; i < NUM_ROUNDS; i++) {
            int roundKey = keys[encrypt ? i : NUM_ROUNDS - 1 - i];
            for (int j = 0; j < data.length; j++) {
                data[j] = feistelFunction(data[j], roundKey);
            }
        }

        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) data[i];
        }
        return result;
    }

    private static int feistelFunction(int halfBlock, int key) {
        return (halfBlock + key) % 256;
    }

    private static int[] generateRoundKeys(String key) {
        int[] keys = new int[NUM_ROUNDS];
        for (int i = 0; i < NUM_ROUNDS; i++) {
            keys[i] = key.charAt(i % key.length());
        }
        return keys;
    }

    private static byte[] pad(byte[] data) {
        int paddingLength = BLOCK_SIZE - (data.length % BLOCK_SIZE);
        byte[] paddedData = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedData, 0, data.length);
        for (int i = data.length; i < paddedData.length; i++) {
            paddedData[i] = (byte) paddingLength;
        }
        return paddedData;
    }

    private static byte[] unpad(byte[] data) {
        int paddingLength = data[data.length - 1];
        byte[] unpaddedData = new byte[data.length - paddingLength];
        System.arraycopy(data, 0, unpaddedData, 0, unpaddedData.length);
        return unpaddedData;
    }
}