package com.devmribeiro.backbee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.db.utility.impl.ResUtil;
import com.devmribeiro.backbee.model.BackupModel;
import com.github.devmribeiro.zenlog.impl.Logger;

public class BackupDao {
	
	private static final Logger log = new Logger(BackupDao.class);
	
	public static BackupModel get(LocalDate backupDate) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = ResUtil.open();

			ps = conn.prepareStatement(
					"select " +
					"	backup_id, " +
					"	backup_created_date " +
					" from " +
					"	backup " +
					" where " + 
					"	backup_created_date >= date_trunc('month', ?)::date and " +
					"	backup_created_date < (date_trunc('month', ?) + interval '1 month')::date");

			ps.setObject(1, backupDate);
			ps.setObject(2, backupDate);

			log.i(ps);
			
			rs = ps.executeQuery();
			
			return rs.next()
					? new BackupModel(rs.getInt("backup_id"), rs.getTimestamp("backup_created_date").toLocalDateTime())
					: null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResUtil.close(rs, ps, conn);
		}
		return null;
	}
	
	public static boolean insert(LocalDateTime dateTime) {
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			
			conn = ResUtil.open();
			ps = conn.prepareStatement("insert into backup (backup_created_date) values (?)");
			ps.setTimestamp(1, Timestamp.valueOf(dateTime));

			log.i(ps);

			if (ps.executeUpdate() > 0) {
				conn.commit();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResUtil.close(ps, conn);
		}
		return false;
	}

	public static void truncate() {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = ResUtil.open();
			ps = conn.prepareStatement("truncate backup restart identity");
			log.i(ps);

			ps.executeUpdate(); 
			conn.commit();

		} catch (Exception e) {
			log.e("Error when on delete", e);
		} finally {
			ResUtil.close(ps, conn);
		}
	}
}