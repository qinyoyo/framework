/*第一次执行脚本需要先单独执行以下注释中脚本*/
CREATE USER "IOTEQU"
     identified by "iotequ"
     default tablespace users
     temporary tablespace temp
     profile DEFAULT;
GRANT connect,resource,dba to "IOTEQU";
GRANT unlimited tablespace to "IOTEQU";
CREATE OR REPLACE
	PROCEDURE "IOTEQU"."REMOVE_OBJECT"(TYP VARCHAR , OBJ VARCHAR , OWN VARCHAR) 
	IS
		num NUMBER;
	BEGIN
	  SELECT count(1) INTO num FROM ALL_OBJECTS  WHERE OBJECT_NAME = OBJ AND OBJECT_TYPE = TYP AND OWNER = OWN;
		IF num > 0 THEN
	    EXECUTE IMMEDIATE 'DROP '||typ||' "'||obj||'"';
	  END IF;
	END;
/
CREATE OR REPLACE
	PROCEDURE "IOTEQU"."CALC_SEQUENCE"(tab varchar2,pk varchar2) AS
	n number(10);
	m number(10);
	BEGIN
 		execute immediate 'select SEQUENCE_'||tab||'.nextval from dual' into n;
 		execute immediate 'select max('||pk||') from '||tab into m; 
 		execute immediate 'alter sequence SEQUENCE_'||tab||' increment by '||to_char(m-n);
 		execute immediate 'select SEQUENCE_'||tab||'.nextval from dual' into n;
 		execute immediate 'alter sequence SEQUENCE_'||tab||' increment by 1';
	END;
/
CREATE OR REPLACE TRIGGER IOTEQU_LOGON_TRIGGER
	AFTER LOGON ON DATABASE
	BEGIN
	EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT=''YYYY-MM-DD HH24:MI:SS''';
	END;
/
