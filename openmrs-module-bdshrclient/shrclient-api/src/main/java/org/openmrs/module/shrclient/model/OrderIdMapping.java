/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.model;

import java.util.Date;

public class OrderIdMapping extends IdMapping {

    public OrderIdMapping(String internalId, String externalId, String type, String uri, Date createdAt) {
        super(internalId, externalId, type, uri, createdAt);
    }

    public OrderIdMapping(String internalId, String externalId, String type, String uri) {
        this(internalId, externalId, type, uri, new Date());
    }
}
