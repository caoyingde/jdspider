package com.huilian.spider.service;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huilian.hlej.base.mybatis.SqlSessionFactoryUtil;
import com.huilian.spider.dao.BizMapper;

/**
 * 业务顶级抽象类，带事务的服务需继承该类
 * @author zyx
 *
 */
public abstract class BizService {
	private ThreadLocal<SqlSession> localSession = new ThreadLocal<SqlSession>();
	private Logger logger = LoggerFactory.getLogger(getLoggerClass());

	/**
	 * 日志输出时是从哪个类产生的
	 */
	protected abstract Class<? extends BizService> getLoggerClass();

	/**
	 * 获取哪个mapper接口
	 * @return
	 */
	protected abstract Class<? extends BizMapper> getMapperClass();

	/**
	 * mybatis mapper的接口
	 */
	protected BizMapper getMapper() {
		SqlSession session = localSession.get();
		if(session == null){
			session = SqlSessionFactoryUtil.getSqlSessionFactory().openSession();
		}
		BizMapper mapper = session.getMapper(getMapperClass());
		localSession.set(session);
		return mapper;
	}

	protected void errorLog(String msg, Throwable t) {
		logger.error(msg, t);
	}

	protected void closeSession() {
		SqlSession session = localSession.get();
		if (session != null) {
			session.close();
		}
		localSession.remove();
	}

	protected void commit() {
		SqlSession session = localSession.get();
		if (session != null) {
			session.commit();
		}
	}

	protected void rollback() {
		SqlSession session = localSession.get();
		if (session != null) {
			session.rollback();
		}
	}
}
