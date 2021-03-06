/*
* Copyright 2014 Basis Technology Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.basistech.autogen;

import java.io.File;

/**
 * Individual item to be processed by autogen.
 */
public class Template {
    private File code;
    private File data;
    private String packageName;
    
    public File getCode() {
        return code;
    }
    public void setCode(File code) {
        this.code = code;
    }
    public File getData() {
        return data;
    }
    public void setData(File data) {
        this.data = data;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getPackageName() {
        return packageName;
    }

}
