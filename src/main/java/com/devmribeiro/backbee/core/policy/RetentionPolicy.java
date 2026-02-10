package com.devmribeiro.backbee.core.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.devmribeiro.backbee.core.model.BackupInfo;

public class RetentionPolicy {
	public List<Integer> indexesToRemove(List<BackupInfo> backups, int max) {
	    if (backups.size() <= max)
	        return Collections.emptyList();

	    class IndexedBackup {
	        int index;
	        BackupInfo backup;

	        IndexedBackup(int index, BackupInfo backup) {
	            this.index = index;
	            this.backup = backup;
	        }
	    }

	    List<IndexedBackup> list = new ArrayList<IndexedBackup>();

	    for (int i = 0; i < backups.size(); i++)
	        list.add(new IndexedBackup(i, backups.get(i)));

	    Collections.sort(list, new Comparator<IndexedBackup>() {
	        @Override
	        public int compare(IndexedBackup a, IndexedBackup b) {
	            return a.backup.createdAt().compareTo(b.backup.createdAt());
	        }
	    });

	    int toRemove = backups.size() - max;
	    List<Integer> result = new ArrayList<Integer>();

	    for (int i = 0; i < toRemove; i++)
	        result.add(list.get(i).index);

	    return result;
	}
}