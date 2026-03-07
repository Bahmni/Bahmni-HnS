/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.util;

public class StringUtil {
    public static String ensureSuffix(String value, String pattern) {
        String trimmedValue = value.trim();
        if (trimmedValue.endsWith(pattern)) {
            return trimmedValue;
        } else {
            return trimmedValue + pattern;
        }
    }

    public static String removeSuffix(String value, String pattern) {
        String trimmedValue = value.trim();
        if (trimmedValue.endsWith(pattern)) {
            return trimmedValue.substring(0, trimmedValue.lastIndexOf(pattern));
        } else {
            return trimmedValue;
        }
    }

    public static String removePrefix(String value, String prefix) {
        String trimmedValue = value.trim();
        if (trimmedValue.startsWith(prefix)) {
            return trimmedValue.substring(prefix.length());
        } else {
            return trimmedValue;
        }
    }

}
