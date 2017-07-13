package com.breeze.init;

public class InitSorter implements Comparable<InitSorter> {
	
	public Initable initer;
	public InitSorter(Initable i){
		this.initer = i;
	}

	
	public int compareTo(InitSorter o) {
		return  this.initer.getInitOrder() - o.initer.getInitOrder();
	}

}
