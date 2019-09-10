package com.abnamro.care.event.in.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class UUIDGenerator {

	 /**
     * Type 5 UUID Generation
     * 
     * @throws UnsupportedEncodingException 
     */
    public static UUID generateType5UUID(String namespace, String name) throws UnsupportedEncodingException {
        String source = namespace + name;
        byte[] bytes = source.getBytes("UTF-8");
        UUID uuid = type5UUIDFromBytes(bytes);
        return uuid;
    }

    public static UUID type5UUIDFromBytes(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported", nsae);
        }
        byte[] bytes = md.digest(name);
        bytes[6] &= 0x0f; /* clear version        */
        bytes[6] |= 0x50; /* set to version 5     */
        bytes[8] &= 0x3f; /* clear variant        */
        bytes[8] |= 0x80; /* set to IETF variant  */
        return constructType5UUID(bytes);
    }

    private static UUID constructType5UUID(byte[] data) {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";

        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);

        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);
        return new UUID(msb, lsb);
    }
}
