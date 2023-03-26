package com.brevitaz;

import java.util.*;

public class AB  implements B{
	int i;

	public AB(int i) {
		this.i = i;
	}

	public int getI() {
		return i;
	}
	//	@Override
//	public void m1() {
//		A.super.m1();
//		System.out.println(A.i);
//
//	}


	@Override
	public boolean equals(Object obj) {
		return obj instanceof AB && this.i == ((AB) obj).getI();
	}

	public static void main(String[] args) {
//		A obj=new A(){
//			int k=1;
//			@Override
//			public void disp() {
//				System.out.println(k);
//			}
//		};
//		obj.disp();
//		List<String> names = new ArrayList<>();
//		names.add("Rams");
//		names.add("Posa");
//		names.add("Chinni");
//		names.add("Chinni222");
//
//		// Getting Spliterator
//		Spliterator<String> namesSpliterator = names.spliterator();
//		Spliterator<String> st= namesSpliterator.trySplit();
//		// Traversing elements
//		namesSpliterator.forEachRemaining(System.out::println);
//
//
//		System.out.println("___");
//		st.forEachRemaining(System.out::println);
//		System.out.println("___");

		AB a1=new AB(1);
		AB a2=new AB(1);
//		a2=a1;
//		AB a2=a1;
//		String a1=new String("hello");
//		String a2=new String("hello");
//		String a2=a1;

		System.out.println(a1.equals(a2));

		System.out.println(a1==a2);


	}
}
