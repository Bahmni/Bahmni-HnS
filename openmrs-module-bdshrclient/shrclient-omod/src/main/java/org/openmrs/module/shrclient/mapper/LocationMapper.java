/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.mapper;

import org.openmrs.Location;
import org.openmrs.module.shrclient.model.FRLocationEntry;

public class LocationMapper {

    public static final String RETIRE_REASON = "Upstream Deletion";

    public Location updateExisting(Location location, FRLocationEntry locationEntry) {
        return writeChanges(location, locationEntry);
    }
    public Location create(FRLocationEntry locationEntry) {
        return writeChanges(new Location(), locationEntry);
    }
    private Location writeChanges(Location location, FRLocationEntry locationEntry) {
        String locationName = String.format("%s (%s)", locationEntry.getName(), locationEntry.getId());
        location.setName(locationName);
        if ("0".equals(locationEntry.getActive())) {
            location.setRetired(true);
            location.setRetireReason(RETIRE_REASON);
        } else {
            location.setRetired(false);
            location.setRetireReason(null);
        }
        return location;
    }
}
