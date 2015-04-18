package org.mascherl.page;

import org.mascherl.application.MascherlApplication;

import javax.ws.rs.container.ResourceInfo;

/**
 * Calculator for the page group, which is attached to every container in HTML.
 *
 * @author Jakob Korherr
 */
public class MascherlPageGroupCalculator {

    public static String calculatePageGroup(MascherlApplication mascherlApplication, ResourceInfo resourceInfo) {
        return calculatePageGroup(mascherlApplication, resourceInfo, null);
    }

    public static String calculatePageGroup(MascherlApplication mascherlApplication, ResourceInfo resourceInfo, String actionPageGroup) {
        return findPageGroup(resourceInfo, actionPageGroup);
    }

    private static String findPageGroup(ResourceInfo resourceInfo, String actionPageGroup) {
        if (actionPageGroup != null) {
            return actionPageGroup;
        }
        PageGroup pageGroupAnnotation = findPageGroupAnnotation(resourceInfo);
        if (pageGroupAnnotation != null) {
            return pageGroupAnnotation.value();
        } else {
            return resourceInfo.getResourceClass().getSimpleName();
        }
    }

    private static PageGroup findPageGroupAnnotation(ResourceInfo resourceInfo) {
        PageGroup pageGroupAnnotation = resourceInfo.getResourceMethod().getAnnotation(PageGroup.class);
        if (pageGroupAnnotation == null) {
            pageGroupAnnotation = resourceInfo.getResourceClass().getAnnotation(PageGroup.class);
        }
        return pageGroupAnnotation;
    }

}
