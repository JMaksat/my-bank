insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (4, 'CONTACTS', 'PHONE_NUMBER', 'Phone number', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (5, 'CONTACTS', 'EMAIL', 'Email address', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (6, 'CONTACTS', 'SKYPE', 'Skype name', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (7, 'ADDRESS', 'ADDRESS_LINE1', 'First address line', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (8, 'CONTACTS', 'ADDRESS_LINE2', 'Second address line', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (9, 'OPERATIONS', 'INCOMING_TRANSFER', 'Transfer money from buffer account for incomings to personal account.', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (10, 'OPERATIONS', 'OUTGOING_TRANSFER', 'Transfer money from personal account to buffer account for outgoings.', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (11, 'OPERATIONS', 'DOMESTIC_TRANSFER', 'Transfer money personal to personal account. Always right to left.', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (12, 'OPERATIONS', 'REFILL', 'Transfer money from buffer account for refills to personal account.', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (13, 'OPERATIONS', 'WITHDRAWAL', 'Transfer money from personal account to buffer account for withdrawals.', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (14, 'OPERATIONS', 'VOID', 'This operation doing nothing.', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (15, 'ACCOUNTS', 'BUFFER', 'Buffer accounts for temporary keep money. Bank may contain more than one of buffer accounts.', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (16, 'ACCOUNTS', 'PERSONAL', 'Personal accounts for clients.', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (17, 'ACCOUNTS', 'INTEREST', 'Accounts can keep any kind of interest money. Bank may contain more than one of interest accounts.', '2016-01-01', '2016-01-01', true, 'scott');
insert into bank.directory (dir_id, dir_group, dir_type, description, date_created, date_modified, is_active, user_id)
values (18, 'ACCOUNTS', 'SYSTEM', 'Whole balance of the bank. Bank must containt only one system account.', '2016-01-01', '2016-01-01', true, 'scott');
alter sequence bank.directory_dir_id_seq restart with 19;


insert into bank.customer_info values (0,	'MyBank',	'First',	'Branch',	'2015-12-31',	'US123BANK#LICENSE',	true,	null,	true, 'scott',	'2016-01-02');
insert into bank.customer_info values (1,	'Scott',	'Ridley',	'Alien',	'1937-11-30',	'US12345HOLLYWOOD',	true,	'2016-01-08',	true,	'scott',	'2016-01-02');
alter sequence bank.customer_info_customer_id_seq restart with 2;


insert into bank.customer_address values (1,	'USA, CA, San Andreas, 123',	'2016-01-02',	null,	true,	'scott',	7,	1);
insert into bank.customer_address values (2,	'USA, NY, New York City, 123',	'2016-01-02',	null,	true,	'scott',	8,	1);
alter sequence bank.customer_address_address_id_seq restart with 3;


insert into bank.customer_contacts values (1,	'+18007573453256',	'2016-01-02',	null,	true,	'scott',	4,	1);
insert into bank.customer_contacts values (2,	'scott@gmail.com',	'2016-01-02',	null,	true,	'scott',	5,	1);
alter sequence bank.customer_contacts_contact_id_seq restart with 3;


insert into bank.accounts values (1,	'0010101201600001016',	1,	'2016-01-01',	null,	'2016-01-02',	null,	'scott',	16,	false, null);
insert into bank.accounts
values (2,	'0010101201600002015',	0,	'2016-01-01',	null,	'2016-01-02',	null,	'scott',	15,	false, 'This account keeps money transferred from another banks to then transfer them to personal accounts.');
insert into bank.accounts values (3,	'0010101201600003018',	0,	'2016-01-01',	null,	'2016-01-02',	null,	'scott',	18,	false, null);
insert into bank.accounts values (4,	'0010101201600004017',	0,	'2016-01-01',	null,	'2016-01-02',	null,	'scott',	17,	false, null);
insert into bank.accounts values (7,	'TEST12345',			1,	'2015-02-02',	null,	'2016-01-17',	null,	'scott',	16,	false, null);
insert into bank.accounts values (8,	'TEST11111111111',		1,	'2016-01-13',	null,	'2016-01-17',	null,	'scott',	16,	false, null);
insert into bank.accounts
values (9, '0010101201600005015', 0, '2016-01-01', null, '2016-01-02', null, 'scott', 15, false, 'This account keeps money to then transfer them to another banks.');
insert into bank.accounts
values (10, '0010101201600006015', 0, '2016-01-01', null, '2016-01-02', null, 'scott', 15, false, 'This account temporarily keeps money deposited by client, until operator transfer them to clients personal account.');
insert into bank.accounts
values (11, '0010101201600007015', 0, '2016-01-01', null, '2016-01-02', null, 'scott', 15, false, 'This account temporarily keeps money transferred from client''s personal account by operator, until client physically gets money.');
alter sequence bank.accounts_account_id_seq restart with 12;

insert into bank.transactions values (0,	14,	false,	0,		'2016-01-02',	'19:10:41.554173',	'scott',	2,	3);
insert into bank.transactions values (1,	12,	false,	2000,	'2016-01-02',	'18:42:14.941146',	'scott',	2,	1);
insert into bank.transactions values (4,	14,	false,	0,		'2016-01-17',	'17:55:45.573993',	'scott',	2,	8);
alter sequence bank.transactions_transaction_id_seq restart with 5;

insert into bank.account_rest values (1,	1,	2000,		1,	'2016-01-02',	'18:46:51.652568');
insert into bank.account_rest values (2,	2,	1000000,	0,	'2016-01-02',	'18:47:29.324624');
insert into bank.account_rest values (3,	3,	1000000,	0,	'2016-01-02',	'18:47:42.796646');
insert into bank.account_rest values (4,	4,	1000000,	0,	'2016-01-02',	'18:47:53.576661');
insert into bank.account_rest values (5,	7,	0,			3,	'2016-01-17',	'14:50:20.239629');
insert into bank.account_rest values (6,	8,	0,			4,	'2016-01-17',	'17:55:45.573993');
insert into bank.account_rest values (7,	9,	1000000,	0,	'2016-01-02',	'18:47:29.324624');
insert into bank.account_rest values (8,   10,	1000000,	0,	'2016-01-02',	'18:47:29.324624');
insert into bank.account_rest values (9,   11,	1000000,	0,	'2016-01-02',	'18:47:29.324624');
alter sequence bank.account_rest_rest_id_seq restart with 10;