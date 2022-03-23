CREATE SEQUENCE vc_persona_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    
CREATE TABLE public.vc_persona
(
  id integer NOT NULL DEFAULT nextval('vc_persona_id_seq'::regclass),
  nombre character varying(40) DEFAULT NULL::character varying,  
  apellidos character varying(70) DEFAULT NULL::character varying,
  email character varying(90),
  orcid character varying(40),
  googleid character varying(40),
  cvlac character varying(40),
  plumx character varying(70),
  pivot character varying(40),
  scopusid character varying(40),
  researcherid character varying(40)
);
