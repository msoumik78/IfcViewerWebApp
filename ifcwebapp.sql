CREATE TABLE UserMaster (
    Username varchar(50),
	Password varchar(50),
    Role varchar(50)
);

insert into UserMaster (Username, Password, Role) values ('testArchitect1', 'password', 'ARCHITECT');

insert into UserMaster (Username, Password, Role) values ('testClient11', 'password', 'CLIENT');
insert into UserMaster (Username, Password, Role) values ('testClient12', 'password', 'CLIENT');
insert into UserMaster (Username, Password, Role) values ('testClient13', 'password', 'CLIENT');

CREATE TABLE ArchitectToClientMapping (
    Architect varchar(50),
    Client varchar(50)	
);

insert into ArchitectToClientMapping (Architect, Client) values ('testArchitect1', 'testClient11');
insert into ArchitectToClientMapping (Architect, Client) values ('testArchitect1', 'testClient12');
insert into ArchitectToClientMapping (Architect, Client) values ('testArchitect1', 'testClient13');

CREATE TABLE FileMaster (
    PictureId int,
    OriginalFileName varchar(100),
    TransformedFileName varchar(100),	
	UploadedByArchitect varchar(50),
	UploadedForClient varchar(50),	
	UploadTime Timestamp	
);

CREATE TABLE MessageExchanges (
    PictureId int,
	ComponentId int,
    MessageFrom varchar(50),
    MessageTo varchar(50),
	MessageText varchar(200), 
	MessageTime Timestamp	
);
