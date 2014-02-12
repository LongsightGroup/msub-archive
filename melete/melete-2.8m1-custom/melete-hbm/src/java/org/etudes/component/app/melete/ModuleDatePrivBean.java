/**********************************************************************************
 *
 * $URL$ 
 * $Id$  
 ***********************************************************************************
 *
 * Copyright (c) 2008 Etudes, Inc.
 *
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007, 2008 Foothill College, ETUDES Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/
package org.etudes.component.app.melete;

import java.io.Serializable;
import java.util.List;

/** @author Hibernate CodeGenerator */
public class ModuleDatePrivBean extends ModuleDateBean implements Serializable {


    
    /** nullable persistent field */
    private org.etudes.component.app.melete.ModuleStudentPrivs moduleStudentPriv;
    private List sectionBeans;

    /** full constructor */
    public ModuleDatePrivBean(int moduleId, org.etudes.component.app.melete.Module module, org.etudes.component.app.melete.ModuleShdates moduleShdate, org.etudes.component.app.melete.ModuleStudentPrivs moduleStudentPriv) {
        this.moduleId = moduleId;
        this.module = module;
        this.moduleShdate = moduleShdate;
        this.moduleStudentPriv = moduleStudentPriv;
    }

    /** default constructor */
    public ModuleDatePrivBean() {
    }

    
    public org.etudes.component.app.melete.ModuleStudentPrivs getModuleStudentPriv() {
        return this.moduleStudentPriv;
    }

    public void setModuleStudentPriv(org.etudes.component.app.melete.ModuleStudentPrivs moduleStudentPriv) {
        this.moduleStudentPriv = moduleStudentPriv;
    }
    public List getSectionBeans() {
        return this.sectionBeans;
    }

    public void setSectionBeans(List sectionBeans) {
        this.sectionBeans = sectionBeans;
    }

    
}
