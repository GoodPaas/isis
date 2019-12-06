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
package org.apache.isis.runtime.system.persistence.events;

import javax.enterprise.event.Event;
import javax.inject.Named;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.isis.commons.internal.ioc.spring._Spring;

/**
 * 
 * @since 2.0
 *
 */
@Configuration
@Named("isisRuntime.PersistenceEventService_Spring")
public class PersistenceEventService_Spring {
    
    @Bean
    public Event<PreStoreEvent> preStoreEvents(ApplicationEventPublisher publisher) {
        return _Spring.event(publisher);
    }
    
    @Bean
    public Event<PostStoreEvent> postStoreEvents(ApplicationEventPublisher publisher) {
        return _Spring.event(publisher);
    }
    
}
