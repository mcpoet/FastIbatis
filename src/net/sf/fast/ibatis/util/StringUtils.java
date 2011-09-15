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
package net.sf.fast.ibatis.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class with string utilities.
 */
public final class StringUtils {
	private static final Pattern SPLIT_PATTERN  = Pattern.compile("([A-Z]+)");
	private static final Pattern PREFIX_PATTERN = Pattern.compile("[^A-Z][\\sA-Z]");
	
	
	/**
	 * Converts first char to lower.
	 * 
	 * @param string the input string
	 * @return the modified string
	 */
	public static String firstToLower(String string) {
		if (string == null || string.length() == 0) {
			return "";
		}
		else if (string.length() == 1) {
			return string.toLowerCase();
		}
		
		return string.substring(0, 1).toLowerCase() + string.substring(1);
	}
	
	/**
	 * Converts first to upper.
	 * 
	 * @param string the input string
	 * @return the modified string
	 */
	public static String firstToUpper(String string) {
		if (string == null || string.length() == 0) {
			return "";
		}
		else if (string.length() == 1) {
			return string.toUpperCase();
		}
		
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
	
	/**
	 * Split string, where characters change from lower to upper case.
	 * All characters will be changed to lower case, despite of the
	 * first and sequences with more then one upper case letter.
	 * Example: getIDFromProdukt => get ID from produkt
	 * 
	 * @param string the input string
	 * @return the modified string
	 */
	public static String split(String string) {
		String[] strings = splitByUpperCase(string);
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < strings.length; ++i) {
			if (i > 0) {
				buffer.append(" ");
			}
			buffer.append(strings[i]);
		}
		return buffer.toString();
	}
	
	/**
	 * Split string, where characters change from lower to upper case.
	 * All characters will be changed to lower case, despite of the
	 * first and sequences with more then one upper case letter.
	 * Example: getIDFromProdukt => get ID from produkt
	 * 
	 * @param string the input string
	 * @return the resulting string array
	 */
	public static String[] splitByUpperCase(String string) {
		Matcher matcher = SPLIT_PATTERN.matcher(string); // "([A-Z]+)"
		
		int start = 0;
		List<String> list = new ArrayList<String>();
		while (matcher.find()) { 
			// change from lower to upper case -> append up to last lower case letter
			if (start < matcher.start()) {
				list.addAll(Arrays.asList(splitBySpace(string.substring(start, matcher.start()))));
			}
			
			// only one upper case letter?
			if (matcher.group(1).length() == 1) {
				// yes -> use as start of next string 
				start = matcher.start();
				continue;
			}
			
			// more then one upper case letter
			if (matcher.end(1) < string.length()) {
				// not at end of string -> append n-1 letters
				list.addAll(Arrays.asList(splitBySpace(string.substring(matcher.start(), matcher.end(1) - 1))));
				start = matcher.end(1) - 1;
			}
			else {
				// upper case letters up to end of string -> append all
				list.addAll(Arrays.asList(splitBySpace(string.substring(matcher.start()))));
				start = string.length();
			}
		}
		
		// and the rest...
		if (start < string.length()) {
			list.addAll(Arrays.asList(splitBySpace(string.substring(start))));
		}
		
		return (String[])list.toArray(new String[list.size()]);
	}
	
	/**
	 * Splits the given string by space.
	 * 
	 * @param string the string
	 * @return the resulting string array
	 */
	public static String[] splitBySpace(String string) {
		return string.split("\\s");
	}
	
	/**
	 * Checks for spaces in the given string.
	 * 
	 * @param string the string
	 * @return true, if spaces exists
	 */
	public static boolean hasSpaces(String string) {
		return string.indexOf(' ') >= 0;
	}
	
	/**
	 * Puts all strings to lower case.
	 * 
	 * @param strings the strings
	 * @param includeFirst false, if the first string should not be modified
	 * @return the resulting string array
	 */
	public static String[] allToLower(String[] strings, boolean includeFirst) {
		int start = includeFirst ? 0 : 1;
		for (int i = start; i < strings.length; ++i) {
			strings[i] = strings[i].toLowerCase();
		}
		return strings;
	}
	
	/**
	 * Gets the substring up to the first upper case letter.
	 * 
	 * @param string the string
	 * @return the prefix
	 */
	public static String getPrefix(String string) {
		Matcher matcher = PREFIX_PATTERN.matcher(string); // "[^A-Z][\sA-Z]"
		
		int start = 0;
		String prefix = null;
		if (matcher.find()) {
			start = matcher.start() + 1;
			prefix = string.substring(0, start).trim();
		}
		
		return prefix;
	}
	
	/**
	 * Gets the last element of the given path.
	 * 
	 * @param path the path
	 * @param separator the element separator
	 * @return the last element
	 */
	public static String getLastElement(String path, char separator) {
		int index = path.lastIndexOf(separator);
		if (index < 0) {
			return path;
		}
		
		if (index >= path.length() - 1) {
			return "";
		}
		
		return path.substring(index + 1);
	}
	
	/**
	 * Checks, if the string starts with the given regular expression.
	 * String.startsWith() does not work with regular expressions.
	 * 
	 * @param string the string
	 * @param regexp the regular expression
	 * @return true, if starts with
	 */
	public static boolean startsWith(String string, String regexp) {
		return Pattern.compile("^" + regexp).matcher(string).find();
	}
	
	/**
	 * Returns empty string in case of null.
	 * 
	 * @param string the string
	 * @return the string
	 */
	public static String checkNull(String string) {
		return string == null ? "" : string;
	}
	
	/**
	 * Infer the indentation level based on the given reference indentation
	 * and tab size.
	 * 
	 * @param tabSize the tab size
	 * @param reference the reference indentation
	 * @return the inferred indentation level
	 */
	public static int inferIndentationLevel(String reference, int tabSize) {
		StringBuffer expanded = new StringBuffer(StringUtils.expandTabs(reference, tabSize));

		int referenceWidth = expanded.length();
		if (tabSize == 0) {
			return referenceWidth;
		}

		int spaceWidth= 1;
		int level = referenceWidth / (tabSize * spaceWidth);
		if (referenceWidth % (tabSize * spaceWidth) > 0) {
			level++;
		}
		return level;
	}
	
	/**
	 * Expands the given string's tabs according to the given tab size.
	 * 
	 * @param string the string
	 * @param tabSize the tab size
	 * @return the expanded string
	 */
	public static String expandTabs(String string, int tabSize) {
		StringBuffer expanded = new StringBuffer();
		for (int i = 0, n = string.length(), chars = 0; i < n; i++) {
			char ch = string.charAt(i);
			if (ch == '\t') {
				for (; chars < tabSize; chars++) {
					expanded.append(' ');
				}
				chars = 0;
			} else {
				expanded.append(ch);
				chars++;
				if (chars >= tabSize) {
					chars = 0;
				}
			}
		}
		return expanded.toString();
	}
}
