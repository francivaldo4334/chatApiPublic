CREATE TABLE task_history (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR(255),
    data_insercao TIMESTAMP DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION schedule_task()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM pg_sleep(3600);  -- Espera por 1 hora (3600 segundos)
    -- Realize a tarefa desejada aqui
    delete from user_tb ut where ut.active = false and ut.create_at < now() - interval '1 day';
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_schedule_task
AFTER INSERT ON task_history
FOR EACH ROW
EXECUTE FUNCTION schedule_task();