/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.fast.ibatis.model;
/**
 * <p>
 * the fast ibast configuration
 * </p>
 * @author trami
 * @version 1.0
 */
public class FastIbatisConfig {
	/** the ibatis xml file path. */
	private String xmlFilePath;
	/** the ibatis xml file name. */
	private String xmlFileName;
	/** the table model class name. */
	private String modelName;
	/** the file name. */
	private String fileName;
	/** the generated method name. */
	private String methodName;
	/** the method comment with javadoc format. */
	private String methodComment;
	/** need to generate dao,it must to generate. */
	private boolean isDAOGenerate;
	/** need to generate the service class,it is a choice. */
	private boolean isServiceGenerate;
	/** the method parameter type. */
	private String paramType;
	/** the method return type. */
	private String returnType;
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getMethodComment() {
		return methodComment;
	}
	public void setMethodComment(String methodComment) {
		this.methodComment = methodComment;
	}
	public boolean isDAOGenerate() {
		return isDAOGenerate;
	}
	public void setDAOGenerate(boolean isDAOGenerate) {
		this.isDAOGenerate = isDAOGenerate;
	}
	public boolean isServiceGenerate() {
		return isServiceGenerate;
	}
	public void setServiceGenerate(boolean isServiceGenerate) {
		this.isServiceGenerate = isServiceGenerate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getXmlFilePath() {
		return xmlFilePath;
	}
	public void setXmlFilePath(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
	}
	public String getXmlFileName() {
		return xmlFileName;
	}
	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}
	
}
