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

package org.apache.isis.core.security.authentication;

import java.util.Optional;

import org.apache.isis.core.commons.internal.debug._Probe;
import org.apache.isis.core.commons.internal.exceptions._Exceptions;

/**
 * @since 2.0
 */
public interface AuthenticationSessionTracker {

    Optional<AuthenticationSession> currentAuthenticationSession();
    
    default AuthenticationSession getAuthenticationSessionElseFail() {
        return currentAuthenticationSession()
                .orElseThrow(()->
                    _Exceptions.illegalState(
                            "no AuthenticationSession available with current %s", 
                            _Probe.currentThreadId()));
    }
    
    default Optional<MessageBroker> currentMessageBroker() {
        return currentAuthenticationSession().map(AuthenticationSession::getMessageBroker);
    }
    
    default MessageBroker getMessageBrokerElseFail() {
        return getAuthenticationSessionElseFail().getMessageBroker();
    }
    
    
}
