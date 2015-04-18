/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
