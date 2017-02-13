package org.openmrs.healthStandard.converter.fhir.openMRSToFhir;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.LocationTag;
import org.openmrs.healthStandard.converter.fhir.FHIRConstants;
import org.openmrs.healthStandard.converter.fhir.FHIRConverter;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OpenMRSToFhirLocationConverter implements FHIRConverter<org.openmrs.Location, FhirLocation> {

    @Override
    public FhirLocation convert(org.openmrs.Location omrsLocation) {
        FhirLocation fhirLocation = new FhirLocation();

        fhirLocation.setId(omrsLocation.getUuid());
        fhirLocation.setName(omrsLocation.getName());
        fhirLocation.setDescription(omrsLocation.getDescription());

        setAddress(omrsLocation, fhirLocation);

        setPosition(omrsLocation, fhirLocation);

        setStatus(omrsLocation, fhirLocation);

        setParentLocation(omrsLocation, fhirLocation);

        setTags(omrsLocation, fhirLocation);
        return fhirLocation;
    }

    private void setTags(org.openmrs.Location omrsLocation, FhirLocation fhirLocation) {
        Set<LocationTag> tags = omrsLocation.getTags();
        if (tags != null) {
            for (LocationTag tag : tags) {
                fhirLocation.addTag(new StringType(tag.getUuid()));

            }
        }
    }

    private void setParentLocation(org.openmrs.Location omrsLocation, FhirLocation fhirLocation) {
        if (omrsLocation.getParentLocation() != null) {
            Reference parent = new Reference();
            parent.setDisplay(omrsLocation.getParentLocation().getName());
            parent.setReference(FHIRConstants.LOCATION + "/" + omrsLocation.getParentLocation().getUuid());
            fhirLocation.setPartOf(parent);
        }
    }

    private void setStatus(org.openmrs.Location omrsLocation, FhirLocation fhirLocation) {
        if (!omrsLocation.getRetired()) {
            fhirLocation.setStatus(Location.LocationStatus.ACTIVE);
        } else {
            fhirLocation.setStatus(Location.LocationStatus.INACTIVE);
            fhirLocation.setInactivationReason(new StringType(omrsLocation.getRetireReason()));
        }
    }

    private void setPosition(org.openmrs.Location omrsLocation, FhirLocation fhirLocation) {
        Location.LocationPositionComponent position = fhirLocation.getPosition();
        if (omrsLocation.getLongitude() != null && !omrsLocation.getLongitude().isEmpty()) {
            BigDecimal longitude = new BigDecimal(omrsLocation.getLongitude());
            position.setLongitude(longitude);
        }

        if (omrsLocation.getLatitude() != null && !omrsLocation.getLatitude().isEmpty()) {
            BigDecimal latitude = new BigDecimal(omrsLocation.getLatitude());
            position.setLatitude(latitude);
        }
    }

    private void setAddress(org.openmrs.Location omrsLocation, FhirLocation fhirLocation) {
        Address address = new Address();
        address.setCity(omrsLocation.getCityVillage());
        address.setDistrict(omrsLocation.getCountyDistrict());
        address.setState(omrsLocation.getStateProvince());
        address.setCountry(omrsLocation.getCountry());
        address.setPostalCode(omrsLocation.getPostalCode());
        address.setLine(getAddressLines(omrsLocation));
        address.setUse(Address.AddressUse.WORK);
        fhirLocation.setAddress(address);
    }

    private List<StringType> getAddressLines(org.openmrs.Location omrsLocation) {
        int MAX_ADDRESS_LINES = 15;
        return IntStream.range(0, MAX_ADDRESS_LINES)
                .mapToObj(i -> {
                    String methodName = "getAddress" + (i + 1);
                    StringType addressLine;
                    try {
                        Method method = org.openmrs.Location.class.getMethod(methodName);
                        addressLine = new StringType((String) method.invoke(omrsLocation));
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Error executing method " + methodName, e);
                    }
                    return addressLine;
                })
                .collect(Collectors.toList());
    }
}
