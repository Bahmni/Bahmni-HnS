/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.feeds.openmrs;

import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.repository.AllMarkers;

import java.net.URI;
import java.util.HashMap;

public class AllMarkersInMemoryImpl implements AllMarkers {
    private final HashMap<URI,Marker> map;

    public AllMarkersInMemoryImpl() {
        map = new HashMap<>();
    }

    @Override
    public Marker get(URI feedUri) {
        return map.get(feedUri);
    }

    @Override
    public void put(URI feedUri, String entryId, URI entryFeedUri) {
        map.put(feedUri, new Marker(feedUri, entryId, entryFeedUri));
    }

}