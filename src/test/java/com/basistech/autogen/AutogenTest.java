/*
* Copyright 2015 Basis Technology Corp.
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

import com.google.common.io.Files;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.python.core.PySystemState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class AutogenTest {

    @Test
    public void gen() throws Exception {
        PySystemState pySystemState = new PySystemState();
        Template template = new Template();
        template.setCode(new File("src/it/test-proj/src/main/templates/LanguageCode.java.tpl"));
        template.setData(new File("src/it/test-proj/src/main/data/bt_language_names.xml"));
        template.setPackageName("com.basistech.test");
        List<String> args = new ArrayList<>();
        args.add("autogen.py");
        args.add("-t");
        if (!template.getCode().exists()) {
            throw new MojoExecutionException(String.format("Template %s does not exist.", template.getCode().getAbsolutePath()));
        }
        args.add(template.getCode().getAbsolutePath());
        args.add("-o");
        File outputDir = Files.createTempDir();
        if (template.getPackageName() != null) {
            String dirTail = template.getPackageName().replaceAll("\\.", "/");
            outputDir = new File(outputDir, dirTail);
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        String outName = template.getCode().getName().replace(String.format(".%1s.tpl", "java"),
                String.format(".%1$s", "java"));
        File of = new File(outputDir, outName);


        args.add(of.getAbsolutePath());
        args.add(template.getData().getAbsolutePath());
        AutogenMojo.runAutogen(pySystemState, args.toArray(new String[args.size()]));
        assertTrue(new File(outputDir, "LanguageCode.java").exists());
    }
}
