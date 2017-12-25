package com.github.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

import net.sf.cglib.proxy.MethodProxy;

public class HelloHystrixCommond extends HystrixCommand<Object>
{
	Object proxy;
	MethodProxy method;
	Object[] args;
	protected HelloHystrixCommond(Object proxy, MethodProxy method, Object[] args)
	{
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(String.format("Group_%s", proxy.getClass().getName())))
				.andCommandKey(HystrixCommandKey.Factory.asKey(String.format("Commond_%s", method.getSuperName())))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(String.format("Pool_%s", method.getSuperName())))
				);
		this.proxy = proxy;
		this.method = method;
		this.args = args;
	}

	@Override
	protected Object run() throws Exception
	{
		try
		{
			return method.invokeSuper(proxy, args);
		} catch (Throwable e)
		{
			throw new Exception(e);
		}
	}

	@Override
	protected Object getFallback()
	{
		return super.getFallback();
	}
	

}
