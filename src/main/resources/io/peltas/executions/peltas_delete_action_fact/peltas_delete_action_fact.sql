WITH sel AS (
  select * from peltas_action_fact where action_dim_id = :peltas_action_dim.id 
  	and node_dim_id = :peltas_delete_node_dim.id 
 	and user_dim_id = 0 
  	and datetime_dim_id = 0
), 
upd AS (
  INSERT INTO peltas_action_fact (action_dim_id, node_dim_id, modified, user_dim_id, datetime_dim_id) 
  select :peltas_action_dim.id, :peltas_delete_node_dim.id, now(), 0,  0  WHERE not exists (select * from sel)
  returning *
) 
select * from upd UNION ALL select * from sel