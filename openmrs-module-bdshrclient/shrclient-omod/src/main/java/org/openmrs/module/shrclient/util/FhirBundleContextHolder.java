/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.util;

import ca.uhn.fhir.context.FhirContext;
import org.springframework.stereotype.Component;

@Component
public class FhirBundleContextHolder {
    private static FhirContext fhirContext;


    public static FhirContext getFhirContext() {
        if(fhirContext == null) {
            fhirContext = FhirContext.forDstu2();
        }
        return fhirContext;
    }
}
