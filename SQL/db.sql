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
