package com.devmribeiro.backbee.dao;

import java.sql.Connection;
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

			ps = conn.prepareStatement(
					"select " +
					"	backup_id, " +
					"	backup_created_date " +
					" from " +
					"	backup " +
					" where " + 
					"	backup_created_date >= date_trunc('month', ?)::date and " +
					"	backup_created_date <= (date_trunc('month', ?) + interval '1 month' - interval '1 day')::date");

			ps.setObject(1, backupDate);
			ps.setObject(2, backupDate);

			System.out.println(ps);
		
			rs = ps.executeQuery();
			
			if (rs.next()) {
				BackupModel backupDTO;
				
				backupDTO = new BackupModel(rs.getInt("backup_id"), rs.getTimestamp("backup_created_date").toLocalDateTime());
				return backupDTO;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnFactory.close(rs, ps, conn);
		}
		return null;
	}
	
	public static boolean insert(LocalDateTime dateTime) {
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			
			conn = ConnFactory.open();
			ps = conn.prepareStatement("insert into backup (backup_created_date) values (?)");
			ps.setTimestamp(1, Timestamp.valueOf(dateTime));
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