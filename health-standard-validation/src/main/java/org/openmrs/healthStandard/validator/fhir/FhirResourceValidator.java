package org.openmrs.healthStandard.validator.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.dstu3.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.openmrs.healthStandard.validator.exceptions.FhirValidationException;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

public class FhirResourceValidator {
    static String FHIR_PROFILES_DIR_NAME = "fhirProfiles/";
    static String JSON_EXTENSION = ".json";

    public static void validateResource(DomainResource domainResource) {
        ResourceDef annotation = domainResource.getClass().getAnnotation(ResourceDef.class);
        String resourceName = annotation.name();

        FhirContext fhirContext = FhirContext.forDstu3();
        fhirContext.setParserErrorHandler(new StrictErrorHandler());
        FhirValidator fhirValidator = fhirContext.newValidator();

        FhirInstanceValidator fhirInstanceValidator = getFhirInstanceValidator(resourceName);
        if (fhirInstanceValidator != null) fhirValidator.registerValidatorModule(fhirInstanceValidator);

        ValidationResult validationResult = fhirValidator.validateWithResult(domainResource);
        if (!validationResult.isSuccessful()) {
            String message = validationResult
                    .getMessages()
                    .stream()
                    .map(SingleValidationMessage::toString)
                    .collect(Collectors.joining(","));

            throw new FhirValidationException(message);
        }
    }

    private static FhirInstanceValidator getFhirInstanceValidator(String resourceName) {
        FhirInstanceValidator fhirInstanceValidator = null;

        StructureDefinition structureDefinition = getStructureDefinition(resourceName);
        if (structureDefinition != null) {
            fhirInstanceValidator = new FhirInstanceValidator();
            fhirInstanceValidator.setStructureDefintion(structureDefinition);
        }
        return fhirInstanceValidator;
    }

    private static StructureDefinition getStructureDefinition(String resourceName) {
        FhirContext fhirContext = FhirContext.forDstu3();
        String profileText;
        try {
            String pathToProfile = OpenmrsUtil.getApplicationDataDirectory() + FHIR_PROFILES_DIR_NAME + resourceName + JSON_EXTENSION;
            File file = new File(pathToProfile);
            if (file.exists()) {
                profileText = IOUtils.toString(new FileInputStream(file), "UTF-8");
            } else {
                InputStream resourceAsStream = FhirResourceValidator.class.getClassLoader().getResourceAsStream(FHIR_PROFILES_DIR_NAME + resourceName + JSON_EXTENSION);
                if (resourceAsStream == null) return null;
                profileText = IOUtils.toString(resourceAsStream, "UTF-8");
            }
        } catch (IOException e) {
            return null;
        }
        return fhirContext.newJsonParser().parseResource(StructureDefinition.class, profileText);
    }
}
