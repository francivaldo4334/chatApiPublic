create or replace function conf_delete_inative_users()
returns void
language plpgsql
as $$
begin
	delete from user_tb ut where ut.active = false and ut.create_at < now() - interval '1 day';
	delete from message_tb where create_at < now() - interval '1 month';
end
$$;