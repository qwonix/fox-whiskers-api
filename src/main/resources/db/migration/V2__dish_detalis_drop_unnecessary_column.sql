alter table dish_details drop column dish_id;
alter table dish_details add foreign key (id) references dish(id);
alter table dish_details rename column id to dish_id;
