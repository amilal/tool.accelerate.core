/*
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/ 

package com.ibm.liberty.starter.gradle


import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MavenTask extends DefaultTask {
	String id = 'unknown'
	String version = '0.0.1'
	boolean hasProvided = false;
	boolean hasRuntime = false;
	boolean hasCompile = false;
	boolean hasServerSnippet = false;
	
    MavenTask() {
        setupUpToDateCheckDirectories()
    }
    
    def setupUpToDateCheckDirectories() {
        inputs.dir(new File(project.projectDir, 'repository'))
        outputs.dir(new File(project.buildDir, 'mavenRepository/artifacts'))
    }

	def getCommandLine() {
		def os = System.getProperty("os.name").toLowerCase()
		def mvnCommand = System.getProperty("mvnCommand", "mvn")
		def osSpecificCommandLine = null
		if (os.contains("windows")) {
    		osSpecificCommandLine = ['cmd', '/c', mvnCommand]
		} else {
    		osSpecificCommandLine = mvnCommand
		}
		return osSpecificCommandLine
	}
	
	def generateMavenInstallArgs = { fileName, artifactId, packaging ->
    [ 'install:install-file', 
      "-Dfile=" + project.projectDir.getAbsolutePath() + "/repository/" + version + "/" + fileName, 
      '-DgroupId=net.wasdev.wlp.starters.' + id, 
      '-DartifactId=' + artifactId, 
      '-Dversion=' + version, 
      '-Dpackaging=' + packaging, 
      "-DlocalRepositoryPath=" + project.buildDir.getAbsolutePath() + "/mavenRepository/artifacts", 
      '-DcreateChecksum=true']
	}
	
	@TaskAction
	def installPOM() {
		def cmd = getCommandLine()
		def provargs = generateMavenInstallArgs('provided-pom.xml', 'provided-pom', 'pom')
		def runargs = generateMavenInstallArgs('runtime-pom.xml', 'runtime-pom', 'pom')
		def comargs = generateMavenInstallArgs('compile-pom.xml', 'compile-pom', 'pom')
		def serverargs = generateMavenInstallArgs('server-snippet.xml', 'server-snippet', 'xml')
		
		if(hasProvided) {
			println "Installing provided POM"
			project.exec {	
				commandLine cmd
    			args provargs
			}
		}
		
		if(hasRuntime) {
			println "Installing runtime POM"
			project.exec {	
				commandLine cmd
    			args runargs
			}
		}
		if(hasCompile) {
            println "Installing compile POM"
            project.exec {  
                commandLine cmd
                args comargs
            }
        }
        if(hasServerSnippet) {
            println "Installing server snippet"
            project.exec {  
                commandLine cmd
                args serverargs
            }
        }
	}
	
}