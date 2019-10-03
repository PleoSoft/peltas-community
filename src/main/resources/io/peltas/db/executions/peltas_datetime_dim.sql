WITH sel AS (
  select * from peltas_datetime_dim where datetime = :modified
), upd AS (
  INSERT INTO peltas_datetime_dim(datetime) select :modified WHERE not exists (select * from sel) returning *
) 
select * from upd UNION ALL select * from sel