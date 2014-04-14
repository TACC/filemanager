
CREATE SEQUENCE portal.filemgr_notification_id_seq
   INCREMENT 1
   MINVALUE 1
   MAXVALUE 9223372036854775807;

CREATE SEQUENCE portal.filemgr_transfers_id_seq
   INCREMENT 1
   MINVALUE 1
   MAXVALUE 9223372036854775807;

CREATE SEQUENCE portal.filemgr_variables_id_seq
   INCREMENT 1
   MINVALUE 1
   MAXVALUE 9223372036854775807;


CREATE TABLE portal.filemgr_notifications (
   id                   integer NOT NULL DEFAULT
                        NEXTVAL('portal.filemgr_notification_id_seq'),
   transferId           integer NOT NULL default '0',
   notificiationType    text NOT NULL default 'EMAIL', check
                        (notificiationType IN
                        ('EMAIL','SMS','IM','TEXT','NONE'))  ,
   subject              varchar(255) default NULL,
   message              text,
   created              timestamp default NULL,
   deliveryDate                 timestamp default NULL,
   address              varchar(255) default NULL,
   username             varchar(255) default NULL,
   PRIMARY KEY  (id)
);

CREATE TABLE portal.filemgr_transfers (
   id           integer NOT NULL DEFAULT NEXTVAL('portal.filemgr_notification_id_seq'),
   epr          text,
   dn           varchar(255) NOT NULL default '',
   notified     integer NOT NULL default '0',
   created      timestamp NOT NULL default now(),
   task                 text NOT NULL,
   sourceURL    varchar(255) NOT NULL default 'file:////',
   destURL      varchar(255) NOT NULL default 'file:////',
   status       integer NOT NULL default '0',
   startTime    timestamp default NULL,
   stopTime     timestamp default NULL,
   file                 text NOT NULL,
   fName        varchar(255) NOT NULL default '',
   fDate        varchar(255) NOT NULL default '',
   fType        smallint NOT NULL default '0',
   fSize        integer NOT NULL default '0',
   fTime        varchar(255) NOT NULL default '',
   para                 integer NOT NULL default '1',
   paraId       integer NOT NULL default '1',
   speed        bigint default '0',
   progress     integer NOT NULL default '0',
   PRIMARY KEY  (id)
);

CREATE TABLE portal.filemgr_variables (
   id integer NOT NULL DEFAULT NEXTVAL('portal.filemgr_variables_id_seq'),
   resource varchar(128) NOT NULL default '',
   tg_home varchar(255) default '',
   tg_work varchar(255) default '',
   tg_scratch varchar(255) default '',
   PRIMARY KEY  (id)
)

CREATE TABLE portal.tgup_user_info
(
  person_id integer NOT NULL,
  username character varying(30) NOT NULL,
  first_name character varying(100) NOT NULL,
  middle_name character varying(60),
  last_name character varying(100) NOT NULL,
  organization character varying(100),
  department character varying(300),
  "position" character varying(100),
  email character varying(200) NOT NULL,
  street1 character varying(250),
  street2 character varying(250),
  city character varying(100),
  state character varying(100),
  zipcode character varying(45),
  country character varying(100),
  home_phone_number character varying(50),
  home_phone_extension character varying(25),
  home_phone_comments character varying(250),
  bus_phone_number character varying(50),
  bus_phone_extension character varying(50),
  bus_phone_comments character varying(250),
  fax_number character varying(50),
  CONSTRAINT user_info_pkey PRIMARY KEY (person_id)
);

CREATE TABLE portal.dn
(
  person_id integer NOT NULL,
  username character(30) NOT NULL,
  dn character(2000) NOT NULL,
  state text,
  CONSTRAINT dns_primary_key PRIMARY KEY (person_id, username, dn)
);

