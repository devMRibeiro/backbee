package com.devmribeiro.backbee.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.db.utility.ConnFactory;
import com.devmribeiro.backbee.model.BackupModel;

public class BackupDao {
	public static BackupModel get(LocalDate backupDate) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = ConnFactory.open();

			ps = conn.prepareStatement("select * from backup where backup_created_date = ?");
			ps.setDate(1, Date.valueOf(backupDate));
			System.out.println(ps);
		
			rs = ps.executeQuery();
			
			if (rs.next()) {
				BackupModel backup = new BackupModel();
				backup.setBackupId(rs.getInt("backup_id"));
				backup.setBackupCreatedDate((LocalDateTime) rs.getObject("backup_created_date"));
				return backup;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnFactory.close(rs, ps, conn);
		}
		return null;
	}
	
	public static boolean insert(BackupModel backup) {
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			
			conn = ConnFactory.open();
			ps = conn.prepareStatement("insert into backup (backup_created_date) values (?)");
			ps.setTimestamp(1, Timestamp.valueOf(backup.getBackupCreatedDate()));
			System.out.println(ps);
			
			if (ps.executeUpdate() > 0) {
				conn.commit();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnFactory.close(ps, conn);
		}
		return false;
	}
}