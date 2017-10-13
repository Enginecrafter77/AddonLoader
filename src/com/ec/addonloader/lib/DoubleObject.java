package com.ec.addonloader.lib;

/**
 * Simple class to pass two objects in one instance.
 * @author Enginecrafter77
 * @param <F> The first object type.
 * @param <S> The second object type.
 */
public class DoubleObject<F, S>{
	
	private final F first;
	private final S second;
	
	/**
	 * Constructs DoubleObject from 2 objects, obviously.
	 * @param first The first object of type F.
	 * @param second The second object of type S.
	 */
	public DoubleObject(F first, S second)
	{
		this.first = first;
		this.second = second;
	}
	
	/**
	 * @return First object of type F.
	 */
	public F getFirst()
	{
		return first;
	}
	
	/**
	 * @return Second object of type F.
	 */
	public S getSecond()
	{
		return second;
	}
	
}