CREATE TABLE portal.accounts
(
  request_id integer NOT NULL,
  grant_number character(200),
  proposal_number character(20),
  project_title character(300),
  fos_id integer,
  fos character(200),
  account_id integer,
  charge_number character(200),
  allocation_id integer,
  start_date date,
  end_date date,
  base_allocation numeric,
  remaining_allocation numeric,
  alloc_type character(200),
  alloc_resource_id integer,
  alloc_resource_name character(200),
  proj_state character(17),
  proj_state_comments text,
  proj_state_ts timestamp with time zone,
  pi_person_id integer,
  pi_last_name character(100),
  pi_first_name character(100),
  person_id integer,
  first_name character(100),
  last_name character(100),
  is_pi boolean,
  used_allocation numeric,
  acct_state character(32),
  acct_state_ts timestamp with time zone,
  id SERIAL,
  CONSTRAINT accounts_pkey PRIMARY KEY (id)
);

CREATE OR REPLACE VIEW portal.user_info AS
SELECT DISTINCT p.person_id, p.username, p.first_name, p.middle_name, p.last_name, p.organization, p.department, p."position", e.email_address AS email, a.street1, a.street2, a.city, a.state, a.zipcode, a.country, hn.number AS home_phone_number, hn.extension AS home_phone_extension, hn.comments AS home_phone_comments, bn.number AS bus_phone_number, bn.extension AS bus_phone_extension, bn.comments AS bus_phone_comments, fn.number AS fax_number
   FROM portal.tgup_user_info

CREATE OR REPLACE VIEW portal.dns AS
SELECT DISTINCT d.person_id, d.username, d.dn, d.state  FROM portal.dn d;
ALTER TABLE portal.dns
  OWNER TO dooley;
  
CREATE OR REPLACE VIEW portal.acctv AS
SELECT DISTINCT p.request_id, p.grant_number, p.proposal_number, p.project_title, p.fos_id, p.fos, p.account_id, p.charge_number, p.allocation_id, p.start_date, p.end_date, p.base_allocation, p.remaining_allocation, p.alloc_type, p.alloc_resource_id, p.alloc_resource_name, p.proj_state, p.proj_state_comments, p.proj_state_ts, p.pi_person_id, p.pi_last_name, p.pi_first_name, p.person_id, p.first_name, p.last_name, p.is_pi, p.used_allocation, p.acct_state, p.acct_state_ts  
FROM portal.accounts p;
ALTER TABLE portal.acctv
  OWNER TO dooley;
  
CREATE TABLE portal.sav
(
  person_id integer,
  tg_username character(30),
  username character(30),
  resource_name character(200),
  is_active boolean,
  id SERIAL,
  CONSTRAINT sav_pkey PRIMARY KEY (id)
);
  

INSERT INTO portal.tgup_user_info(
            person_id, username, first_name, middle_name, last_name, 
            organization, department, "position", email, street1, street2, 
            city, state, zipcode, country, home_phone_number, home_phone_extension, 
            home_phone_comments, bus_phone_number, bus_phone_extension, bus_phone_comments, 
            fax_number)
VALUES 
	(934,'rdoole1','Rion',NULL,'Dooley','University of Texas at Austin','TACC','University Non-Research Staff','dooley@tacc.utexas.edu','Research Office Complex 1.101','10100 Burnet Road R8700 Bldg 196','Austin','Texas','78758','United States','512-475-7886',NULL,NULL,'512-475-9445', NULL, NULL,NULL),
	(99,'maytal','Maytal',NULL,'Dahan','Texas Advanced Computing Center',NULL,'Center Non-Researcher Staff','maytal@tacc.utexas.edu','10100 Burnet Road','R8700','Austin','Texas','78758-4497','United States',NULL,NULL,NULL,'310-429-9419',NULL,NULL,NULL);
			
INSERT INTO portal.dn(
            person_id, username, dn, state)
