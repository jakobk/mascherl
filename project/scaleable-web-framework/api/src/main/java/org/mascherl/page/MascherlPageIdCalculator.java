package org.mascherl.page;

import org.mascherl.application.MascherlApplication;

import javax.ws.rs.container.ResourceInfo;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Calculator for the pageId, which is attached to every container in HTML.
 *
 * @author Jakob Korherr
 */
public class MascherlPageIdCalculator {

    private static final String SHA_256 = "SHA-256";

    public static String calculatePageId(MascherlApplication mascherlApplication, ResourceInfo resourceInfo) {
        String pageId = resourceInfo.getResourceClass().getName();
        if (resourceInfo.getResourceMethod() != null) {
            pageId += "." + resourceInfo.getResourceMethod().getName();
        }

        if (!mascherlApplication.isDevelopmentMode()) {
            // SHA-256 plain resource page id in order to hide resource class + method
            pageId = sha256(pageId);
        }

        return pageId;
    }

    private static String sha256(String value) {
        MessageDigest messageDigest = createMessageDigest();
        messageDigest.update(value.getBytes(StandardCharsets.UTF_8));
        byte[] digest = messageDigest.digest();
        byte[] base64Digest = Base64.getEncoder().encode(digest);
        return new String(base64Digest, StandardCharsets.UTF_8);
    }

    private static MessageDigest createMessageDigest() {
        try {
            return MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
