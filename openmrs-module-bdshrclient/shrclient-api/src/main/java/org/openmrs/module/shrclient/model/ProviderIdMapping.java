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

public class ProviderIdMapping extends IdMapping {

    public ProviderIdMapping(String internalId, String externalId, String uri) {
        super(internalId, externalId, IdMappingType.PROVIDER, uri, new Date());
    }
}
