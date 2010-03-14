/******************************************************************************
 ** This data and information is proprietary to, and a valuable trade secret
 ** of, Basis Technology Corp.  It is given in confidence by Basis Technology
 ** and may only be used as permitted under the license agreement under which
 ** it has been distributed, and in no other way.
 **
 ** Copyright (c) 2010 Basis Technology Corporation All rights reserved.
 **
 ** The technical data and information provided herein are provided with
 ** `limited rights', and the computer software provided herein is provided
 ** with `restricted rights' as those terms are defined in DAR and ASPR
 ** 7-104.9(a).
 ******************************************************************************/

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
