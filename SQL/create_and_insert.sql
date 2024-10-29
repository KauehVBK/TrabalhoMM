create database pokedex;
use pokedex;

create table tb_pokemon
(
	id_pokemon int auto_increment primary key not null ,
    nome_pokemon varchar(25) not null,
    tipo_pokemon varchar(35) not null
);

insert into tb_pokemon(nome_pokemon, tipo_pokemon) values
("pikachu", "eletrico"),
("miraidon", "eletrico"),
("charmander", "fogo"),
("fuecoco", "fogo"),
("miraidon", "eletrico"),
("pidgeotto", "voador"),
("butterfree", "voador"),
("butterfree", "voador"),
("fuecoco", "fogo");

create table tb_pomekon_eletrico
(
	id_pokemon int primary key not null ,
    nome_pokemon varchar(25) not null,
    tipo_pokemon varchar(35) not null
);
create table tb_pokemon_fogo
(
	id_pokemon int primary key not null ,
    nome_pokemon varchar(25) not null,
    tipo_pokemon varchar(35) not null
);
create table tb_pokemon_voador
(
	id_pokemon int primary key not null ,
    nome_pokemon varchar(25) not null,
    tipo_pokemon varchar(35) not null
);

