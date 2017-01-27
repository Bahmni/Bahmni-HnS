package org.openmrs.healthStandard.converter.fhir.fhirModels;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.StringType;

import static org.openmrs.healthStandard.converter.fhir.FHIRConstants.birthDateEstimatedUrl;
import static org.openmrs.healthStandard.converter.fhir.FHIRConstants.deceasedCauseUrl;
import static org.openmrs.healthStandard.converter.fhir.FHIRConstants.deceasedDateEstimatedUrl;

public class FhirPatient extends Patient {

    @Child(name = "birthDateEstimated")
    @Extension(url = birthDateEstimatedUrl, definedLocally = false, isModifier = false)
    private BooleanType birthDateEstimated;

    @Child(name = "deceasedDateEstimated")
    @Extension(url = deceasedDateEstimatedUrl, definedLocally = false, isModifier = false)
    private BooleanType deceasedDateEstimated;

    @Child(name = "deceasedCause")
    @Extension(url = deceasedCauseUrl, definedLocally = false, isModifier = false)
    private StringType deceasedCause;

    public Boolean getBirthDateEstimated() {
        return birthDateEstimated.booleanValue();
    }

    public void setBirthDateEstimated(Boolean birthDateEstimated) {
        this.birthDateEstimated = new BooleanType(birthDateEstimated);
    }

    public Boolean getDeceasedDateEstimated() {
        return deceasedDateEstimated.booleanValue();
    }

    public void setDeceasedDateEstimated(Boolean deathDateEstimated) {
        this.deceasedDateEstimated = new BooleanType(deathDateEstimated);
    }

    public StringType getDeceasedCause() {
        return deceasedCause;
    }

    public void setDeceasedCause(String deceasedCause) {
        this.deceasedCause = new StringType(deceasedCause);
    }
}
