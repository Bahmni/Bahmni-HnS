/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.shrclient.web.controller.MciPatientSearchRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MCIPatientLookupService {

    @Authorized(value = {"National Registry"}, requireAll = true)
    public Object searchPatientInRegistry(MciPatientSearchRequest request);

    @Authorized(value = {"National Registry"}, requireAll = true)
    public Object downloadPatient(MciPatientSearchRequest request);
}
