/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.service;

import org.openmrs.module.shrclient.dao.BbsCodeDao;
import org.springframework.stereotype.Service;

@Service
public class BbsCodeService {

    private final BbsCodeDao dao;

    public BbsCodeService() {
        this.dao = BbsCodeDao.getInstance();
    }

    public String getGenderCode(String concept) {
        return dao.getGenderCode(concept);
    }

    public String getGenderConcept(String code) {
        return dao.getGenderConcept(code);
    }

    public String getEducationCode(String concept) {
        return dao.getEducationCode(concept);
    }

    public String getEducationConceptName(String code) {
        return dao.getEducationConcept(code);
    }

    public String getOccupationCode(String concept) {
        return dao.getOccupationCode(concept);
    }

    public String getOccupationConceptName(String code) {
        return dao.getOccupationConcept(code);
    }
}
