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
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 * @description Generate java code with python autogen.
 * @goal generate
 * @phase generate-sources
 */
@SuppressWarnings("PMD")
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

    public AutogenMojo() {
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
            args.add(template.getCode().getAbsolutePath());
            args.add("-o");
            String dirTail = template.getPackageName().replaceAll("\\.", "/");
            String outName = template.getCode().getName().replace(".java.tpl", ".java");
            File of = new File(outputDirectory, dirTail);
            if (!of.exists()) {
                of.mkdirs();
            }
            of = new File(of, outName);
            args.add(of.getAbsolutePath());
            args.add(template.getData().getAbsolutePath());
            getLog().info("Running: " + args);
            runAutogen(args.toArray(new String[args.size()]));
        }

    }

    private void runAutogen(String[] args) {
        PythonInterpreter.initialize(PySystemState.getBaseProperties(), null, args);
        PythonInterpreter pi = new PythonInterpreter();
        InputStreamReader genReader = new InputStreamReader(getClass().getResourceAsStream("/autogen.py"),
                                                            UTF8);
        PyCode autogenCode = pi.compile(genReader);
        pi.exec(autogenCode);
    }
}
