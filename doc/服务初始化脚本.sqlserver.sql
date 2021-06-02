-- 修改以下表中, page_id列的长度到3000
alter table bpm_ru_procinst alter column page_id varchar(3000)
alter table bpm_hi_procinst alter column page_id varchar(3000)
alter table bpm_ru_task alter column page_id varchar(3000)
alter table bpm_hi_task alter column page_id varchar(3000)
