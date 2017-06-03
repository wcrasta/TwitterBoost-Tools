package com.sl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Unfollow {
	public static void main(String[] args) throws InterruptedException {
		List<Long> neverUnfollow = new ArrayList<Long>();
		List<Long> neverUnfollow2 = new ArrayList<Long>();
		for (long i = 0; i <= 201; i++) {
			neverUnfollow.add(i);
		}
		
		for (int i = 0; i < neverUnfollow.size(); i += 100) {
			List<Long> sub = neverUnfollow.subList(i, Math.min(neverUnfollow.size(),i+100));
			neverUnfollow2.addAll(sub);
			neverUnfollow2.add((long) 6969);
		}
		
		System.out.println(neverUnfollow.toString());
		System.out.println(neverUnfollow2.toString());
		
		
		
		
	}
}
