/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.mapper;

import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonAttributeMapperTest {

    @Test
    public void shouldGetAttributeWhenTypeAndValueGiven() throws Exception {
        PersonAttributeMapper mapper = new PersonAttributeMapper();

        PersonService mockPersonService = mock(PersonService.class);
        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setName("type");
        when(mockPersonService.getPersonAttributeTypeByName("type")).thenReturn(personAttributeType);

        Context context = new Context();
        ServiceContext serviceContext = ServiceContext.getInstance();
        serviceContext.setService(PersonService.class, mockPersonService);
        context.setServiceContext(serviceContext);

        PersonAttribute attribute = mapper.getAttribute("type", "value");

        assertEquals("type", attribute.getAttributeType().getName());
        assertEquals("value",attribute.getValue());

    }
}