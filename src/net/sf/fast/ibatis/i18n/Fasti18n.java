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
package net.sf.fast.ibatis.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>
 * The i18n resource reader.
 * </p>
 * 
 * @author dan.zheng
 * @version 1.0
 */
public class Fasti18n {
	private static final String BASE_NAME = "net/sf/fast/ibatis/i18n/message";
	private static class I18nGenerator {
		static Locale locale = Locale.getDefault();
		static ResourceBundle rb_zh_CN = ResourceBundle.getBundle(BASE_NAME, Locale.CHINESE);
		static ResourceBundle rb_en_US = ResourceBundle.getBundle(BASE_NAME, Locale.US);
		static ResourceBundle rb_default = ResourceBundle.getBundle(BASE_NAME, locale);
	}
	
	
	public static String getString(String key) {
		Locale locale = I18nGenerator.locale;
		if (locale != null && locale.getCountry().equals("CN")) {
			return I18nGenerator.rb_zh_CN.getString(key);
		} else if (locale != null && locale.getCountry().equals("US")) {
			return I18nGenerator.rb_en_US.getString(key);
		} else {
			return I18nGenerator.rb_default.getString(key);
		}
	}
}
