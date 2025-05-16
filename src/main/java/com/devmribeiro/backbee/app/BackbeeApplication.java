package com.devmribeiro.backbee.app;

import com.devmribeiro.backbee.impl.BackbeeImpl;

public class BackbeeApplication {
	public static void main(String[] args) {
		new BackbeeImpl().backup();
	}
}