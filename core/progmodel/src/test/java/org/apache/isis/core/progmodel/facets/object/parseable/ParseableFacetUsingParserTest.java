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
package org.apache.isis.core.progmodel.facets.object.parseable;

import java.util.IllegalFormatWidthException;

import org.apache.isis.applib.adapters.Localization;
import org.apache.isis.applib.adapters.Parser;
import org.apache.isis.applib.adapters.ParsingException;
import org.apache.isis.core.commons.authentication.AuthenticationSessionProvider;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.map.AdapterMap;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.parseable.TextEntryParseException;
import org.apache.isis.core.metamodel.facets.object.value.ValueFacet;
import org.apache.isis.core.metamodel.runtimecontext.DependencyInjector;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

public class ParseableFacetUsingParserTest {
    
    protected Mockery mockery = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    private FacetHolder mockFacetHolder;
    private AuthenticationSessionProvider mockAuthenticationSessionProvider;
    private DependencyInjector mockDependencyInjector;
    private AdapterMap mockAdapterManager;

    private ParseableFacetUsingParser parseableFacetUsingParser;

//    private ObjectAdapter mockAdapter;

//    private ObjectSpecification mockSpecification;

    @Before
    public void setUp() throws Exception {
        
        mockFacetHolder = mockery.mock(FacetHolder.class);
        mockDependencyInjector = mockery.mock(DependencyInjector.class);
        mockAdapterManager = mockery.mock(AdapterMap.class);
        mockAuthenticationSessionProvider = mockery.mock(AuthenticationSessionProvider.class);


        mockery.checking(new Expectations(){{
            never(mockAuthenticationSessionProvider);
            never(mockAdapterManager);

            allowing(mockFacetHolder).containsFacet(ValueFacet.class);
            will(returnValue(Boolean.FALSE));

           allowing(mockDependencyInjector).injectDependenciesInto(with(any(Object.class)));
        }});

        Parser parser = new Parser<String>() {
            public String parseTextEntry(Object contextPojo, String entry) {
                if (entry.equals("invalid")) {
                    throw new ParsingException();
                }
                if (entry.equals("number")) {
                    throw new NumberFormatException();
                }
                if (entry.equals("format")) {
                    throw new IllegalFormatWidthException(2);
                }
                return entry.toUpperCase();
            }

            public int typicalLength() {
                return 0;
            }

            public String displayTitleOf(String object, Localization localization) {
                return null;
            }

            public String displayTitleOf(String object, String usingMask) {
                return null;
            }

            public String parseableTitleOf(String existing) {
                return null;
            }
        };            
        parseableFacetUsingParser = new ParseableFacetUsingParser(parser, mockFacetHolder, mockAuthenticationSessionProvider, mockDependencyInjector, mockAdapterManager);
        
      //  mockAdapter = mockery.mock(ObjectAdapter.class);
      //  mockSpecification = mockery.mock(ObjectSpecification.class);
    }

    @Test
    public void testParseNormalEntry() throws Exception {
        // TODO why is this so complicated to check!!!
        /*
        final AuthenticationSession session = mockery.mock(AuthenticationSession.class);
        
        mockery.checking(new Expectations(){{
            one(mockAdapterManager).adapterFor("XXX");
            will(returnValue(mockAdapter));
            
            one(mockAdapter).getSpecification();
            will(returnValue(mockSpecification));
            
            one(mockAuthenticationSessionProvider).getAuthenticationSession();
            will(returnValue(session));
            
            allowing(mockSpecification).createValidityInteractionContext(session, InteractionInvocationMethod.BY_USER, mockAdapter);
        }});
        ObjectAdapter adapter = parseableFacetUsingParser.parseTextEntry(null, "xxx");
        
        adapter.getObject();
        */
    }
    

    @Test(expected=TextEntryParseException.class)
    public void parsingExceptionRethrown() throws Exception {
        parseableFacetUsingParser.parseTextEntry(null, "invalid");
    }

    @Test(expected=TextEntryParseException.class)
    public void numberFormatExceptionRethrown() throws Exception {
        parseableFacetUsingParser.parseTextEntry(null, "number");
    }

    @Test(expected=TextEntryParseException.class)
    public void illegalFormatExceptionRethrown() throws Exception {
        parseableFacetUsingParser.parseTextEntry(null, "format");
    }
}

