--
-- PostgreSQL database dump
--

-- Dumped from database version 10.6
-- Dumped by pg_dump version 15.2

-- Started on 2023-10-30 22:12:17

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4134 (class 0 OID 1128546)
-- Dependencies: 216
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: postgis; Owner: postgres
--

--COPY spatial_ref_sys (srid, auth_name, auth_srid, srtext, proj4text) FROM stdin;
--\.


--
-- TOC entry 4329 (class 0 OID 1137947)
-- Dependencies: 231
-- Data for Name: project; Type: TABLE DATA; Schema: projects; Owner: postgres
--

COPY projects.project (project_id, name) FROM stdin;
790	New Project (790)
530	New Project (530)
747	New Project (747)
312	New Project (312)
798	New Project (798)
515	New Project (515)
\.


--
-- TOC entry 4330 (class 0 OID 1137955)
-- Dependencies: 232
-- Data for Name: project_envelope; Type: TABLE DATA; Schema: projects; Owner: postgres
--

COPY projects.project_envelope (project_id, lowerleft, upperright, srid) FROM stdin;
312	(-13477904.208765,4405248.4775360003)	(-13336648.580494,4525101.737888)	3857
515	(-13501141.065363999,4378342.6435799999)	(-13289563.371071,4506145.3548729997)	3857
747	(-13251421.293956,4190415.6522809998)	(-13237204.006695,4206314.5541639999)	3857
530	(-13336342.832381001,4156981.009666)	(-13199367.677694,4276834.2700169999)	3857
790	(-13964790,3794999)	(-12605137,5224085)	3857
\.


--
-- TOC entry 4331 (class 0 OID 1137965)
-- Dependencies: 233
-- Data for Name: projectdatasource; Type: TABLE DATA; Schema: projects; Owner: postgres
--

COPY projects.projectdatasource (project_id, rastergroup_id) FROM stdin;
\.


--
-- TOC entry 4315 (class 0 OID 1125392)
-- Dependencies: 202
-- Data for Name: raster; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.raster (raster_id, rastertype_id, source_id, rastergeomproperties_id) FROM stdin;
0	0	0	0
1	0	1	1
2	0	2	1
3	0	3	1
4	0	4	1
5	0	5	1
\.


--
-- TOC entry 4316 (class 0 OID 1125397)
-- Dependencies: 203
-- Data for Name: rastergeomproperties; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rastergeomproperties (rastergeomproperties_id, dx, dy, lowerleft, upperright, srid) FROM stdin;
0	1000	1000	(59881,3599665)	(1516621,4738362)	32610
1	1000	1000	(-13964790,3794999)	(-12605137,5224085)	3857
\.


--
-- TOC entry 4317 (class 0 OID 1125419)
-- Dependencies: 204
-- Data for Name: rastergroup; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rastergroup (rastergroup_id, name) FROM stdin;
0	titans_test_rastergroup_01
1	North American Model Forecasts
2	High Resolution Rapid Refresh
3	MRMS
4	GOES
\.


--
-- TOC entry 4319 (class 0 OID 1125430)
-- Dependencies: 206
-- Data for Name: rastergroup_by_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rastergroup_by_user (rastergroup_by_user_id, rastergroup_id, user_id) FROM stdin;
1	1	0
2	2	0
3	3	0
4	4	0
\.


--
-- TOC entry 4327 (class 0 OID 1125476)
-- Dependencies: 214
-- Data for Name: rastergroup_raster_link; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rastergroup_raster_link (rastergroup_id, raster_id) FROM stdin;
0	0
1	1
2	2
2	3
3	4
4	5
\.


--
-- TOC entry 4313 (class 0 OID 1125372)
-- Dependencies: 200
-- Data for Name: rastertype; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rastertype (rastertype_id, name) FROM stdin;
0	basic_model
\.


--
-- TOC entry 4314 (class 0 OID 1125377)
-- Dependencies: 201
-- Data for Name: source; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.source (source_id, title, description) FROM stdin;
0	test_source	for testing purposes
1	North American Model Forecasts	Forecasted data
2	High Resolution Rapid Refresh	High Resolution Rapid Refresh
3	High Resolution Rapid Refresh (Archived)	Archived High Resolution Rapid Refresh
4	Multi-Radar Multi-Sensor	Multi-Radar Multi-Sensor System
5	GOES-18	GOES 18 data
\.


--
-- TOC entry 4328 (class 0 OID 1129754)
-- Dependencies: 230
-- Data for Name: authentication; Type: TABLE DATA; Schema: users; Owner: postgres
--

COPY users.authentication (user_id, key) FROM stdin;
0	password
\.


--
-- TOC entry 4318 (class 0 OID 1125425)
-- Dependencies: 205
-- Data for Name: user; Type: TABLE DATA; Schema: users; Owner: postgres
--

COPY users."user" (user_id, name, email) FROM stdin;
0	Ricardo Marquez	ricardo.marquez@terrabyteanalytics.com
\.


--
-- TOC entry 4339 (class 0 OID 0)
-- Dependencies: 207
-- Name: raster_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.raster_id_seq', 1, true);


--
-- TOC entry 4340 (class 0 OID 0)
-- Dependencies: 208
-- Name: rastergeomproperties_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rastergeomproperties_id_seq', 0, true);


--
-- TOC entry 4341 (class 0 OID 0)
-- Dependencies: 210
-- Name: rastergroup_by_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rastergroup_by_user_id_seq', 2, true);


--
-- TOC entry 4342 (class 0 OID 0)
-- Dependencies: 209
-- Name: rastergroup_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rastergroup_id_seq', 2, true);


--
-- TOC entry 4343 (class 0 OID 0)
-- Dependencies: 211
-- Name: rastertype_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rastertype_id_seq', 0, false);


--
-- TOC entry 4344 (class 0 OID 0)
-- Dependencies: 212
-- Name: source_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.source_id_seq', 0, true);


--
-- TOC entry 4345 (class 0 OID 0)
-- Dependencies: 213
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: users; Owner: postgres
--

SELECT pg_catalog.setval('users.user_id_seq', 0, true);


-- Completed on 2023-10-30 22:12:18

--
-- PostgreSQL database dump complete
--

