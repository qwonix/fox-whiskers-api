alter table "order" drop column client_id;
alter table client drop column id;
alter table client add column id uuid primary key;
alter table "order" add column client_id uuid references client(id);