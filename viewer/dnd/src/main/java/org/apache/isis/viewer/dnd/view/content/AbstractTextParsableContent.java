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


package org.apache.isis.viewer.dnd.view.content;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.consent.Consent;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.title.TitleFacet;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.core.metamodel.spec.feature.ParseableEntryActionParameter;
import org.apache.isis.viewer.dnd.drawing.Image;


public abstract class AbstractTextParsableContent extends AbstractContent {

    public abstract void clear();

    public abstract void entryComplete();

    public Image getIconPicture(final int iconHeight) {
        return null;
    }

    public abstract boolean isEmpty();

    @Override
    public boolean isPersistable() {
        return false;
    }

    public boolean isTransient() {
        return false;
    }

    public abstract void parseTextEntry(final String entryText);

    public abstract Consent isEditable();

    @Override
    public boolean isTextParseable() {
        return true;
    }

    /**
     * @param propertyOrParamValue
     *            the target property or parameter
     * @param propertyOrParam
     *            the {@link ObjectAssociation} or {@link ParseableEntryActionParameter}
     * @param propertyOrParamTypeSpecification
     *            the specification of the type of the property or parameter (for fallback).
     */
    protected String titleString(
            final ObjectAdapter propertyOrParamValue,
            final FacetHolder propertyOrParam,
            final FacetHolder propertyOrParamTypeSpecification) {
        
        TitleFacet titleFacet = propertyOrParam.getFacet(TitleFacet.class);
        if (titleFacet != null) {
            return titleFacet.title(propertyOrParamValue, null);
        } else {
            return propertyOrParamValue.titleString();
        }
    }

}
