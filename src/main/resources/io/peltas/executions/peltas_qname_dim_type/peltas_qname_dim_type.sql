WITH sel AS (
  select * from peltas_qname_dim where localname = split_part(:type, ':', 2)
), upd AS (
  INSERT INTO peltas_qname_dim (localname, type, modified, model_dim_id) 
  select split_part(:type, ':', 2), 'type', now(), COALESCE((select id from peltas_model_dim where shortname = split_part(:type, ':', 1)),0) WHERE not exists (select * from sel) 
  RETURNING *
) 
select * from upd UNION ALL select * from sel;