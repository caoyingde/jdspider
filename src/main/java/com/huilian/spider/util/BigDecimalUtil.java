package com.huilian.spider.util;

import java.math.BigDecimal;

public class BigDecimalUtil{
	/**
	 * value1 < value2
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean lessThan(BigDecimal value1, BigDecimal value2){
		if(value1.compareTo(value2) == -1){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * value1 = value2
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean equal(BigDecimal value1, BigDecimal value2){
		if(value1.compareTo(value2) == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * value1 >= value2
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean greaterOrEqual(BigDecimal value1, BigDecimal value2){
		if(value1.compareTo(value2) == 0 || value1.compareTo(value2) == 1){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * value1 > value2
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean greaterThan(BigDecimal value1, BigDecimal value2){
		if(value1.compareTo(value2) == 1){
			return true;
		}
		else{
			return false;
		}
	}
}
