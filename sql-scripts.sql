-- Create table
create table backup (
	backup_id serial primary key,
	backup_created_date timestamp
)

-- Restart sequence column and DELETE ALL DATA
truncate backup restart identity

-- Reset auto increment with anyId
ALTER SEQUENCE backup_backup_id_seq RESTART WITH anyId;