package com.github.os72.protocjar.maven;

import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;

@RunWith(MavenJUnitTestRunner.class)
@MavenVersions({"3.2.3"})
public class MojoProtoFilesTest {
	@Rule
	public final TestResources resources = new TestResources();
	public final MavenRuntime maven;

	public MojoProtoFilesTest(MavenRuntimeBuilder mavenBuilder) throws Exception {
		this.maven = mavenBuilder.withCliOptions("-B", "-U", "-e").build();
	}

	@Test
	public void testProtoFilesParameter() throws Exception {
		File basedir = resources.getBasedir("proto-files-test");
		maven.forProject(basedir)
			.withCliOption("-Dprotobuf.version=3.21.6")
			.withCliOption("-Dprotoc.version=3.21.6")
			.execute("verify")
			.assertErrorFreeLog();
		
		// Verify that only the specified proto files were compiled
		File targetDir = new File(basedir, "target/generated-sources");
		
		// These files should be generated because they are specified in protoFiles
		File userProtoClass = new File(targetDir, "com/github/os72/protocjar/test/user/UserProto.java");
		File productProtoClass = new File(targetDir, "com/github/os72/protocjar/test/product/ProductProto.java");
		
		assertTrue("User proto should be compiled", userProtoClass.exists());
		assertTrue("Product proto should be compiled", productProtoClass.exists());
		
		// This file should NOT be generated because it's not in the protoFiles list
		File ignoredProtoClass = new File(targetDir, "com/github/os72/protocjar/test/ignore/IgnoreProto.java");
		assertFalse("Ignored proto should NOT be compiled", ignoredProtoClass.exists());
	}
}
