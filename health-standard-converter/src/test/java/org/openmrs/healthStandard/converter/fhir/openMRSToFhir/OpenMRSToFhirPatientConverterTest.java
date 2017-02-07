package org.openmrs.healthStandard.converter.fhir.openMRSToFhir;

import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.fhir.FHIRConverter;
import org.openmrs.healthStandard.converter.fhir.FHIRConverterRegistry;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirPatient;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OpenMRSToFhirPatientConverterTest extends BaseModuleContextSensitiveTest {

    private FHIRConverter<Patient,FhirPatient> converter;

    @Before
    public void setUp() throws Exception {
        converter = FHIRConverterRegistry.getInstance().getConverterFor(Patient.class, org.hl7.fhir.dstu3.model.Patient.class);
    }

    @Test
    public void shouldConvertOpenMrsPatientToFhir() throws Exception {
        Patient omrsPatient = Context.getPatientService().getPatient(2);
        FhirPatient fhirPatient = converter.convert(omrsPatient);
        assertEquals(omrsPatient.getUuid(),fhirPatient.getId());
        assertIdentifier(omrsPatient.getIdentifiers(),fhirPatient.getIdentifier());
        assertNames(omrsPatient.getNames(),fhirPatient.getName());
        assertEquals(AdministrativeGender.MALE,fhirPatient.getGender());
        assertEquals(omrsPatient.getBirthdate(),fhirPatient.getBirthDate());
        assertEquals(omrsPatient.getBirthdateEstimated(),fhirPatient.getBirthDateEstimated());
    }

    @Test
    public void shouldMapGender() throws Exception {
        Patient patient = new Patient();
        patient.setGender("F");
        assertEquals(converter.convert(patient).getGender(),AdministrativeGender.FEMALE);
        patient.setGender("O");
        assertEquals(converter.convert(patient).getGender(),AdministrativeGender.OTHER);
        patient.setGender("A");
        assertEquals(converter.convert(patient).getGender(),AdministrativeGender.UNKNOWN);

    }

    @Test
    public void shouldMapDeathDateAndCauseOfDate() throws Exception {
        Patient patient = new Patient();
        patient.setDead(true);
        patient.setDeathDate(new SimpleDateFormat("dd/MM/yy").parse("01/01/2017"));
        patient.setDeathdateEstimated(true);
        patient.setCauseOfDeath(new Concept());

        FhirPatient fhirPatient = converter.convert(patient);

        assertEquals(patient.getDeathdateEstimated(),fhirPatient.getDeceasedDateEstimated());
        assertEquals(patient.getDeathDate(),fhirPatient.getDeceasedDateTimeType().getValue());
        assertEquals(patient.getCauseOfDeath().getUuid(),fhirPatient.getDeceasedCause().getValueNotNull());

    }

    private void assertNames(Set<PersonName> omrsNames, List<HumanName> fhirPatientNames) {
        assertEquals(omrsNames.size(),fhirPatientNames.size());
        Map<String, PersonName> personNameMap = omrsNames.stream().collect(Collectors.toMap(PersonName::getFamilyName, name -> name));
        for (HumanName fhirPatientName : fhirPatientNames) {
            PersonName personName = personNameMap.get(fhirPatientName.getFamily().get(0).getValue());
            assertNotNull(personName);
            String givenName = personName.getGivenName() + " " + personName.getMiddleName();
            assertEquals(givenName,fhirPatientName.getGiven().get(0).getValue());
            assertEquals(defaultIfBlank(personName.getPrefix(),""),fhirPatientName.getPrefixAsSingleString());
            assertEquals(defaultIfBlank(personName.getFamilyNameSuffix(),""),fhirPatientName.getSuffixAsSingleString());
            assertEquals(personName.getPreferred(),fhirPatientName.getUse().equals(NameUse.OFFICIAL));
            assertEquals(!personName.getPreferred(),fhirPatientName.getUse().equals(NameUse.USUAL));
        }

    }

    private void assertIdentifier(Set<PatientIdentifier> omrsIdentifiers, List<Identifier> fhirIdentifiers){
        assertEquals(omrsIdentifiers.size(),fhirIdentifiers.size());
        Map<String, PatientIdentifier> identifier =
                omrsIdentifiers.stream().collect(Collectors.toMap(PatientIdentifier::getIdentifier, id -> id));
        for (Identifier fhirIdentifier : fhirIdentifiers) {
            PatientIdentifier patientIdentifier = identifier.get(fhirIdentifier.getValue());
            assertNotNull(patientIdentifier);
            assertEquals(patientIdentifier.getIdentifierType().getUuid(),fhirIdentifier.getSystem());
            assertEquals(patientIdentifier.getPreferred(),fhirIdentifier.getUse().equals(IdentifierUse.USUAL));
            assertEquals(!patientIdentifier.getPreferred(),fhirIdentifier.getUse().equals(IdentifierUse.SECONDARY));
        }
    }


}