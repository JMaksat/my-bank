create database bankdb;
create user scott with password 'tiger' superuser;
create schema bank;

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
);

/* - Bank account structure: BBB DDMMYYYY CCCCC TTT (e.g. 0032812201500152056). BBB - branch, DDMMYYYY - open date, CCCCC - counter which start from 0 every business day, TTT - type of the account (buffer, personal, interest, system) */
CREATE TABLE bank.accounts
(
  account_id serial NOT NULL,
  account_number character varying(32),
  account_owner integer,
  date_opened date,
  date_closed date,
  date_created date,
  date_modified date,
  user_id character varying(32),
  account_type integer,
  is_suspended boolean,
  comment character varying(4000),
  CONSTRAINT accounts_pkey PRIMARY KEY (account_id),
  CONSTRAINT account_owner_fk FOREIGN KEY (account_owner)
      REFERENCES bank.customer_info (customer_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

/*
- Domestic transactions. Domestic transaction can be fulfilled from one account (dropdown-list) to second.
- Transactions between banks. If transaction is incoming, then first account should be foreign and second is domestic. If transaction is outgoing, then the first account is domestic and second is foreign.
- Transactions between banks should use special one buffer account.
- Transaction contains several types of operations: Money transfer (incoming, outgoing, between domestic accounts...always from right to left), Refill, Withdrawal
*/
CREATE TABLE bank.transactions
(
  transaction_id serial NOT NULL,
  operation_type integer,
  is_reversed boolean,
  transaction_sum real,
  transaction_date date,
  transaction_time time without time zone,
  user_id character varying(32),
  account_debit integer,
  account_credit integer,
  CONSTRAINT transactions_pkey PRIMARY KEY (transaction_id),
  CONSTRAINT account_debit_fk FOREIGN KEY (account_debit)
      REFERENCES bank.accounts (account_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

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
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT rest_transaction_fk FOREIGN KEY (transaction_id)
      REFERENCES bank.transactions (transaction_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE bank.bank_parameters
(
  parameter_id serial NOT NULL,
  parent_id integer,
  parameter_name character varying(64),
  value character varying(1024),
  date_created date,
  date_modified date,
  active_from date,
  active_to date,
  user_id character varying(32),
  CONSTRAINT bank_parameters_pkey PRIMARY KEY (parameter_id),
  CONSTRAINT bank_parameters_fkey FOREIGN KEY (parent_id)
  REFERENCES bank.bank_parameters (parameter_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE bank.customer_address
(
  address_id serial NOT NULL,
  value character varying(1024),
  date_crated date,
  date_modified date,
  is_active boolean,
  user_id character varying(32),
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
  value character varying(1024),
  date_created date,
  date_modified date,
  is_active boolean,
  user_id character varying(32),
  contact_type integer,
  customer_id integer,
  CONSTRAINT customer_contacts_pkey PRIMARY KEY (contact_id),
  CONSTRAINT contacts_customer_fk FOREIGN KEY (customer_id)
      REFERENCES bank.customer_info (customer_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE bank.directory
(
  dir_id serial NOT NULL,
  dir_group character varying(64),
  dir_type character varying(64),
  description character varying(1024),
  date_created date,
  date_modified date,
  is_active boolean,
  user_id character varying(32),
  CONSTRAINT directory_pkey PRIMARY KEY (dir_id)
);

CREATE OR REPLACE FUNCTION bank.change_customer_activity(
    p_customer_id integer,
    p_is_active boolean)
  RETURNS boolean AS
$BODY$
begin
   update bank.customer_info
      set is_active     = p_is_active,
          date_modified = now(),
          user_id       = user
    where customer_id = p_customer_id;

   return true;
	
exception when others then 
   return false;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.decrease_account_rest(
    p_account integer,
    p_transaction integer,
    p_sum real)
  RETURNS integer AS
$BODY$
declare
   l_rest_id integer;
   l_sum real;
begin
   select t.rest_sum 
     into l_sum
     from bank.account_rest t 
    where t.account_id = p_account
      and t.rest_id = (select max(z.rest_id)
                         from bank.account_rest z
                        where z.account_id = t.account_id);
						
   l_sum := l_sum - p_sum;
						
   insert into bank.account_rest (account_id, rest_sum, transaction_id, rest_date, rest_time)
   values (p_account, l_sum, p_transaction, now(), now()) returning rest_id into l_rest_id;
   
   return l_rest_id;
	
exception when others then 
   return -1;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.get_account_list(p_customer_id integer)
  RETURNS refcursor AS
$BODY$
declare
    rcur refcursor;
begin
    open rcur for select t.account_id,
	                 t.account_number
		    from bank.accounts t
		   where t.account_owner = p_customer_id
		     and now() between t.date_opened and coalesce(t.date_closed, now());
    return rcur;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.get_account_rest(p_account integer)
  RETURNS real AS
$BODY$
declare
l_retval real := -1;
begin
   select t.rest_sum 
     into l_retval
     from bank.account_rest t 
    where t.account_id = p_account
      and t.rest_id = (select max(z.rest_id)
                         from bank.account_rest z
                        where z.account_id = t.account_id);
   return l_retval;
	
exception when others then 
   return -1;
end; $BODY$
  LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bank.get_account_transactions(p_account_id integer)
  RETURNS refcursor AS
$BODY$
declare
    rcur refcursor;
begin
    open rcur for select transaction_id,
			 (select z.dir_type
			    from bank.directory z
			   where z.is_active = true 
			     and z.dir_id = t.operation_type) as operation_type,
			 is_reversed,
			 transaction_sum,
			 transaction_date,
			 to_char(transaction_time, 'hh24:mi:ss') as transaction_time,
			 (select z.account_number
			    from bank.accounts z
			   where z.account_id = t.account_debit) as account_debit,
			 (select z.account_number
			    from bank.accounts z
			   where z.account_id = t.account_credit) as account_credit
		    from bank.transactions t
		   where p_account_id in (t.account_debit, t.account_credit);
    return rcur;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.get_account_types()
  RETURNS refcursor AS
$BODY$
declare
    rcur refcursor;
begin
    open rcur for select t.dir_id
                       , t.dir_type
		    from bank.directory t
		   where t.is_active = true
		     and t.dir_group = 'ACCOUNTS';
    return rcur;
end; $BODY$
  LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bank.get_address(p_customer_id integer)
  RETURNS refcursor AS
$BODY$
declare
    rcur refcursor;
begin
    open rcur for select ca.address_id
		       , (select d.dir_type
			    from bank.directory d
			   where d.dir_group = 'ADDRESS'
			     and d.dir_id = ca.address_type) as address_type
		       , ca.value
		    from bank.customer_address ca
		   where ca.customer_id = p_customer_id
		     and ca.is_active = true;
   
    return rcur;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.get_all_accounts()
  RETURNS refcursor AS
$BODY$
declare
    rcur refcursor;
begin
    open rcur for select t.account_id,
	                 t.account_number
		    from bank.accounts t
		   where now() between t.date_opened and coalesce(t.date_closed, now());
    return rcur;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.get_contacts(p_customer_id integer)
  RETURNS refcursor AS
$BODY$
declare
    rcur refcursor;
begin
    open rcur for select cc.contact_id
		       , (select d.dir_type
			    from bank.directory d
			   where d.dir_group = 'CONTACTS'
			     and d.dir_id = cc.contact_type) as contact_type
		       , cc.value
		    from bank.customer_contacts cc
		   where cc.customer_id = p_customer_id
		     and cc.is_active = true;
   
    return rcur;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.get_customer_accounts(p_customer_id integer)
  RETURNS refcursor AS
$BODY$
declare
    rcur refcursor;
begin
    open rcur for select t.account_id,
                         t.account_number,
                         t.account_owner,
                         t.date_opened,
                         coalesce(t.date_closed, '1901-01-01') as date_closed,
                         t.date_created,
                         coalesce(t.date_modified, '1901-01-01') as date_modified,
                         t.user_id,
                         (select z.dir_type
                            from bank.directory z
                           where z.dir_id = t.account_type) as account_type,
                         t.is_suspended,
                         (select z.rest_sum
                            from bank.account_rest z
                           where z.rest_id = (select max(r.rest_id)
                                                from bank.account_rest r
                                               where r.account_id = z.account_id)
                             and z.account_id = t.account_id) as rest_sum
                    from bank.accounts t
                   where t.account_owner = p_customer_id
                     and now() between t.date_opened and coalesce(t.date_closed, now());
    return rcur;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.get_customer_info(p_pattern character varying)
  RETURNS refcursor AS
$BODY$
declare
    rcur refcursor;
begin
    open rcur for select t.customer_id,
			 t.first_name,
			 t.last_name,
			 t.middle_name,
			 t.birth_date,
			 t.personal_id,
			 t.is_resident,
			 coalesce(t.date_modified, '1901-01-01') as date_modified,
			 t.is_active,
			 t.user_id,
			 t.date_created
                    from bank.customer_info t
                   where t.first_name || t.last_name || t.middle_name like p_pattern
                   order by t.customer_id;
    return rcur;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.get_transaction_types()
  RETURNS refcursor AS
$BODY$
declare
    rcur refcursor;
begin
    open rcur for select t.dir_id
                       , t.dir_type
		    from bank.directory t
		   where t.is_active = true
		     and t.dir_group = 'OPERATIONS';
    return rcur;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.increase_account_rest(
    p_account integer,
    p_transaction integer,
    p_sum real)
  RETURNS integer AS
$BODY$
declare
   l_rest_id integer;
   l_sum real;
begin
   select t.rest_sum 
     into l_sum
     from bank.account_rest t 
    where t.account_id = p_account
      and t.rest_id = (select max(z.rest_id)
                         from bank.account_rest z
                        where z.account_id = t.account_id);
						
   l_sum := l_sum + p_sum;
						
   insert into bank.account_rest (account_id, rest_sum, transaction_id, rest_date, rest_time)
   values (p_account, l_sum, p_transaction, now(), now()) returning rest_id into l_rest_id;
   
   return l_rest_id;
	
exception when others then 
   return -1;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.insert_account_info(
    p_account_number character varying,
    p_account_owner integer,
    p_date_opened date,
    p_account_type integer)
  RETURNS boolean AS
$BODY$
declare
l_account_id integer;
l_transaction_id integer;
begin
   -- Adding new account
   insert into bank.accounts (account_number, account_owner, date_opened, date_closed, date_created, date_modified, user_id, account_type, is_suspended)
   values (p_account_number, p_account_owner, p_date_opened, null, now(), null, user, p_account_type, false) returning account_id into l_account_id;

   -- Adding void transaction
   insert into bank.transactions (operation_type, is_reversed, transaction_sum, transaction_date, transaction_time, user_id, account_debit, account_credit)
   values (14, false, 0.0, now(), now(), user, 2, l_account_id) returning transaction_id into l_transaction_id;

   -- Adding very first rest of theaccount and it is equal to 0
   insert into bank.account_rest (account_id, rest_sum, transaction_id, rest_date, rest_time)
   values (l_account_id, 0, l_transaction_id, now(), now());
	
   return true;
	
--exception when others then 
  -- return false;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.insert_customer_info(
    p_first_name character varying,
    p_last_name character varying,
    p_middle_name character varying,
    p_birth_date date,
    p_personal_id character varying,
    p_is_resident boolean)
  RETURNS boolean AS
$BODY$
begin
   insert into bank.customer_info (first_name, last_name, middle_name, birth_date, personal_id, is_resident, date_created, date_modified, is_active, user_id)
   values (p_first_name, p_last_name, p_middle_name, p_birth_date, p_personal_id, p_is_resident, now(), null, true, user);
	
   return true;
	
exception when others then 
   return false;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.insert_transaction_info(
    p_operation_type integer,
    p_is_reversed boolean,
    p_transaction_sum real,
    p_account_debit integer,
    p_account_credit integer)
  RETURNS integer AS
$BODY$
declare
l_rest_id integer;
l_transaction_id integer;
l_suspended boolean := false;
k record;
begin
  
  for k in (select t.is_suspended
			  from bank.accounts t
		     where t.account_id in (p_account_debit, p_account_credit))
  loop
    if k.is_suspended = true then
	  l_suspended := true;
    end if;
  end loop;
  
  if l_suspended = false then
  
    if p_transaction_sum <= bank.get_account_rest(p_account_debit) then
      insert into bank.transactions (operation_type, is_reversed, transaction_sum, transaction_date, transaction_time, user_id, account_debit, account_credit)
        values (p_operation_type, p_is_reversed, p_transaction_sum, now(), now(), user, p_account_debit, p_account_credit) returning transaction_id into l_transaction_id;
		  
      l_rest_id := bank.decrease_account_rest(p_account_debit, l_transaction_id, p_transaction_sum);
      if (l_rest_id >= 0) then 
        l_rest_id := bank.increase_account_rest(p_account_credit, l_transaction_id, p_transaction_sum);
      end if;
    else 
      return -2;
    end if;

  else
    return -3;
  end if;

  return l_rest_id;	
exception when others then 
   return -1;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.update_account_info(
    p_account_id integer,
    p_is_suspended boolean)
  RETURNS boolean AS
$BODY$
begin
   update bank.accounts
      set is_suspended   = p_is_suspended,
          date_modified  = now(),
          user_id        = user
    where account_id     = p_account_id;

   return true;
	
exception when others then 
   return false;
end; $BODY$
  LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION bank.update_customer_info(
    p_customer_id integer,
    p_first_name character varying,
    p_last_name character varying,
    p_middle_name character varying,
    p_birth_date date,
    p_personal_id character varying,
    p_is_resident boolean)
  RETURNS boolean AS
$BODY$
begin
   update bank.customer_info
      set first_name    = p_first_name,
          last_name     = p_last_name,
          middle_name   = p_middle_name,
          birth_date    = p_birth_date,
          personal_id   = p_personal_id,
          is_resident   = p_is_resident,
          date_modified = now(),
          user_id       = user
    where customer_id = p_customer_id;

   return true;
	
exception when others then 
   return false;
end; $BODY$
  LANGUAGE plpgsql;