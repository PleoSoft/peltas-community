WITH sel AS (
  select * from peltas_qname_dim where localname = split_part(:properties.key, '}', 2)
), upd AS (
  INSERT INTO peltas_qname_dim (localname, type, modified, model_dim_id) 
  select split_part(:properties.key, '}', 2), 'metadata', now(), COALESCE((select id from peltas_model_dim where longname = substring(:properties.key from '{.+}')),0) WHERE not exists (select * from sel) 
  RETURNING *
) 
select * from upd UNION ALL select * from sel;