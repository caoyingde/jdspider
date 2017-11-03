package com.huilian.spider;

import java.math.BigDecimal;

import com.huilian.spider.model.JdGoodsPricePOJO;
import com.huilian.spider.util.BigDecimalUtil;

public class InterestCalStrategy {

	/**
	 * 计算商品售价、是否免手续费、免息期数、现金券金额
	 * @param entity
	 * @return
	 */
	public static void cal(JdGoodsPricePOJO entity){
		BigDecimal price = entity.getPrice();
		
		// 京东价小于0，商品是下架状态
		if(BigDecimalUtil.lessThan(price, BigDecimal.ZERO)){
			entity.setSellPrice(BigDecimal.valueOf(-1));
			entity.setChargeFree(0);
			entity.setPeriods(0);
			entity.setCouponAmount(BigDecimal.ZERO);
			entity.setSellStatus(0);
			return;
		}
		
		// 减价策略
		price = price.subtract(BizConstant.JD_PRICE_DERATE);
		
		BigDecimal supplyPrice = entity.getSupplyPrice();
		if(supplyPrice == null){
			return;
		}
		else if(BigDecimalUtil.lessThan(supplyPrice,BizConstant.ON_SELL_PRICE_MIN)){
			entity.setSellPrice(BigDecimal.valueOf(-1));
			entity.setChargeFree(0);
			entity.setPeriods(0);
			entity.setCouponAmount(BigDecimal.ZERO);
			entity.setSellStatus(0);
			return;
		}
		else{
			entity.setSellStatus(1);
		}
		BigDecimal dif = price.subtract(supplyPrice);
		// 百分比，四舍五入，保留4位小数
		BigDecimal difRatio = price.subtract(supplyPrice).divide(price,4, BigDecimal.ROUND_HALF_UP);
		// (1-2) dif <0 或者 0<=dif<10
		if (BigDecimalUtil.lessThan(dif, BigDecimal.ZERO)
				|| (BigDecimalUtil.greaterOrEqual(dif, BigDecimal.ZERO) && BigDecimalUtil.lessThan(dif, BigDecimal.valueOf(10)))) {
			BigDecimal sellPrice = supplyPrice.add(supplyPrice.multiply(BizConstant.LEVEL_BASE));
			entity.setSellPrice(sellPrice);
			entity.setChargeFree(1);
			entity.setPeriods(3);//免3期
			entity.setCouponAmount(BigDecimal.ZERO);
		}
		// (3) 差价比例 < 3%（差价比例=（京东价-供货价）/京东价）
		else if(BigDecimalUtil.lessThan(difRatio, BizConstant.LEVEL_1)){
			entity.setSellPrice(price);
			entity.setChargeFree(0);
			entity.setPeriods(0);
			entity.setCouponAmount(dif);
		}
		// (4) 3%<=差价比例 <5.25%
		else if (BigDecimalUtil.greaterOrEqual(difRatio, BizConstant.LEVEL_1)
				&& BigDecimalUtil.lessThan(difRatio, BizConstant.LEVEL_2)) {
			entity.setSellPrice(price);
			entity.setChargeFree(1);
			entity.setPeriods(0);
			BigDecimal amount = dif.subtract(price.multiply(BizConstant.LEVEL_1));
			entity.setCouponAmount(amount);
		}
		// (5) 5.25%<=差价比例<7.5%
		else if (BigDecimalUtil.greaterOrEqual(difRatio, BizConstant.LEVEL_2)
				&& BigDecimalUtil.lessThan(difRatio, BizConstant.LEVEL_3)) {
			entity.setSellPrice(price);
			entity.setChargeFree(1);
			entity.setPeriods(3);
			BigDecimal amount = dif.subtract(price.multiply(BizConstant.LEVEL_2));
			entity.setCouponAmount(amount);
		}
		// (6) 7.5%<=差价比例<9.75%
		else if (BigDecimalUtil.greaterOrEqual(difRatio, BizConstant.LEVEL_3)
				&& BigDecimalUtil.lessThan(difRatio, BizConstant.LEVEL_4)) {
			entity.setSellPrice(price);
			entity.setChargeFree(1);
			entity.setPeriods(6);
			BigDecimal amount = dif.subtract(price.multiply(BizConstant.LEVEL_3));
			entity.setCouponAmount(amount);
		}
		// (7) 9.75%<=差价比例<12%
		else if (BigDecimalUtil.greaterOrEqual(difRatio, BizConstant.LEVEL_4)
				&& BigDecimalUtil.lessThan(difRatio, BizConstant.LEVEL_5)) {
			entity.setSellPrice(price);
			entity.setChargeFree(1);
			entity.setPeriods(9);
			BigDecimal amount = dif.subtract(price.multiply(BizConstant.LEVEL_4));
			entity.setCouponAmount(amount);
		}
		// (8) 差价比例>=12%
		else if (BigDecimalUtil.greaterOrEqual(difRatio, BizConstant.LEVEL_5)) {
			entity.setSellPrice(price);
			entity.setChargeFree(1);
			entity.setPeriods(12);
			BigDecimal amount = dif.subtract(price.multiply(BizConstant.LEVEL_5));
			entity.setCouponAmount(amount);
		}
		
		// 售价与返现金额精确度处理
		BigDecimal sellPrice = entity.getSellPrice();
		BigDecimal couponAmount = entity.getCouponAmount();
		if(sellPrice != null){
			// 售价小数点向上取整
			entity.setSellPrice(sellPrice.setScale(0, BigDecimal.ROUND_CEILING));
		}
		if(couponAmount != null){
			// 返还给客户的现金券向下取整（以10为最小计数单位），不足10元的不予返现
			BigDecimal re = couponAmount.divide(BizConstant.COUPON_NO_PAYMENT).setScale(0,BigDecimal.ROUND_FLOOR).multiply(BizConstant.COUPON_NO_PAYMENT);
			entity.setCouponAmount(re);
		}
	}
}
