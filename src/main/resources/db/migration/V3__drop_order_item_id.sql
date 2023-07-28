alter table order_item drop column id;

alter table order_item add primary key (order_id, dish_id);