/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class EncounterIdMapping extends IdMapping {
    private String healthId;

    public EncounterIdMapping(String internalId, String externalId, String uri, Date createdAt, Date lastSyncDateTime, Date serverUpdateDateTime) {
        super(internalId, externalId, IdMappingType.ENCOUNTER, uri, createdAt, lastSyncDateTime, serverUpdateDateTime);
        this.healthId = StringUtils.substringBefore(StringUtils.substringAfter(uri, "patients/"), "/encounters");
    }

    public EncounterIdMapping(String internalId, String externalId, String uri, Date lastSyncDateTime) {
        this(internalId, externalId, uri, new Date(), lastSyncDateTime, null);
    }

    public String getHealthId() {
        return this.healthId;
    }
}
