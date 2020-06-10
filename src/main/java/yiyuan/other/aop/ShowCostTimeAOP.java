package yiyuan.other.aop;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ShowCostTimeAOP {

	private Long start;

	// @Pointcut("@annotation(yiyuan.other.annotation.TimeCost)")
	@Pointcut(value = "execution(* yiyuan..*ServiceImpl*.*(..))")
	public void showCostTime() {

	}

	/*
	 * @Around("showCostTime()") public void around(ProceedingJoinPoint jp) throws
	 * Throwable {
	 * 
	 * start=new Date().getTime(); jp.proceed(); end=new Date().getTime();
	 * System.out.println(jp.getSignature().getName()+" cost "+(end-start)/
	 * 1000+" 毫秒"); }
	 */

	@Before("showCostTime()")
	public void before() {

		start = new Date().getTime();
	}

	@After("showCostTime()")
	public void after(JoinPoint jp) {

		long cost = new Date().getTime() - start;
		    if (cost > 5)
			System.out.println(jp.getSignature().getName() + " cost " + cost + " 毫秒");
	}
}
