WITH upd AS (
  UPDATE peltas_user_dim  SET fullname  = '', modified = now() WHERE username = :creator
  RETURNING *
),ins AS (
  INSERT INTO peltas_user_dim (username, fullname, modified) select :creator, '', now() WHERE not exists (select * from upd) 
  RETURNING *
) 
select * from upd UNION ALL select * from ins