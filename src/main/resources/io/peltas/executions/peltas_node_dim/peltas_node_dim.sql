WITH select_node AS (
  SELECT * from peltas_node_dim where ( cast(:path as varchar) IS NOT NULL and path = :path) OR ( cast(:nodeRef as varchar) IS NOT NULL and noderef = :nodeRef)
),
upd_noderef AS (
  UPDATE peltas_node_dim  SET noderef  = :nodeRef, modified = now() where  path = :path and cast(:nodeRef as varchar) is not null and noderef <> :nodeRef
  RETURNING *
), upd AS (
  UPDATE peltas_node_dim  SET path  = :path, modified = now() where  noderef = :nodeRef and  cast(:path as varchar) is not null and path <> :path
  RETURNING *
), ins AS (
   INSERT INTO peltas_node_dim (path, noderef, qname_dim_id, modified) select  :path, :nodeRef, :peltas_qname_dim_type.id, now() 
   WHERE not exists (select * from select_node UNION ALL select * from upd UNION ALL select * from upd_noderef) 
   RETURNING *
) 
select * from select_node WHERE not exists (select * from upd UNION ALL select * from upd_noderef) UNION ALL select * from ins UNION ALL select * from upd UNION ALL select * from upd_noderef