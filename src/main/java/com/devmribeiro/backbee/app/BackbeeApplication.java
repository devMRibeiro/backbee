package com.devmribeiro.backbee.app;

import com.devmribeiro.backbee.service.BackupService;

public class BackbeeApplication {
	public static void main(String[] args) {
		new BackupService().backup();
	}
}