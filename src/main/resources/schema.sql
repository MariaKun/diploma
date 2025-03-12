create TABLE if not exists users(
	id SERIAL PRIMARY key,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    email VARCHAR(50)
);

create TABLE if not exists files(
	id SERIAL PRIMARY key,
    filename VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    size BIGINT NOT NULL,
    filecontent BIGINT not null,
    userid INTEGER REFERENCES USERS (id)
);
