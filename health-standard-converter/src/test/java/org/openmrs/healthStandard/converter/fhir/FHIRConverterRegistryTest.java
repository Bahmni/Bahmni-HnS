package org.openmrs.healthStandard.converter.fhir;

import org.hl7.fhir.dstu3.model.Location;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.healthStandard.converter.ConverterSameOrderException;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class FHIRConverterRegistryTest {


    FHIRConverterRegistry fhirConverterRegistry;

    @Before
    public void setUp() throws Exception {
        fhirConverterRegistry = FHIRConverterRegistry.getInstance();
        Field instance = FHIRConverterRegistry.class.getDeclaredField("fhirConverterRegistry");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void shouldRegisterConverterWithLowOrder() throws Exception {
        FHIRConverter converterWithOrder10 = getConverterWithOrder(10);
        FHIRConverter converterWithOrder20 = getConverterWithOrder(20);
        fhirConverterRegistry.registerConverter(org.openmrs.Location.class, Location.class, converterWithOrder10);
        fhirConverterRegistry.registerConverter(org.openmrs.Location.class, Location.class, converterWithOrder20);

        FHIRConverter<org.openmrs.Location, Location> converter = fhirConverterRegistry
                .getConverterFor(org.openmrs.Location.class, Location.class);

        assertEquals(converterWithOrder10, converter);

    }

    @Test(expected = ConverterSameOrderException.class)
    public void shouldThrowExceptionWhenTheOrderIsSame() throws Exception {
        FHIRConverter converterOne = getConverterWithOrder(10);
        FHIRConverter converterTwo = getConverterWithOrder(10);
        fhirConverterRegistry.registerConverter(org.openmrs.Location.class, Location.class, converterOne);
        fhirConverterRegistry.registerConverter(org.openmrs.Location.class, Location.class, converterTwo);
    }

    private FHIRConverter getConverterWithOrder(int order) {
        return new FHIRConverter<org.openmrs.Location, Location>() {
            @Override
            public Location convert(org.openmrs.Location source) {
                return null;
            }

            @Override
            public int getOrder() {
                return order;
            }
        };
    }
}