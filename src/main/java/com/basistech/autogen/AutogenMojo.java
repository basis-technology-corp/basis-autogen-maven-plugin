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
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.python.core.PyCode;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 * @description Generate java code with python autogen.
 * @goal generate
 * @phase generate-sources
 */
public class AutogenMojo extends AbstractMojo {
    private static final Charset UTF8 = Charset.forName("utf-8");
    /**
     * Destination directory
     * 
     * @parameter expression="${autogen.targetDir}"
     *            default-value="${project.build.directory}/generated-sources/"
     */
    private File outputDirectory;

    /**
     * The templates to process. Each one is specified as code, data, package.
     * 
     * @parameter
     * @required
     */
    private List templates;

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * @parameter expression="${autogen.language}" default-value="java"
     */
    private String language = "java";
    
    private PySystemState pySystemState;

    public AutogenMojo() {
        pySystemState = new PySystemState();
    }

    /** {@inheritDoc} */
    public void execute() throws MojoExecutionException, MojoFailureException {

        File f = outputDirectory;

        if (!f.exists()) {
            f.mkdirs();
        }
        if (project != null && outputDirectory != null && outputDirectory.exists()) {
            project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
        }

        for (Object tObject : templates) {
            Template template = (Template) tObject;
            List<String> args = new ArrayList<String>();
            args.add("autogen.py");
            args.add("-t");
            if (!template.getCode().exists()) {
                throw new MojoExecutionException(String.format("Template %s does not exist.", template.getCode().getAbsolutePath()));
            }
            args.add(template.getCode().getAbsolutePath());
            args.add("-o");
            File outputDir = outputDirectory;
            if (template.getPackageName() != null) {
                String dirTail = template.getPackageName().replaceAll("\\.", "/");
                outputDir = new File(outputDir, dirTail);
            }
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            String outName = template.getCode().getName().replace(String.format(".%1s.tpl", language),
                    String.format(".%1$s", language));
            File of = new File(outputDir, outName);


            args.add(of.getAbsolutePath());
            args.add(template.getData().getAbsolutePath());
            getLog().info("Running: " + args);
            runAutogen(args.toArray(new String[args.size()]));
        }

    }

    private void runAutogen(String[] args) {
        PyList argv = new PyList();
        if (args != null) {
            for (String arg : args) {
                argv.append(new PyString(arg));
            }
        }
        pySystemState.argv = argv;
        PythonInterpreter pi = new PythonInterpreter(new PyStringMap(), pySystemState);
        InputStreamReader genReader = new InputStreamReader(getClass().getResourceAsStream("/autogen.py"),
                                                            UTF8);
        PyCode autogenCode = pi.compile(genReader);
        pi.exec(autogenCode);
    }
}
