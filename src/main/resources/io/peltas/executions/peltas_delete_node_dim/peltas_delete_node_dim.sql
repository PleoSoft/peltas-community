WITH select_node AS (
  SELECT * from peltas_node_dim where noderef = :nodeRef
),
select_unknown AS (
  SELECT * from peltas_node_dim where not exists (select * from select_node) and id = 0
)
select * from select_node UNION ALL select * from select_unknown