create database pokedex;
use pokedex;

create table pokemon(
	id int primary key auto_increment,
	nome varchar(45) not null,
    type varchar(45) not null
);

create table pokemon_eletrico(
	id int primary key,
	nome varchar(45) not null,
    type varchar(45) not null
);

create table pokemon_fogo(
	id int primary key ,
	nome varchar(45) not null,
    type varchar(45) not null
);

create table pokemon_voador(
	id int primary key ,
	nome varchar(45) not null,
    type varchar(45) not null
);

create table pokemon_deletados(
	id int primary key,
	nome varchar(45) not null,
    type varchar(45) not null
);

create table pokemon_totalizador(
	id int primary key auto_increment,
    type varchar(45) not null,
    quant int not null,
    quantdup int not null
);

insert into pokemon(nome, type) values
('pikachu','Elétrico'),
('miraidon','Elétrico'),
('charmander','Fogo'),
('fuecoco','Fogo'),
('miraidon','Elétrico'),
('pidgeotto','Voador'),
('butterfree','Voador'),
('butterfree','Voador'),
('fuecoco', 'Fogo');

-- VIEW PARA STATUS DE SE É DUPLICADO E PQ FOI DELETADO

CREATE VIEW vw_pokemon_status AS
SELECT 
    p.id,
    p.nome,
    p.type,
    'Átivo' AS status,
    NULL AS razao_remocao
FROM 
    pokemon p
WHERE p.id NOT IN (SELECT id FROM pokemon_deletados)

UNION ALL

SELECT 
    pd.id,
    pd.nome,
    pd.type,
    'Removido' AS status,
    'Duplicado' AS razao_remocao
FROM 
    pokemon_deletados pd;

-- VIEW PARA CALCULO DE REPETIDOS

CREATE VIEW vw_pokemon_totais AS
SELECT 
    p.type AS tipo,
    (COUNT(p.id) + (SELECT COUNT(id) FROM pokemon_deletados d WHERE d.type = p.type)) AS valor_total,
    (SELECT COUNT(id) FROM pokemon_deletados d WHERE d.type = p.type) AS count_deletados,
    (COUNT(p.id) + (SELECT COUNT(id) FROM pokemon_deletados d WHERE d.type = p.type)) - (SELECT COUNT(id) FROM pokemon_deletados d WHERE d.type = p.type) AS resultado_final
FROM 
    pokemon p
GROUP BY p.type;
