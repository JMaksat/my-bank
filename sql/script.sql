create database bankdb;
create user scott with password 'tiger' superuser;
create schema bank;

CREATE TABLE bank.account_rest
(
  rest_id serial NOT NULL,
  account_id integer,
  rest_sum real,
  transaction_id integer,
  rest_date date,
  rest_time time without time zone,
  CONSTRAINT account_rest_pkey PRIMARY KEY (rest_id),
  CONSTRAINT rest_account_fk FOREIGN KEY (account_id)
      REFERENCES bank.accounts (account_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE bank.accounts
(
  account_id serial NOT NULL,
  account_number varchar(32),
  account_owner integer,
  date_opened date,
  date_closed date,
  date_created date,
  date_modified date,
  user_id varchar(32),
  account_type integer,
  is_suspended boolean,
  CONSTRAINT accounts_pkey PRIMARY KEY (account_id),
  CONSTRAINT account_owner_fk FOREIGN KEY (account_owner)
      REFERENCES bank.customer_info (customer_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE bank.bank_parameters
(
  parameter_id serial NOT NULL,
  parent_id integer,
  parameter_name varchar(64),
  value varchar(1024),
  date_created date,
  date_modified date,
  active_from date,
  active_to date,
  user_id varchar(32)
);

CREATE TABLE bank.customer_address
(
  address_id serial NOT NULL,
  value varchar(1024),
  date_crated date,
  date_modified date,
  is_active boolean,
  user_id varchar(32),
  address_type integer,
  customer_id integer,
  CONSTRAINT customer_address_pkey PRIMARY KEY (address_id),
  CONSTRAINT address_customer_fk FOREIGN KEY (customer_id)
      REFERENCES bank.customer_info (customer_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE bank.customer_contacts
(
  contact_id serial NOT NULL,
  value varchar(1024),
  date_created date,
  date_modified date,
  is_active boolean,
  user_id varchar(32),
  contact_type integer,
  customer_id integer,
  CONSTRAINT customer_contacts_pkey PRIMARY KEY (contact_id),
  CONSTRAINT contacts_customer_fk FOREIGN KEY (customer_id)
      REFERENCES bank.customer_info (customer_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE bank.customer_info
(
  customer_id serial NOT NULL,
  first_name character varying(64),
  last_name character varying(64),
  middle_name character varying(64),
  birth_date date,
  personal_id character varying(64),
  is_resident boolean,
  date_modified date,
  is_active boolean,
  user_id character varying(32),
  date_created date,
  CONSTRAINT customer_info_pkey PRIMARY KEY (customer_id)
)

CREATE TABLE bank.directory
(
  dir_id serial NOT NULL,
  dir_group varchar(64),
  dir_type varchar(64),
  description varchar(1024),
  date_created date,
  date_modified date,
  is_active boolean,
  user_id varchar(32),
  CONSTRAINT directory_pkey PRIMARY KEY (dir_id)
);

CREATE TABLE bank.transactions
(
  transaction_id serial NOT NULL,
  operation_type integer,
  is_reversed boolean,
  transaction_sum real,
  transaction_date date,
  transaction_time time without time zone,
  user_id varchar(32),
  account_debit integer,
  account_credit integer,
  CONSTRAINT transactions_pkey PRIMARY KEY (transaction_id),
  CONSTRAINT account_debit_fk FOREIGN KEY (account_debit)
      REFERENCES bank.accounts (account_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);