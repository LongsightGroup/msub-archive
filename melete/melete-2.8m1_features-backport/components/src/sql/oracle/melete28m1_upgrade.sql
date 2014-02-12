CREATE TABLE melete_bookmark (
  BOOKMARK_ID number(11) NOT NULL default '0',
  SECTION_ID number(11) NOT NULL default '0',
  USER_ID varchar2(99) default NULL,
  SITE_ID varchar2(99) default NULL,
  TITLE varchar2(255) NOT NULL default '',
  NOTES CLOB,
  LAST_VISITED number(1) default NULL,
  PRIMARY KEY  (BOOKMARK_ID),
  CONSTRAINT FK_MB_MS FOREIGN KEY(SECTION_ID) REFERENCES melete_section(SECTION_ID)
);