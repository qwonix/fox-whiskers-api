alter table dish_details drop column image_data_id;

alter table image_data drop column id;
alter table image_data add primary key (original_file_name);
alter table image_data rename column original_file_name to file_name;

alter table dish_details add column image_name varchar(255) references image_data;