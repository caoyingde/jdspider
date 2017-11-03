package com.huilian.spider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class SpiderConstant {
	public static final String FIREFOX_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0";
	public static final String GOOGLE_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";
	public static final String IE_USER_AGENT = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)";

	public static final String[] USER_AGENTS = { FIREFOX_USER_AGENT, GOOGLE_USER_AGENT, IE_USER_AGENT };
	public static final Header[] REQUEST_HEADERS = {
			new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
			new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"), new BasicHeader("Accept-Encoding", "gzip, deflate"),
			new BasicHeader("Connection", "keep-alive"), new BasicHeader("Upgrade-Insecure-Requests", "1"), new BasicHeader("Cache-Control", "no-cache") };

	public static final int CONNECTION_TIMEOUT = 10 * 1000;// HTTP 连接超时设置（毫秒）
	
	/**
	 * 获取商品价格时每次发送多少个sku请求参数
	 */
	public static final int FETCH_PER_COUNT = 30;
	
	//public static final String ALIYUN_PUBLIC_IP = "112.74.48.145";
	
	public static final Map<String, String[]> URL_FORMAT_MAP = new HashMap<>();
	
//	public static final String MOBILE_URL_FORMAT = "http://search.jd.com/s_new.php?keyword=@kw@&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=@kw@&psort=3&cid2=653&cid3=655&page=@page@&s=@count@&click=0";
//	public static final String MOBILE_URL_FORMAT_PAGE = "http://search.jd.com/s_new.php?keyword=@kw@&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=@kw@&psort=3&cid2=653&cid3=655&page=@page@&s=@count@&scrolling=y&pos=30&tpl=3_M";
	
	public static final String COMMON_URL_FORMAT = "http://search.jd.com/Search?keyword=@kw@&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=@kw@&psort=3&stock=1&page=@page@&s=@count@&click=0&ev=exprice_100gt%40&wtype=1";
	//public static final String COMMON_URL_FORMAT = "http://search.jd.com/s_new.php?keyword=@kw@&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=@kw@&psort=3&stock=1&page=@page@&s=@count@&click=0";
	public static final String COMMON_URL_FORMAT_PAGE = "http://search.jd.com/s_new.php?keyword=@kw@&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=@kw@&psort=3&stock=1&page=@page@&s=@count@&scrolling=y&pos=30&ev=exprice_100gt%40&wtype=1";
	
	public static final String[] GOODS_CATEGORY = { "吸尘器", "取暖电器", "加湿器", "净化器", "饮水机", "电风扇", "冷风扇", "熨斗", "除湿器", "扫地机器人", "干衣机", "电饭煲", "多用途锅", "榨汁机", "豆浆机",
			"电磁炉", "微波炉", "电烤箱", "电热水壶", "咖啡机", "酸奶机", "电压力锅", "面包机", "电炖锅", "煮蛋器", "养生壶", "平板电视", "家庭影院", "空调", "冰箱", "洗衣机", "迷你音响", "灶具", "消毒柜", "热水器", "剃须刀",
			"电吹风", "美容器", "足浴盆", "血压计", "按摩器", "按摩椅", "笔记本", "台式机", "笔记本配件", "游戏本", "平板电脑", "平板电脑配件", "超极本", "CPU", "显卡", "内存", "主板", "散热器", "硬盘", "机箱", "显示器",
			"组装电脑", "键盘", "鼠标", "摄像头", "移动硬盘", "U盘", "外置盒", "游戏设备", "电玩", "手写板", "鼠标垫", "插座", "UPS电源", "路由器", "网卡", "交换机", "网络存储", "4G3G上网", "网络盒子", "打印机",
			"传真设备", "复合机", "多功能一体机", "扫描设备", "投影仪", "碎纸机", "考勤机", "点钞机", "保险柜", "手机", "对讲机", "电池移动电源", "充电器数据线", "手机耳机", "蓝牙耳机", "车载配件", "无线音响", "合约机",
			"数码相机", "单反相机", "摄像机", "镜头", "数码相框", "微单相机", "拍立得", "运动相机", "户外器材", "MP3MP4", "音箱音响", "耳机耳麦", "智能设备", "麦克风", "专业音频", "高清播放器", "苹果配件", "滤镜",
			"闪光灯手柄", "存储卡", "读卡器", "电池充电器", "移动电源", "智能手环", "智能手表", "智能眼镜", "运动跟踪器", "健康监测", "智能配饰", "智能家居", "体感车", "电子词典", "录音笔", "电纸书", "复读机", "点读机",
			"学生平板", "早教益智" };
	
	// 京东top100热销商品信息爬虫执行状态
	public volatile static boolean isJdTop100SpiderRunning = false;
	// 京东商品信息更新锁
	public static ReentrantLock lock = new ReentrantLock();
	// 自营商品免息计算任务是否在运行
	public volatile static boolean isSelfGoodsCalTaskRunning = false;
}
