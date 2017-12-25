package com.github.hystrix;

public class HelloService
{
	public String method1(String arg1)
	{
		double r = Math.random();
		if(r >= 0.0)
		{
			throw new RuntimeException("an error");
		}
		return String.format("Service Method:%s, Args: %s", "method1", arg1);
	}
	
	public String method2(String arg1, String arg2)
	{
		double r = Math.random();
		if(r > 0.5)
		{
			throw new RuntimeException("an error");
		}
		return String.format("Service Method:%s, Arg1: %s %s", "method2", arg1,  arg2);
	}
	
	public String method3(String arg1, String arg2, String arg3)
	{
		double r = Math.random();
		if(r >= 0.6)
		{
			throw new RuntimeException("an error");
		}
		return String.format("Service Method:%s, Args: %s %s %s", "method3", arg1,  arg2, arg3);
	}
}
