CREATE TABLE peltas_user_dim
(
  id serial NOT NULL,
  username character varying(255) NOT NULL,
  fullname character varying(500) NOT NULL,
  "modified" timestamp without time zone NOT NULL,
  CONSTRAINT peltas_user_dim_pkey PRIMARY KEY (id),
  CONSTRAINT peltas_user_dim_username_unq UNIQUE (username)
);

CREATE TABLE peltas_model_dim
(
  id serial NOT NULL,
  shortname character varying(500) NOT NULL,
  longname character varying(500) NOT NULL,
  modified timestamp without time zone NOT NULL,
  CONSTRAINT peltas_model_dim_pkey PRIMARY KEY (id),
  CONSTRAINT peltas_model_dim_longname UNIQUE (longname),
  CONSTRAINT peltas_model_dim_shortname_unq UNIQUE (shortname)
);

CREATE TABLE peltas_qname_dim
(
  id serial NOT NULL,
  localname character varying(500) NOT NULL,
  type character varying(50) NOT NULL,
  model_dim_id integer NOT NULL,
  modified timestamp without time zone NOT NULL,
  CONSTRAINT peltas_qname_dim_pkey PRIMARY KEY (id),
  CONSTRAINT peltas_qname_dim_qname_model_fkey FOREIGN KEY (model_dim_id)
      REFERENCES public.peltas_model_dim (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT peltas_qname_dim_qname_unq UNIQUE (model_dim_id, localname)
);

CREATE TABLE peltas_datetime_dim
(
  id bigserial NOT NULL,
  "datetime" timestamp without time zone,
  -- add date column (yyyymmdd)
  CONSTRAINT peltas_datetime_dim_pkey PRIMARY KEY (id),
  CONSTRAINT peltas_datetime_dim_datetime_unq UNIQUE (datetime)
);

CREATE TABLE peltas_action_dim
(
  id serial NOT NULL,
  action character varying(255) NOT NULL,
  "modified" timestamp without time zone NOT NULL,
  CONSTRAINT peltas_action_dim_pkey PRIMARY KEY (id),
  CONSTRAINT peltas_action_dim_action_unq UNIQUE (action)
);

CREATE TABLE peltas_node_dim -- dim or fact?
(
  id bigserial NOT NULL,
  path character varying(500),
  noderef character varying(500),
  qname_dim_id int NOT NULL,
  "modified" timestamp without time zone NOT NULL,
  CONSTRAINT peltas_node_fact_pkey PRIMARY KEY (id),
  CONSTRAINT peltas_node_fact_qname_fkey FOREIGN KEY (qname_dim_id) REFERENCES peltas_qname_dim (id),
  CONSTRAINT peltas_node_fact_noderef_unq UNIQUE (noderef),
  CONSTRAINT peltas_node_fact_path_unq UNIQUE (path)
);

CREATE TABLE peltas_action_fact
(
  id bigserial NOT NULL,
  action_dim_id int NOT NULL,
  node_dim_id bigint NOT NULL,
  "modified" timestamp without time zone NOT NULL,
  user_dim_id int NOT NULL,
  datetime_dim_id bigint NOT NULL,
  CONSTRAINT peltas_action_fact_pkey PRIMARY KEY (id),
  CONSTRAINT peltas_action_fact_action_fkey FOREIGN KEY (action_dim_id) REFERENCES peltas_action_dim (id),
  CONSTRAINT peltas_action_fact_creator_fkey FOREIGN KEY (user_dim_id) REFERENCES peltas_user_dim (id),
  CONSTRAINT peltas_action_fact_modified_fkey FOREIGN KEY (datetime_dim_id) REFERENCES peltas_datetime_dim (id),
  CONSTRAINT peltas_action_fact_node__fkey FOREIGN KEY (node_dim_id) REFERENCES peltas_node_dim (id)
);

CREATE TABLE peltas_action_aspect_fact -- is fact or dim?
(
  id serial NOT NULL,
  qname_id int NOT NULL,
  action_fact_id bigint NOT NULL,
  "modified" timestamp without time zone NOT NULL,
  CONSTRAINT peltas_content_aspect_fact_pkey PRIMARY KEY (id),
  CONSTRAINT peltas_content_aspect_fact_qname_fkey FOREIGN KEY (qname_id) REFERENCES peltas_qname_dim (id),
  CONSTRAINT peltas_content_aspect_fact_node_fkey FOREIGN KEY (action_fact_id) REFERENCES peltas_action_fact (id)
);

CREATE TABLE peltas_action_metadata_fact -- is fact or dim?
(
  id serial NOT NULL,
  qname_id int NOT NULL,
  action_fact_id bigint NOT NULL,
  value character varying(500),
  "modified" timestamp without time zone NOT NULL,
  CONSTRAINT peltas_content_metadata_fact_pkey PRIMARY KEY (id),
  CONSTRAINT peltas_content_metadata_fact_qname_fkey FOREIGN KEY (qname_id) REFERENCES peltas_qname_dim (id),
  CONSTRAINT peltas_content_metadata_node_fkey FOREIGN KEY (action_fact_id) REFERENCES peltas_action_fact (id)
);

-- DEFAULT INSERTS - UNKOWNS
INSERT INTO peltas_datetime_dim(id,datetime) VALUES(0,NULL);
INSERT INTO peltas_user_dim(id,username,fullname,modified) VALUES(0,'UNKNOWN','UNKNOWN',NOW());
INSERT INTO peltas_model_dim(id,shortname,longname,modified) VALUES(0,'UNKNOWN','UNKNOWN',NOW());
INSERT INTO peltas_qname_dim(id,localname,type, model_dim_id,modified) VALUES(0,'UNKNOWN','UNKNOWN', 0,NOW());
INSERT INTO peltas_node_dim(id,path,noderef,qname_dim_id,modified) VALUES(0,'UNKNOWN','UNKNOWN',0,NOW());

-- DEFAULT INSERTS - MODELS
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'rn','{http://www.alfresco.org/model/rendition/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'rn'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'sys','{http://www.alfresco.org/model/system/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'sys'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'cm','{http://www.alfresco.org/model/content/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'cm'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'app','{http://www.alfresco.org/model/application/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'app'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'd','{http://www.alfresco.org/model/dictionary/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'd'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'fm','{http://www.alfresco.org/model/forum/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'fm'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'ver2','{http://www.alfresco.org/model/versionstore/2.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'ver2'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'usr','{http://www.alfresco.org/model/user/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'usr'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'ver','{http://www.alfresco.org/model/versionstore/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'ver'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'rule','{http://www.alfresco.org/model/rule/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'rule'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'act','{http://www.alfresco.org/model/action/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'act'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'st','{http://www.alfresco.org/model/site/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'st'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'stcp','{http://www.alfresco.org/model/sitecustomproperty/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'stcp'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'exif','{http://www.alfresco.org/model/exif/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'exif'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'audio','{http://www.alfresco.org/model/audio/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'audio'
);
INSERT INTO peltas_model_dim(shortname,longname,modified) SELECT 'webdav','{http://www.alfresco.org/model/webdav/1.0}',NOW()
WHERE NOT EXISTS (
    SELECT * FROM peltas_model_dim WHERE shortname = 'webdav'
);

