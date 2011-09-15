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
package net.sf.fast.ibatis.build.dao;

import net.sf.fast.ibatis.build.AbstractCodeBuilder;
import net.sf.fast.ibatis.model.FastIbatisConfig;
import net.sf.fast.ibatis.util.Utils;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.StringLiteral;
/**
 * <p>
 * generate the dao class with ibatis SQL Delete type.
 * </p>
 * @author trami
 * @version 1.0
 */
public class DAODeleteBuilder extends AbstractCodeBuilder {
	/**
	 * <p>
	 * create the delete code block.
	 * </p>
	 * @param ast the ast tree.
	 * 		  fc the configuration.
	 */
	@SuppressWarnings("unchecked")
	public Block createBlock(AST ast,FastIbatisConfig fc) {
		Block block = ast.newBlock();
		MethodInvocation methodInvocation = ast.newMethodInvocation();
		MethodInvocation m1 = ast.newMethodInvocation();
		m1.setName(ast.newSimpleName("getSqlMapClientTemplate"));
		methodInvocation.setExpression(m1);
		methodInvocation.setName(ast.newSimpleName("delete")); 
		ast.newSimpleName("param");
		StringLiteral literal = ast.newStringLiteral();
		literal.setLiteralValue(Utils.stripFileExtension(fc.getXmlFileName())+"."+fc.getMethodName());
		methodInvocation.arguments().add(literal);
		methodInvocation.arguments().add(ast.newSimpleName("param"));
		ReturnStatement rs = ast.newReturnStatement();
		rs.setExpression(methodInvocation);
		block.statements().add(rs);
		return block;
	}
	/**
	 * <p>
	 * Now we can generate 2 Categories
	 * </p>
	 * @return 1 = Service Category.
	 *         2 = DAO Category.
	 */
	public int getGenerateCategory() {
		return 2;
	}
}
