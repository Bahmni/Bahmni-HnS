/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.bahmniHubConnect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

public class Activator extends BaseModuleActivator {

    Log log = LogFactory.getLog(Activator.class);

    @Override
    public void started() {
        log.info("Started the Bahmni Resource Sync Atom Feed Client module");
    }

    @Override
    public void stopped() {
        log.info("Stopped the Bahmni Resource Sync Atom Feed Client module");
    }
}
