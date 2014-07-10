/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.metamodel.facets.properties.interaction;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.isis.applib.services.eventbus.AbstractInteractionEvent;
import org.apache.isis.applib.services.eventbus.PropertyInteractionEvent;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.MultiTypedFacet;
import org.apache.isis.core.metamodel.facets.InteractionHelper;
import org.apache.isis.core.metamodel.facets.SingleValueFacetAbstract;
import org.apache.isis.core.metamodel.facets.propcoll.accessor.PropertyOrCollectionAccessorFacet;
import org.apache.isis.core.metamodel.facets.properties.update.clear.PropertyClearFacet;
import org.apache.isis.core.metamodel.runtimecontext.ServicesInjector;

public abstract class InteractionWithPropertyClearFacetAbstract
        extends SingleValueFacetAbstract<Class<? extends PropertyInteractionEvent<?,?>>>
        implements InteractionWithPropertyClearFacet, MultiTypedFacet {


    public static Class<? extends Facet> type() {
        return PropertyClearFacet.class;
    }

    private final PropertyOrCollectionAccessorFacet getterFacet;
    private final PropertyClearFacet clearFacet;
    private final InteractionHelper interactionHelper;

    public InteractionWithPropertyClearFacetAbstract(
            final Class<? extends PropertyInteractionEvent<?, ?>> eventType,
            final PropertyOrCollectionAccessorFacet getterFacet,
            final PropertyClearFacet clearFacet,
            final ServicesInjector servicesInjector,
            final FacetHolder holder) {
        super(type(), eventType, holder);
        this.getterFacet = getterFacet;
        this.clearFacet = clearFacet;
        this.interactionHelper = new InteractionHelper(servicesInjector, AbstractInteractionEvent.Mode.EXECUTE);
    }

    @Override
    public void clearProperty(ObjectAdapter targetAdapter) {
        if(clearFacet == null) {
            return;
        }

        if(!interactionHelper.hasEventBusService()) {
            clearFacet.clearProperty(targetAdapter);
            return;
        }

        final Object oldValue = getterFacet.getProperty(targetAdapter);
        clearFacet.clearProperty(targetAdapter);
        final Object newValue = getterFacet.getProperty(targetAdapter);

        if(Objects.equal(oldValue, newValue)) {
            // do nothing.
            return;
        }

        interactionHelper.postEventForPropertyClear(value(), targetAdapter, getIdentified(), oldValue, newValue);
    }



    // //////////////////////////////////////
    // MultiTypedFacet
    // //////////////////////////////////////

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Facet>[] facetTypes() {
        return Lists.newArrayList(
                type(),
                InteractionWithPropertyClearFacet.class
        ).toArray(new Class[]{});
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Facet> T getFacet(Class<T> facet) {
        return (T) this;
    }


}
