/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.healthStandard.converter.fhir.fhirModels;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.StringType;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.healthStandard.converter.fhir.FHIRConstants.inactivationReasonUrl;
import static org.openmrs.healthStandard.converter.fhir.FHIRConstants.locationTagUrl;

@ResourceDef(name = "Location")
public class FhirLocation extends Location {

    @Child(name = "fhirLocationTags")
    @Extension(url = locationTagUrl, definedLocally = false, isModifier = false)
    protected List<StringType> fhirLocationTags = new ArrayList<>();

    @Child(name = "inactivationReason")
    @Extension(url = inactivationReasonUrl, definedLocally = false, isModifier = false)
    protected StringType inactivationReason;

    public List<StringType> getFhirLocationTags() {
        return fhirLocationTags;
    }

    public void addTag(StringType fhirLocationTag) {
        fhirLocationTags.add(fhirLocationTag);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && ElementUtil.isEmpty(fhirLocationTags);
    }

    public StringType getInactivationReason() {
        return inactivationReason;
    }

    public void setInactivationReason(StringType inactivationReason) {
        this.inactivationReason = inactivationReason;
    }
}
