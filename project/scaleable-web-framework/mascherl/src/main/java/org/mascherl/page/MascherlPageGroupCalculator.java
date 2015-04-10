package org.mascherl.page;

import org.mascherl.application.MascherlApplication;

import javax.ws.rs.container.ResourceInfo;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Calculator for the page group, which is attached to every container in HTML.
 *
 * @author Jakob Korherr
 */
public class MascherlPageGroupCalculator {

    private static final String SHA_256 = "SHA-256";

    public static String calculatePageGroup(MascherlApplication mascherlApplication, ResourceInfo resourceInfo) {
        return calculatePageGroup(mascherlApplication, resourceInfo, null);
    }

    public static String calculatePageGroup(MascherlApplication mascherlApplication, ResourceInfo resourceInfo, String formResultPageGroup) {
        String pageGroup = findPageGroup(resourceInfo, formResultPageGroup);

        if (!mascherlApplication.isDevelopmentMode()) {
            // SHA-256 plain resource page id in order to hide resource class + method
            pageGroup = sha256(pageGroup);
        }

        return pageGroup;
    }

    private static String findPageGroup(ResourceInfo resourceInfo, String formResultPageGroup) {
        if (formResultPageGroup != null) {
            return formResultPageGroup;
        }
        PageGroup pageGroupAnnotation = findPageGroupAnnotation(resourceInfo);
        if (pageGroupAnnotation != null) {
            return pageGroupAnnotation.value();
        } else {
            return resourceInfo.getResourceClass().getName();
        }
    }

    private static PageGroup findPageGroupAnnotation(ResourceInfo resourceInfo) {
        PageGroup pageGroupAnnotation = resourceInfo.getResourceMethod().getAnnotation(PageGroup.class);
        if (pageGroupAnnotation == null) {
            pageGroupAnnotation = resourceInfo.getResourceClass().getAnnotation(PageGroup.class);
        }
        return pageGroupAnnotation;
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
