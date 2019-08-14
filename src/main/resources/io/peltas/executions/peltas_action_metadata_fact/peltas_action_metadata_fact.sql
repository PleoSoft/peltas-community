WITH sel AS (
  select * from peltas_action_metadata_fact where qname_id = :peltas_qname_dim_properties.id  and action_fact_id = :peltas_action_fact.id
  ), 
upd AS (
  INSERT INTO peltas_action_metadata_fact (qname_id, action_fact_id, value, modified) 
  select :peltas_qname_dim_properties.id, :peltas_action_fact.id, :properties.value, now() WHERE not exists (select * from sel)
  returning *
) 
select * from upd UNION ALL select * from sel