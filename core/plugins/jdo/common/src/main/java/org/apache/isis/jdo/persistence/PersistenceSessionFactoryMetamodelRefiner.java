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
package org.apache.isis.jdo.persistence;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import org.apache.isis.applib.Identifier;
import org.apache.isis.commons.internal.collections._Lists;
import org.apache.isis.commons.internal.collections._Maps;
import org.apache.isis.metamodel.facetapi.MetaModelRefiner;
import org.apache.isis.metamodel.progmodel.ProgrammingModel;
import org.apache.isis.metamodel.spec.ObjectSpecId;
import org.apache.isis.metamodel.spec.ObjectSpecification;
import org.apache.isis.metamodel.specloader.validator.MetaModelValidatorVisiting;
import org.apache.isis.metamodel.specloader.validator.ValidationFailures;
import org.apache.isis.runtime.system.context.IsisContext;

import static org.apache.isis.commons.internal.base._NullSafe.stream;

import lombok.val;

@Component
public class PersistenceSessionFactoryMetamodelRefiner implements MetaModelRefiner {
    
    @Override
    public void refineProgrammingModel(ProgrammingModel programmingModel) {
        val configuration = IsisContext.getConfiguration();
        
        val shouldCheckUniqueObjTypes = configuration.getReflector().getValidator().isEnsureUniqueObjectTypes();
        if(!shouldCheckUniqueObjTypes) {
            return;
        }
        addValidatorToEnsureUniqueObjectIds(programmingModel);
        
        
        val shouldCheckModuleExtent = configuration.getReflector().getValidator().isCheckModuleExtent();
        if(!shouldCheckModuleExtent) {
            return;
        }
        addValidatorToCheckModuleExtent(programmingModel);
        
    }
    
    private void addValidatorToEnsureUniqueObjectIds(ProgrammingModel programmingModel) {

        final Map<ObjectSpecId, List<ObjectSpecification>> specsById = _Maps.newHashMap();

        MetaModelValidatorVisiting.SummarizingVisitor ensureUniqueObjectIds = new MetaModelValidatorVisiting.SummarizingVisitor(){

            @Override
            public boolean visit(ObjectSpecification objSpec, ValidationFailures validationFailures) {
                ObjectSpecId specId = objSpec.getSpecId();
                List<ObjectSpecification> objectSpecifications = specsById.get(specId);
                if(objectSpecifications == null) {
                    objectSpecifications = _Lists.newArrayList();
                    specsById.put(specId, objectSpecifications);
                }
                objectSpecifications.add(objSpec);
                return true;
            }

            @Override
            public void summarize(final ValidationFailures validationFailures) {
                for (final ObjectSpecId specId : specsById.keySet()) {
                    val specList = specsById.get(specId);
                    int numSpecs = specList.size();
                    if(numSpecs > 1) {
                        val csv = asCsv(specList);
                        validationFailures.add(
                                Identifier.classIdentifier(specId.asString()),
                                "Object type '%s' mapped to multiple classes: %s", 
                                specId.asString(), 
                                csv);
                    }
                }
            }

            private String asCsv(final List<ObjectSpecification> specList) {
                return stream(specList)
                        .map(ObjectSpecification.Functions.FULL_IDENTIFIER)
                        .collect(Collectors.joining(","));
            }

        };

        programmingModel.addValidator(ensureUniqueObjectIds);
    }
    
    private void addValidatorToCheckModuleExtent(ProgrammingModel programmingModel) {

        final Map<String, List<String>> domainObjectClassNamesByPackage = _Maps.newTreeMap();

        MetaModelValidatorVisiting.SummarizingVisitor visitor = new MetaModelValidatorVisiting.SummarizingVisitor(){

            @Override
            public boolean visit(ObjectSpecification objSpec, ValidationFailures validationFailures) {
                Class<?> correspondingClass = objSpec.getCorrespondingClass();
                if(correspondingClass == null) {
                    return true;
                }
                Package aPackage = correspondingClass.getPackage();
                if(aPackage == null) {
                    return true;
                }
                final String packageName = aPackage.getName();

                if (objSpec.isValue() || objSpec.isAbstract() || objSpec.isMixin() ||
                        objSpec.isParentedOrFreeCollection() ||
                        objSpec.getFullIdentifier().startsWith("java") ||
                        objSpec.getFullIdentifier().startsWith("org.joda") ||
                        objSpec.getFullIdentifier().startsWith("org.apache.isis")) {
                    // ignore
                } else {
                    List<String> classNames = domainObjectClassNamesByPackage.get(packageName);
                    if (classNames == null) {
                        classNames = _Lists.newArrayList();
                        domainObjectClassNamesByPackage.put(packageName, classNames);
                    }
                    classNames.add(objSpec.getFullIdentifier());
                }
                return true;
            }

            @Override
            public void summarize(final ValidationFailures validationFailures) {
                //FIXME[2112] module (legacy) specific, remove?
                //                final Set<String> modulePackageNames = modulePackageNamesFrom(appManifest);
                //
                //                final Set<String> domainObjectPackageNames = domainObjectClassNamesByPackage.keySet();
                //                for (final String pkg : domainObjectPackageNames) {
                //                    List<String> domainObjectClassNames = domainObjectClassNamesByPackage.get(pkg);
                //                    boolean withinSomeModule = isWithinSomeModule(modulePackageNames, pkg);
                //                    if(!withinSomeModule) {
                //                        String csv = stream(domainObjectClassNames)
                //                                .collect(Collectors.joining(","));
                //                        validationFailures.add(
                //                                "Domain objects discovered in package '%s' are not in the set of modules obtained from "
                //                                        + "the AppManifest's top-level module '%s'.  Classes are: %s",
                //                                        pkg, topLevelModule.getClass().getName(), csv);
                //                    }
                //                }
            }
            //FIXME[2112] module (legacy) specific, remove?
            //            private Set<String> modulePackageNamesFrom(final AppManifest appManifest) {
            //                final List<Class<?>> modules = appManifest.getModules();
            //                return modules.stream()
            //                        .map(aClass->aClass.getPackage().getName())
            //                        .collect(Collectors.toCollection(HashSet::new));
            //            }
            //
            //            private boolean isWithinSomeModule(final Set<String> modulePackageNames, final String pkg) {
            //                for (final String modulePackageName : modulePackageNames) {
            //                    if(pkg.startsWith(modulePackageName)) {
            //                        return true;
            //                    }
            //                }
            //                return false;
            //            }
        };

        programmingModel.addValidator(visitor);
    }
    
}