WITH sel AS (
  select * from peltas_qname_dim where localname = :aspect.localName
), upd AS (
  INSERT INTO peltas_qname_dim (localname, type, modified, model_dim_id) 
  select :aspect.localName, 'aspect', now(), COALESCE((select id from peltas_model_dim where shortname = :aspect.prefixString),0) WHERE not exists (select * from sel) 
  RETURNING *
) 
select * from upd UNION ALL select * from sel;