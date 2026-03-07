/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.model;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class EncounterIdMappingTest {

    @Test
    public void shouldSetHealthIdFromUri() {
        EncounterIdMapping encounterIdMapping = new EncounterIdMapping("internalId","externalId","http://shr.com/patients/123456789/encounters/encounter-id", new Date(), null,null);
        assertEquals("123456789", encounterIdMapping.getHealthId());

    }
}