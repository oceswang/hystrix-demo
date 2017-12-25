package com.github.hystrix;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandMetrics.HealthCounts;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class HelloDemo
{
	int processors = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(processors, processors, 1, TimeUnit.HOURS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
    HelloService service = null;
	public static void main(String[] args)
	{
		new HelloDemo().startDemo();
	}
	
	public void startDemo()
	{
		initService();
		startMetricsMonitor();
		while(true)
		{
			startRequestOnThread();
		}
	}
	public void initService()
	{
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(HelloService.class);
		enhancer.setCallback(new MethodInterceptor() {
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
			{
				return new HelloHystrixCommond(obj, proxy, args).execute();
			}
		});

		this.service = (HelloService) enhancer.create();
	}

	public void startMetricsMonitor()
	{
		Thread t = new Thread(new Runnable() {
			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(5000);
					} catch (Exception e)
					{
					}

					Collection<HystrixCommandMetrics> coll = HystrixCommandMetrics.getInstances();
					StringBuilder out = new StringBuilder();
					out.append("\n");
					out.append("#####################################################################################").append("\n");
					for(HystrixCommandMetrics metrics : coll)
					{
						out.append("# " + getStatsStringFromMetrics(metrics)).append("\n");
					}
					out.append("#####################################################################################").append("\n");
					System.out.println(out.toString());
				}

			}

			private String getStatsStringFromMetrics(HystrixCommandMetrics metrics)
			{
				StringBuilder m = new StringBuilder();
				if (metrics != null)
				{
					HealthCounts health = metrics.getHealthCounts();
					m.append("Commond: ").append(metrics.getCommandKey()).append(" ");
					m.append("Requests: ").append(health.getTotalRequests()).append(" ");
					m.append("Errors: ").append(health.getErrorCount()).append(" (").append(health.getErrorPercentage()).append("%)   ");
					m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
					m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
					m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
					m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
				}
				return m.toString();
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	public void startRequestOnThread()
	{
		pool.execute(new Runnable(){

			public void run()
			{
				try
				{
					try
					{
						service.method1("arg1");
					} catch (Exception e)
					{
					}
					try
					{
						service.method2("arg1","arg2");
					} catch (Exception e)
					{
					}
					try
					{
						service.method3("arg1","arg2","srg3");
					} catch (Exception e)
					{
					}
				} catch (Exception e)
				{
					
				}
			}
			
		});
	}
}