VALUES 
    (934,'rdoole1','/C=US/O=National Center for Supercomputing Applications/CN=Rion Dooley','active'),
    (99,'maytal','/DC=EDU/DC=TENNESSEE/DC=NICS/O=National Institute for Computational Sciences/CN=Maytal Dahan','active'),
	(99,'maytal','/C=US/O=Pittsburgh Supercomputing Center/OU=PSC Kerberos Certification Authority/CN=maytal/UID=maytal/emailAddress=maytal@PSC.EDU','inactive'),
	(99,'maytal','/C=US/O=Pittsburgh Supercomputing Center/OU=PSC Kerberos Certification Authority/CN=maytal/USERID=maytal/Email=maytal@PSC.EDU','inactive'),
	(99,'maytal','/C=US/O=Pittsburgh Supercomputing Center/OU=PSC Kerberos Certification Authority/CN=maytal/USERID=maytal/EMAIL=maytal@PSC.EDU','inactive'),
	(99,'maytal','/C=US/O=Pittsburgh Supercomputing Center/OU=PSC Kerberos Certification Authority/CN=maytal/UID=maytal/Email=maytal@PSC.EDU','inactive'),
	(99,'maytal','/C=US/O=Pittsburgh Supercomputing Center/OU=PSC Kerberos Certification Authority/CN=maytal/UID=maytal/EMAIL=maytal@PSC.EDU','inactive'),
	(99,'maytal','/C=US/O=Pittsburgh Supercomputing Center/OU=PSC Kerberos Certification Authority/CN=maytal/UID=maytal/E=maytal@PSC.EDU','inactive'),
	(99,'maytal','/DC=EDU/DC=UTEXAS/DC=TACC/O=UT-AUSTIN/O=TACC Classic CA/CN=Maytal Dahan','active'),
	(99,'maytal','/DC=EDU/DC=UTEXAS/DC=TACC/O=UT-AUSTIN/O=TACC MICS CA/CN=Maytal Dahan','active'),
	(99,'maytal','/C=US/O=UTAustin/OU=TACC/CN=Maytal Dahan/USERID=maytal','inactive'),
	(99,'maytal','/C=US/O=National Center for Supercomputing Applications/OU=People/CN=Maytal Dahan','active'),
	(99,'maytal','/C=US/O=Pittsburgh Supercomputing Center/CN=Maytal Dahan','active'),
	(99,'maytal','/C=US/O=UTAustin/OU=TACC/CN=Maytal Dahan/UID=maytal','active'),
	(99,'maytal','/C=US/O=National Center for Supercomputing Applications/CN=Maytal Dahan','active');


INSERT INTO portal.accounts(
	request_id, grant_number, proposal_number, project_title, fos_id, fos, account_id, charge_number, allocation_id, start_date, end_date, base_allocation, remaining_allocation, alloc_type, alloc_resource_id, alloc_resource_name, proj_state, proj_state_comments, proj_state_ts, pi_person_id, pi_last_name, pi_first_name, person_id, first_name, last_name, is_pi, used_allocation, acct_state, acct_state_ts) 
