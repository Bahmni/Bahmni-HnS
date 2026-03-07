/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.identity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityToken {
    public IdentityToken() {
    }

    public IdentityToken(String token) {
        this.token = token;
    }

    @JsonProperty("access_token")
    private String token;

    @Override
    public String toString() {
        return token;
    }
}
