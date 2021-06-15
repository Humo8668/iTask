CREATE TABLE public."Users" (
    id integer NOT NULL,
    login character(32) NOT NULL,
    "fullName" character(150) NOT NULL,
    email character(320),
    "passwordHash" character(80) NOT NULL,
    state character(1) NOT NULL
);

ALTER TABLE public."Users" ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public."Users_id_seq1"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

ALTER TABLE ONLY public."Users"
    ADD CONSTRAINT login_uniq UNIQUE (login);
	
GRANT ALL ON TABLE public."Users" TO postgres;

ALTER TABLE public."Users"
    ALTER COLUMN state SET DEFAULT 'A';

ALTER TABLE public."Users"
    ADD PRIMARY KEY (id);