VALUES 
	(45,'ASC040019T','ASC040021','TeraGrid: Cactus Computational Toolkit',49,'Advanced Scientific Computing',45,'TG-ASC040019T',45,'2004-02-03','2005-02-28',30000,28695.43848222259334488888692476000000000000000,'new',1,'teragrid','inactive','inactivated by NCSA','2007-02-19 08:51:18.327683-08',361,'Allen','Gabrielle',934,'Rion','Dooley','f',0.0,'inactive','2007-02-19 08:51:18.327683-08'),
	(840,'STA060001N','STA090002','TG Staff Project: Portal Test Project',153,'Center Systems Staff',669,'TG-STA060001N',887,'2006-01-18','2015-12-31',25000,21152.68203822222222224973746000000000000000000,'new',2761,'staff.teragrid','inactive','As per ticket 207086','2011-10-18 13:12:22.189676-07',99,'Dahan','Maytal',934,'Rion','Dooley','f',4.537575,'inactive','2011-10-18 13:12:22.189676-07'),
	(1775,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',1983,'2006-05-19','2007-05-02',10000,-61922.5774001,'new',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'inactive','2011-10-09 16:15:51.693374-07'),
	(3072,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',3615,'2007-05-03','2008-05-04',30000,29209.30177962393158375416470,'renewal',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'inactive','2011-10-09 16:15:51.693374-07'),
	(4410,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',5301,'2008-05-05','2009-05-31',350000,182948.43291851282046958244728,'renewal',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'inactive','2011-10-09 16:15:51.693374-07'),
	(6464,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',7934,'2009-06-01','2010-06-01',100000,43371.65056352564102582498158,'renewal',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'inactive','2011-10-09 16:15:51.693374-07'),
	(8125,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',11467,'2010-06-02','2011-10-02',800000,387140.49232060000592776218,'renewal',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',934,'Rion','Dooley','f',431.42934773333337216106,'inactive','2011-10-09 16:15:51.693374-07'),
	(11390,'MCB110022','MCB110022','Enabling Large-scale Computational Genomics',64,'Molecular Biosciences',6256,'TG-MCB110022',18283,'2011-11-12','2012-06-30',83829,69275.0,'transfer',2792,'trestles.sdsc.teragrid','active','','2013-04-12 09:16:02.932232-07',13669,'Vaughn','Matthew',934,'Rion','Dooley','f',0.0,'active','2013-10-24 05:16:05.420499-07'),
	(12538,'MCB110022','MCB110022','The iPlant Collaborative: Enabling Large-scale Computational Genomics',84,'Integrative Biology and Neuroscience',6256,'TG-MCB110022',20716,'2012-07-01','2013-09-30',400944,97482.0,'renewal',2792,'trestles.sdsc.teragrid','active','','2013-04-12 09:16:02.932232-07',13669,'Vaughn','Matthew',934,'Rion','Dooley','f',0.0,'active','2013-10-24 05:16:05.420499-07'),
	(13716,'MCB110022','MCB110022','The iPlant Collaborative: Enabling Large-scale Computational Genomics',84,'Integrative Biology and Neuroscience',6256,'TG-MCB110022',22914,'2013-01-07','2013-09-30',600104,-970540.0230,'transfer',2801,'stampede.tacc.xsede','active','','2013-09-18 20:30:23.012568-07',13669,'Vaughn','Matthew',934,'Rion','Dooley','f',0.0,'active','2013-10-24 05:16:12.973031-07'),
	(15210,'MCB110022','MCB110022','The iPlant Collaborative: Enabling Large-scale Computational Genomics',67,'Genetics and Nucleic Acids',6256,'TG-MCB110022',25743,'2013-10-01','2014-09-30',3255548,3187209.4240,'renewal',2801,'stampede.tacc.xsede','active','','2013-09-18 20:30:23.012568-07',13669,'Vaughn','Matthew',934,'Rion','Dooley','f',6.1760,'active','2013-10-24 05:16:12.973031-07'),
	(15210,'MCB110022','MCB110022','The iPlant Collaborative: Enabling Large-scale Computational Genomics',67,'Genetics and Nucleic Acids',6256,'TG-MCB110022',25742,'2013-10-01','2014-09-30',466035,301633.0,'renewal',2792,'trestles.sdsc.teragrid','active','','2013-04-12 09:16:02.932232-07',13669,'Vaughn','Matthew',934,'Rion','Dooley','f',0.0,'active','2013-10-24 05:16:05.420499-07'),
	(15210,'MCB110022','MCB110022','The iPlant Collaborative: Enabling Large-scale Computational Genomics',67,'Genetics and Nucleic Acids',6256,'TG-MCB110022',25745,'2013-10-01','2014-09-30',500,500,'renewal',2803,'oasis.sdsc.xsede','active','','2013-09-18 20:30:39.640364-07',13669,'Vaughn','Matthew',934,'Rion','Dooley','f',0.0,'active','2013-10-24 05:16:26.025168-07'),
	(15210,'MCB110022','MCB110022','The iPlant Collaborative: Enabling Large-scale Computational Genomics',67,'Genetics and Nucleic Acids',6256,'TG-MCB110022',25744,'2013-10-01','2014-09-30',500,487.1818904317915,'renewal',2804,'ranch.tacc.xsede','active','','2013-09-18 20:30:31.814414-07',13669,'Vaughn','Matthew',934,'Rion','Dooley','f',0.0,'active','2013-10-24 05:16:20.791091-07'),
	(10911,'STA110012S','STA110012','XSEDE 1.3 User Services',153,'Center Systems Staff',6807,'TG-STA110012S',17440,'2011-09-20','2012-09-20',300000,298556.249064,'new',2761,'staff.teragrid','active','','2011-09-19 09:46:39.161277-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'active','2011-10-12 09:47:08.285617-07'),
	(12811,'STA110012S','STA110012','XSEDE 1.3 User Services',153,'Center Systems Staff',6807,'TG-STA110012S',21315,'2012-09-21','2013-09-20',300000,-18159.128133,'renewal',2761,'staff.teragrid','active','','2011-09-19 09:46:39.161277-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'active','2011-10-12 09:47:08.285617-07'),
	(15316,'STA110012S','STA110012','XSEDE 1.3 User Services',153,'Center Systems Staff',6807,'TG-STA110012S',26054,'2013-09-21','2014-09-20',300000,299994.737640,'renewal',2761,'staff.teragrid','active','','2011-09-19 09:46:39.161277-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'active','2011-10-12 09:47:08.285617-07'),
	(10912,'STA110019S','STA110019','XSEDE SP TACC',153,'Center Systems Staff',6808,'TG-STA110019S',17441,'2011-09-20','2012-09-20',300000,207653.661060,'new',2761,'staff.teragrid','active','','2011-09-19 09:46:47.895645-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'active','2013-01-30 13:47:56.090948-08'),
	(12812,'STA110019S','STA110019','XSEDE SP TACC',153,'Center Systems Staff',6808,'TG-STA110019S',21316,'2012-09-21','2013-09-20',300000,210663.5732872,'renewal',2761,'staff.teragrid','active','','2011-09-19 09:46:47.895645-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'active','2013-01-30 13:47:56.090948-08'),
	(15317,'STA110019S','STA110019','XSEDE SP TACC',153,'Center Systems Staff',6808,'TG-STA110019S',26055,'2013-09-21','2014-09-20',300000,143951.057925,'renewal',2761,'staff.teragrid','active','','2011-09-19 09:46:47.895645-07',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'active','2013-01-30 13:47:56.090948-08'),
	(13511,'TRA120009','TRA120009',' Education allocation for TACC Visualization and Data Management Training',154,'Training',7112,'TG-TRA120009',22709,'2013-01-07','2014-01-04',100000,37511.5500,'transfer',2801,'stampede.tacc.xsede','inactive','project has expired','2014-01-05 18:01:25.703152-08',336,'Hempel','Chris',934,'Rion','Dooley','f',6.5600,'inactive','2014-01-05 18:01:25.703152-08'),
	(11679,'TRA120007','TRA120007','Education allocation for TACC HPC Training',154,'Training',7115,'TG-TRA120007',18935,'2012-01-04','2013-01-04',30000,28986.7990,'new',2781,'longhorn.tacc.teragrid','inactive','project has expired','2014-01-05 18:01:25.703152-08',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'inactive','2014-01-05 18:01:25.703152-08'),
	(13442,'TRA120007','TRA120007','Education allocation for TACC HPC Training',154,'Training',7115,'TG-TRA120007',22547,'2013-01-05','2014-01-04',100000,100000,'renewal',2781,'longhorn.tacc.teragrid','inactive','project has expired','2014-01-05 18:01:25.703152-08',336,'Hempel','Chris',934,'Rion','Dooley','f',0.0,'inactive','2014-01-05 18:01:25.703152-08'),
	
	(11,'STA040001N',NULL,'TG Work Group: User Services',153,'Center Systems Staff',11,'TG-STA040001N',11,'2003-12-16','2013-12-31',99999,-349509.93857551977783244105627858400000000000000,'new',1,'teragrid','inactive','project is out of funds','2007-03-07 12:05:28.349665-08',69,'Towns','John',99,'Maytal','Dahan','f',0.0013755555555555555555541800000000000,'inactive','2007-03-07 12:05:28.349665-08'),
	(168,'STA040025N',NULL,'TG Staff Project: TACC',153,'Center Systems Staff',168,'TG-STA040025N',178,'2004-10-01','2013-12-31',99999,72175.48827509511111111118548000000000000000000,'new',2761,'staff.teragrid','inactive','deactivated by TeraGrid GIG','2007-03-09 09:28:19.572988-08',831,'Boisseau','John R.',99,'Maytal','Dahan','f',0.464000,'inactive','2007-03-09 09:28:19.572988-08'),
	(840,'STA060001N','STA090002','TG Staff Project: Portal Test Project',153,'Center Systems Staff',669,'TG-STA060001N',887,'2006-01-18','2015-12-31',25000,21152.68203822222222224973746000000000000000000,'new',2761,'staff.teragrid','inactive','As per ticket 207086','2011-10-18 13:12:22.189676-07',99,'Dahan','Maytal',99,'Maytal','Dahan','t',42.50759277777777775026254000000000000000000,'inactive','2011-10-18 13:12:22.189676-07'),
	(1775,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',1983,'2006-05-19','2007-05-02',10000,-61922.5774001,'new',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'inactive','2011-10-09 16:15:51.693374-07'),
	(3072,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',3615,'2007-05-03','2008-05-04',30000,29209.30177962393158375416470,'renewal',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'inactive','2011-10-09 16:15:51.693374-07'),
	(4410,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',5301,'2008-05-05','2009-05-31',350000,182948.43291851282046958244728,'renewal',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',429.049136,'inactive','2011-10-09 16:15:51.693374-07'),
	(6464,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',7934,'2009-06-01','2010-06-01',100000,43371.65056352564102582498158,'renewal',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',163.746188,'inactive','2011-10-09 16:15:51.693374-07'),
	(8125,'STA060015N','STA090004','TG RP TACC',153,'Center Systems Staff',2055,'TG-STA060015N',11467,'2010-06-02','2011-10-02',800000,387140.49232060000592776218,'renewal',2761,'staff.teragrid','inactive','project has expired','2011-10-09 16:15:51.693374-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'inactive','2011-10-09 16:15:51.693374-07'),
	(10911,'STA110012S','STA110012','XSEDE 1.3 User Services',153,'Center Systems Staff',6807,'TG-STA110012S',17440,'2011-09-20','2012-09-20',300000,298556.249064,'new',2761,'staff.teragrid','active',NULL,'2011-09-19 09:46:39.161277-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'active','2011-09-19 11:46:46.493822-07'),
	(12811,'STA110012S','STA110012','XSEDE 1.3 User Services',153,'Center Systems Staff',6807,'TG-STA110012S',21315,'2012-09-21','2013-09-20',300000,-18159.128133,'renewal',2761,'staff.teragrid','active',NULL,'2011-09-19 09:46:39.161277-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'active','2011-09-19 11:46:46.493822-07'),
	(15316,'STA110012S','STA110012','XSEDE 1.3 User Services',153,'Center Systems Staff',6807,'TG-STA110012S',26054,'2013-09-21','2014-09-20',300000,299994.737640,'renewal',2761,'staff.teragrid','active',NULL,'2011-09-19 09:46:39.161277-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',2.612232,'active','2011-09-19 11:46:46.493822-07'),
	(10912,'STA110019S','STA110019','XSEDE SP TACC',153,'Center Systems Staff',6808,'TG-STA110019S',17441,'2011-09-20','2012-09-20',300000,207653.661060,'new',2761,'staff.teragrid','active',NULL,'2011-09-19 09:46:47.895645-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'active','2011-10-07 11:46:45.609356-07'),
	(12812,'STA110019S','STA110019','XSEDE SP TACC',153,'Center Systems Staff',6808,'TG-STA110019S',21316,'2012-09-21','2013-09-20',300000,210663.5732872,'renewal',2761,'staff.teragrid','active',NULL,'2011-09-19 09:46:47.895645-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'active','2011-10-07 11:46:45.609356-07'),
	(15317,'STA110019S','STA110019','XSEDE SP TACC',153,'Center Systems Staff',6808,'TG-STA110019S',26055,'2013-09-21','2014-09-20',300000,143951.057925,'renewal',2761,'staff.teragrid','active',NULL,'2011-09-19 09:46:47.895645-07',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'active','2011-10-07 11:46:45.609356-07'),
	(11679,'TRA120007','TRA120007','Education allocation for TACC HPC Training',154,'Training',7115,'TG-TRA120007',18935,'2012-01-04','2013-01-04',30000,28986.7990,'new',2781,'longhorn.tacc.teragrid','inactive','project has expired','2014-01-05 18:01:25.703152-08',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'inactive','2014-01-05 18:01:25.703152-08'),
	(13442,'TRA120007','TRA120007','Education allocation for TACC HPC Training',154,'Training',7115,'TG-TRA120007',22547,'2013-01-05','2014-01-04',100000,100000,'renewal',2781,'longhorn.tacc.teragrid','inactive','project has expired','2014-01-05 18:01:25.703152-08',336,'Hempel','Chris',99,'Maytal','Dahan','f',0.0,'inactive','2014-01-05 18:01:25.703152-08');