** Create Sequence

CREATE SEQUENCE filemgr_notification_id_seq
   INCREMENT 1
   MINVALUE 1
   MAXVALUE 9223372036854775807;

CREATE SEQUENCE filemgr_transfers_id_seq
   INCREMENT 1
   MINVALUE 1
   MAXVALUE 9223372036854775807;

CREATE SEQUENCE filemgr_variables_id_seq
   INCREMENT 1
   MINVALUE 1
   MAXVALUE 9223372036854775807;

** Create Table

CREATE TABLE filemgr_notifications (
   id                   integer NOT NULL DEFAULT
                        NEXTVAL('filemgr_notification_id_seq'),
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

CREATE TABLE filemgr_transfers (
   id           integer NOT NULL DEFAULT NEXTVAL('filemgr_notification_id_seq'),
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

CREATE TABLE filemgr_variables (
   id integer NOT NULL DEFAULT NEXTVAL('filemgr_variables_id_seq'),
   resource varchar(128) NOT NULL default '',
   tg_home varchar(255) default '',
   tg_work varchar(255) default '',
   tg_scratch varchar(255) default '',
   PRIMARY KEY  (id)
)
