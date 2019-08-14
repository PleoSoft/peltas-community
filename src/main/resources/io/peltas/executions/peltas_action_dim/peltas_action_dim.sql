WITH sel AS (
  select * from peltas_action_dim where action = :action
), 
upd AS (
  INSERT INTO peltas_action_dim (action, modified) select :action, now() WHERE not exists (select * from sel)
  returning *
) 
select * from upd UNION ALL select * from sel