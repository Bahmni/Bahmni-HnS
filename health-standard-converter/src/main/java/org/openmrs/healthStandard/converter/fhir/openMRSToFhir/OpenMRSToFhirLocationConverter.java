package org.openmrs.healthStandard.converter.fhir.openMRSToFhir;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.healthStandard.converter.fhir.FHIRConstants;
import org.openmrs.healthStandard.converter.fhir.FHIRConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OpenMRSToFhirLocationConverter implements FHIRConverter<org.openmrs.Location, Location> {

    @Override
    public Location convert(org.openmrs.Location omrsLocation) {
        Location fhirLocation = new Location();

        //Set resource id
        fhirLocation.setId(omrsLocation.getUuid());

        //Set name and location description
        fhirLocation.setName(omrsLocation.getName());
        fhirLocation.setDescription(omrsLocation.getDescription());

        //Set address
        Address address = new Address();
        address.setCity(omrsLocation.getCityVillage());
        address.setDistrict(omrsLocation.getCountyDistrict());
        address.setState(omrsLocation.getStateProvince());
        address.setCountry(omrsLocation.getCountry());
        address.setPostalCode(omrsLocation.getPostalCode());
        List<StringType> addressStrings = new ArrayList<>();
        addressStrings.add(new StringType(omrsLocation.getAddress1()));
        addressStrings.add(new StringType(omrsLocation.getAddress2()));
        addressStrings.add(new StringType(omrsLocation.getAddress3()));
        addressStrings.add(new StringType(omrsLocation.getAddress4()));
        addressStrings.add(new StringType(omrsLocation.getAddress5()));
        address.setLine(addressStrings);
        address.setUse(Address.AddressUse.WORK);
        fhirLocation.setAddress(address);

        Location.LocationPositionComponent position = fhirLocation.getPosition();
        if (omrsLocation.getLongitude() != null && !omrsLocation.getLongitude().isEmpty()) {
            BigDecimal longitude = new BigDecimal(omrsLocation.getLongitude());
            position.setLongitude(longitude);
        }

        if (omrsLocation.getLatitude() != null && !omrsLocation.getLatitude().isEmpty()) {
            BigDecimal latitude = new BigDecimal(omrsLocation.getLatitude());
            position.setLatitude(latitude);
        }

        if (!omrsLocation.isRetired()) {
            fhirLocation.setStatus(Location.LocationStatus.ACTIVE);
        } else {
            fhirLocation.setStatus(Location.LocationStatus.INACTIVE);
        }

        if (omrsLocation.getParentLocation() != null) {
            Reference parent = new Reference();
            parent.setDisplay(omrsLocation.getParentLocation().getName());
            parent.setReference(FHIRConstants.LOCATION+"/"+omrsLocation.getParentLocation().getUuid());
            fhirLocation.setPartOf(parent);
        }
        return fhirLocation;
    }
}
