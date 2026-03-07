/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.shrclient.util;


import org.openmrs.api.context.Context;
import org.openmrs.module.shrclient.dao.IdMappingRepository;
import org.openmrs.module.shrclient.dao.FacilityCatchmentRepository;
import org.openmrs.module.shrclient.identity.IdentityStore;

import java.util.List;

public class PlatformUtil {
    public static <T> T getRegisteredComponent(Class<T> clazz) {
        List<T> registeredComponents = Context.getRegisteredComponents(clazz);
        if (!registeredComponents.isEmpty()) {
            return registeredComponents.get(0);
        }
        return null;
    }

    public static <T> T getRegisteredComponent(String beanName, Class<T> clazz) {
        return Context.getRegisteredComponent(beanName, clazz);
    }

    public static PropertiesReader getPropertiesReader() {
        return getRegisteredComponent(PropertiesReader.class);
    }

    public static IdMappingRepository getIdMappingsRepository() {
        return getRegisteredComponent(IdMappingRepository.class);
    }

    public static FacilityCatchmentRepository getFacilityCatchmentRepository() {
        return getRegisteredComponent(FacilityCatchmentRepository.class);
    }

    public static IdentityStore getIdentityStore() {
        return getRegisteredComponent(IdentityStore.class);
    }
}
