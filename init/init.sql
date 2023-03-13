CREATE TABLE public.t_club (
                               id serial4 NOT NULL,
                               "name" varchar(32) NOT NULL,
                               "money" int4 NULL,
                               nick_name varchar(32) NULL,
                               birthday timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                               create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                               update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT t_club_pkey PRIMARY KEY (id)
);
CREATE TABLE public.ts_kv (
                              ts int8 NOT NULL,
                              "key" int4 NOT NULL,
                              bool_v bool NULL,
                              str_v varchar(10000000) NULL,
                              long_v int8 NULL,
                              dbl_v float8 NULL,
                              json_v json NULL,
                              CONSTRAINT ts_kv_pkey PRIMARY KEY (ts, key)
);