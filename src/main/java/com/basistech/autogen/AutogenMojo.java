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
