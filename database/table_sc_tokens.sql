CREATE TABLE public.sc_tokens
(
    token text COLLATE pg_catalog."default" NOT NULL,
    user_id bigint,
    secret_key character varying(32)[] COLLATE pg_catalog."default" NOT NULL,
    expiry_date time without time zone NOT NULL,
    id integer NOT NULL DEFAULT nextval('sc_tokens_id'),
    CONSTRAINT sc_tokens_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE public.sc_tokens
    OWNER to main_admin;