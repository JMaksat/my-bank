insert into bank.customer_info values (0,	'MyBank',	'First',	'Branch',	'2015-12-31',	'US123BANK#LICENSE',	true,	null,	true, 'scott',	'2016-01-02');
insert into bank.customer_info values (1,	'Scott',	'Ridley',	'Alien',	'1937-11-30',	'US12345HOLLYWOOD'	true,	'2016-01-08',	true,	'scott',	'2016-01-02');

insert into bank.customer_address values (1,	'USA, CA, San Andreas, 123',	'2016-01-02',	null,	true,	'scott',	7,	1);
insert into bank.customer_address values (2,	'USA, NY, New York City, 123',	'2016-01-02',	null,	true,	'scott',	8,	1);

insert into bank.customer_contacts values (1,	'+18007573453256',	'2016-01-02',	null,	true,	'scott',	12,	1);
insert into bank.customer_contacts values (2,	'scott@gmail.com',	'2016-01-02',	null,	true,	'scott',	13,	1);

insert into bank.accounts values (1,	'0010101201600001016',	1,	'2016-01-01',	null,	'2016-01-02',	null,	'scott',	16,	false);
insert into bank.accounts values (2,	'0010101201600002015',	0,	'2016-01-01',	null,	'2016-01-02',	null,	'scott',	15,	false);
insert into bank.accounts values (3,	'0010101201600003018',	0,	'2016-01-01',	null,	'2016-01-02',	null,	'scott',	18,	false);
insert into bank.accounts values (4,	'0010101201600004017',	0,	'2016-01-01',	null,	'2016-01-02',	null,	'scott',	17,	false);
insert into bank.accounts values (7,	'TEST12345',			1,	'2015-02-02',	null,	'2016-01-17',	null,	'scott',	16,	false);
insert into bank.accounts values (8,	'TEST11111111111',		1,	'2016-01-13',	null,	'2016-01-17',	null,	'scott',	16,	false);

insert into bank.transactions values (0,	2,	false,	0,		'2016-01-02',	'19:10:41.554173',	'scott',	2,	3);
insert into bank.transactions values (1,	3,	false,	2000,	'2016-01-02',	'18:42:14.941146',	'scott',	2,	1);
insert into bank.transactions values (3,	2,	false,	0,		'2016-01-17',	'14:50:20.239629',	'scott',	2,	7);
insert into bank.transactions values (4,	2,	false,	0,		'2016-01-17',	'17:55:45.573993',	'scott',	2,	8);

insert into bank.account_rest values (1,	1,	2000,		1,	'2016-01-02',	'18:46:51.652568');
insert into bank.account_rest values (2,	2,	1000000,	0,	'2016-01-02',	'18:47:29.324624');
insert into bank.account_rest values (3,	3,	1000000,	0,	'2016-01-02',	'18:47:42.796646');
insert into bank.account_rest values (4,	4,	1000000,	0,	'2016-01-02',	'18:47:53.576661');
insert into bank.account_rest values (5,	7,	0,			3,	'2016-01-17',	'14:50:20.239629');
insert into bank.account_rest values (6,	8,	0,			4,	'2016-01-17',	'17:55:45.573993');