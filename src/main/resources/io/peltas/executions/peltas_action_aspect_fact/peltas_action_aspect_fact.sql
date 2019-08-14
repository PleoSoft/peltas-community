WITH sel AS (
  select * from peltas_action_aspect_fact where qname_id = :peltas_qname_dim_aspect.id  and action_fact_id = :peltas_action_fact.id
  ), 
upd AS (
  INSERT INTO peltas_action_aspect_fact (qname_id, action_fact_id, modified) 
  select :peltas_qname_dim_aspect.id, :peltas_action_fact.id, now() WHERE not exists (select * from sel)
  returning *
) 
select * from upd UNION ALL select * from sel