package com.xpos.common.searcher;

import com.xpos.common.entity.example.Example;

public abstract class AbstractSearcher<T> {

	protected Example<?> example;
	
	public abstract Example<?> getExample();
	
}
