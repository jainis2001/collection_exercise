package com.brevitaz;

public interface A {
	// comment
	int i=10;
	default void m1(){
		System.out.println("A");
	}
	void disp();
}


