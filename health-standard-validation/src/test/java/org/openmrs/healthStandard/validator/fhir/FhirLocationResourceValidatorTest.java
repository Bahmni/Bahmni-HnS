/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.healthStandard.validator.fhir;

import org.hl7.fhir.dstu3.model.Location;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.healthStandard.validator.exceptions.FhirValidationException;


public class FhirLocationResourceValidatorTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenValidationFailsForProvidedProfile() throws Exception {
        expectedEx.expect(FhirValidationException.class);
        expectedEx.expectMessage("Element 'Location.name': minimum required = 1, but only found 0");

        Location location = new Location();
        FhirResourceValidator.validateResource(location);
    }

    @Test
    public void shouldNotThrowExceptionWhenValidationPassesForProvidedProfile() throws Exception {
        expectedEx.reportMissingExceptionWithMessage("No Exception");

        Location location = new Location();
        location.setName("someName");
        FhirResourceValidator.validateResource(location);
    }
}
