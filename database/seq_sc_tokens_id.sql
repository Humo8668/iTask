CREATE SEQUENCE public.sc_tokens_id
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 999999999999999
    CACHE 1;

ALTER SEQUENCE public.sc_tokens_id
    OWNER TO main_admin;