package org.openmrs.healthStandard.converter.fhir.openMRSToFhir;

import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.healthStandard.converter.fhir.FHIRConverter;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirPatient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.UNKNOWN;
import static org.hl7.fhir.dstu3.model.Identifier.IdentifierUse.SECONDARY;
import static org.hl7.fhir.dstu3.model.Identifier.IdentifierUse.USUAL;

public class OpenMRSToFhirPatientConverter implements FHIRConverter<Patient, FhirPatient> {
    private static final Map<String, AdministrativeGender> genderMap = new HashMap<String, AdministrativeGender>() {
        {
            put("M", AdministrativeGender.MALE);
            put("F", AdministrativeGender.FEMALE);
            put("O", AdministrativeGender.OTHER);
        }
    };

    @Override
    public FhirPatient convert(Patient omrsPatient) {
        FhirPatient fhirPatient = new FhirPatient();
        fhirPatient.setId(omrsPatient.getUuid());
        setIdentifier(omrsPatient, fhirPatient);
        setName(omrsPatient, fhirPatient);
        setGender(omrsPatient, fhirPatient);
        setBirthdateAndDeathDate(omrsPatient, fhirPatient);


        return fhirPatient;
    }

    private void setBirthdateAndDeathDate(Patient omrsPatient, FhirPatient fhirPatient) {
        fhirPatient.setBirthDate(omrsPatient.getBirthdate());
        fhirPatient.setBirthDateEstimated(omrsPatient.getBirthdateEstimated());

        if (omrsPatient.getDead()) {
            fhirPatient.setDeceased(new DateTimeType(omrsPatient.getDeathDate()));
            fhirPatient.setDeceasedDateEstimated(omrsPatient.getDeathdateEstimated());
            Concept causeOfDeath = omrsPatient.getCauseOfDeath();
            if (causeOfDeath != null) {
                fhirPatient.setDeceasedCause(causeOfDeath.getUuid());
            }
        }
    }

    private void setGender(Patient omrsPatient, FhirPatient fhirPatient) {
        String gender = omrsPatient.getGender();
        fhirPatient.setGender(genderMap.getOrDefault(gender, UNKNOWN));
    }

    private void setName(Patient omrsPatient, FhirPatient fhirPatient) {
        //todo:middel name mapping,are we using familyname2??
        List<HumanName> humanNames = omrsPatient.getNames().stream().map(name -> {
            HumanName fhirName = new HumanName();
            fhirName.setFamily(Arrays.asList(new StringType(name.getFamilyName())));
            StringType givenName = new StringType(name.getGivenName() + " " + name.getMiddleName());
            fhirName.setGiven(Arrays.asList(givenName));
            StringType suffix = new StringType(name.getFamilyNameSuffix());
            fhirName.setSuffix(Arrays.asList(suffix));
            StringType prefix = new StringType(name.getPrefix());
            fhirName.setPrefix(Arrays.asList(prefix));
            NameUse nameUse = name.getPreferred() ? NameUse.OFFICIAL : NameUse.USUAL;
            fhirName.setUse(nameUse);
            return fhirName;
        }).collect(Collectors.toList());
        fhirPatient.setName(humanNames);
    }

    private void setIdentifier(Patient omrsPatient, FhirPatient fhirPatient) {
        for (PatientIdentifier identifier : omrsPatient.getActiveIdentifiers()) {
            IdentifierUse identifierUse = identifier.getPreferred() ? USUAL : SECONDARY;
            fhirPatient.addIdentifier().setUse(identifierUse).setSystem(identifier.getIdentifierType().getUuid())
                    .setValue(identifier.getIdentifier());
        }
    }
}
