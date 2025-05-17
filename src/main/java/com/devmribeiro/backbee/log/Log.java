package com.devmribeiro.backbee.log;

import com.github.devmribeiro.zenlog.impl.Logger;

public class Log {
	private static final Logger logger = Logger.getLogger(Log.class);
	
	public static void t(Object msg) {
    	logger.t(msg);
    }

    public static void d(Object msg) {
    	logger.d(msg);
    }

    public static void i(Object msg) {
    	logger.i(msg);
    }

    public static void w(Object msg) {
    	logger.w(msg);
    }

    public static void e(Object msg) {
    	logger.e(msg);
	}

    public static void e(Object msg, Throwable t) { 
    	logger.e(msg, t);
	}

    public static void f(Object msg) {
    	logger.f(msg);
	}
}