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
package net.sf.fast.ibatis.build.service;

import java.util.Map;

import net.sf.fast.ibatis.build.AbstractCodeBuilder;
import net.sf.fast.ibatis.model.FastIbatisConfig;
import net.sf.fast.ibatis.util.Utils;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
/**
 * <p>
 * generate the service class of getting record count,
 * dao as a memeber to fulfil the function.
 * </p>
 * @author trami
 * @version 1.0
 */
public class ServiceIntegerBuilder extends AbstractCodeBuilder {
	/**
	 * <p>
	 * create the integer return code block with dao invoked.
	 * mainly used to get record count from specific table.
	 * </p>
	 * @param ast the ast tree.
	 * 		  fc the configuration.
	 */
	@SuppressWarnings("unchecked")
	public Block createBlock(AST ast,FastIbatisConfig fc) {
		Block block = ast.newBlock();
		MethodInvocation methodInvocation = ast.newMethodInvocation();
		methodInvocation.setExpression(ast.newName(Utils.lowFirstChar(fc.getModelName())+"DAO"));
		methodInvocation.setName(ast.newSimpleName(fc.getMethodName())); 
		methodInvocation.arguments().add(ast.newSimpleName("param"));
		ReturnStatement rs = ast.newReturnStatement();
		rs.setExpression(methodInvocation);
		block.statements().add(rs);
		return block;
	}
	
	public Map<String,String> generateCode(FastIbatisConfig fc) {		
		return super.generateCode(fc);
	}

	/**
	 * <p>
	 * Now we can generate 2 Categories
	 * </p>
	 * @return 1 = Service Category.
	 *         2 = DAO Category.
	 */
	public int getGenerateCategory() {
		return 1;
	}
}
